package controllers;

import models.Piece;
import models.PieceImage;
import service.GoogleDriveHandler;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import play.Logger;
import play.api.templates.Html;
import play.libs.Akka;
import play.libs.F.Promise;
import play.libs.F.Function;
import play.mvc.Controller;
import play.mvc.Http.RequestBody;
import play.mvc.Result;
import play.mvc.Results;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import securesocial.core.OAuth2Info;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.Drive.Files.*;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.*;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class Admin extends Controller{
	static String admin = "rianne.huijs@gmail.com";
	static GoogleDriveHandler driveHandler;
	static Boolean driveHandlerSet = false;
	
	private static String folderName;
	
	@SecureSocial.SecuredAction
	public static Result admin(){
		Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
		String email = user.email().isDefined() ? user.email().get() : "Not available";
				
		if(email.equals(admin)){
			if(driveHandlerSet){
				return ok( views.html.main.render(views.html.admin.admin.render(true),Application.getMeta("admin")) );
			}else
				return ok( views.html.main.render(views.html.admin.admin.render(false),Application.getMeta("admin")) );
		}
		return forbidden();
	}
	
	public static String getFolderJson(){ // Make async because this sucks
		if(driveHandlerSet){
			try{
				return driveHandler.folderToJson(driveHandler.findFolderId(folderName));
			}catch(IOException ex){
				return ex.getLocalizedMessage();
			}
		}else
			return "forbidden";
	}

	@SecureSocial.SecuredAction
	public static Result getDrivePieces(String foldername) {
		folderName = foldername;
		
		Promise<String> promiseOfString = play.libs.Akka.future(
			new Callable<String>() {
				public String call() {
					return getFolderJson();
				}
			}
		);
		
		return async(
			promiseOfString.map(
				new Function<String,Result>() {
					public Result apply(String i) {
						return ok(i).as("application/json");
					}
				}
			)
		);
	}

	@SuppressWarnings("unchecked")
	@SecureSocial.SecuredAction
	public static Result getDbJson(){
		JSONArray json = new JSONArray();
		Iterator<Piece> allPieces = Piece.find.all().iterator();
		while(allPieces.hasNext()){
			json.add(allPieces.next().getJson());
		}
		return ok(json.toJSONString()).as("application/json");
	}
	
	@SecureSocial.SecuredAction
	public static Result addJsonDb(){
		ArrayNode pieces = (ArrayNode) ctx().request().body().asJson();
		Iterator<JsonNode> jsonItr = pieces.getElements();
		while(jsonItr.hasNext()){
			ObjectNode piece = (ObjectNode)jsonItr.next();
			Logger.info(piece.toString());
			addDbObject(piece);
		}
		return ok();
	}
	
	@SecureSocial.SecuredAction
	public static Result replaceDb(){
		ArrayNode pieces = (ArrayNode) ctx().request().body().asJson();
		
		Iterator<Piece> allPieces = Piece.find.all().iterator();
		while(allPieces.hasNext()){
			allPieces.next().delete();
		}
		
		Iterator<JsonNode> jsonItr = pieces.getElements();
		while(jsonItr.hasNext()){
			ObjectNode piece = (ObjectNode)jsonItr.next();
			Logger.info(piece.toString());
			addDbObject(piece);
		}
		return ok();
	}
	
	@SecureSocial.SecuredAction
	public static Result addDbObject(){
		return addDbObject((ObjectNode)ctx().request().body().asJson());
	}
	
	@SuppressWarnings("unchecked")
	@SecureSocial.SecuredAction
	public static Result addDbObject(ObjectNode json){

		ArrayNode images = (ArrayNode) json.get("images");
		

        Piece piece = new Piece();
        piece.name = json.get("Name").getTextValue();
        piece.addeddate = json.get("Date").getTextValue();
        piece.description = json.get("Desc").getTextValue();
        piece.kind = json.get("Kind").getTextValue();
        try{
        	piece.thumbnail = json.get("Thumb").getTextValue().replace("&amp;", "&");
        }catch(NullPointerException ex){
        	piece.thumbnail = json.get("Thumbnail").getTextValue().replace("&amp;", "&");
        }
        
        Iterator<JsonNode> imgIterator = images.getElements();
        List<PieceImage> imagesOfPiece = new ArrayList<>();
        while (imgIterator.hasNext()) {
        	ObjectNode image = (ObjectNode) imgIterator.next();

            List<PieceImage> imgCheck = PieceImage.find
                    .where()
                    .eq("url",image.get("Url").getTextValue().replace("&amp;", "&"))
                    .findList();

            PieceImage img;
            if(imgCheck.isEmpty()){
                img = new PieceImage();
                img.name = image.get("Name").getTextValue();
                img.description = image.get("Desc").getTextValue();
                try{
                	img.focus = (image.get("FocusX").getTextValue()+"% "+image.get("FocusY").getTextValue()+"%");
                }catch(NullPointerException ex){
                	img.focus = image.get("Focus").getTextValue();
                }
                img.setUrl(image.get("Url").getTextValue().replace("&amp;", "&"));
                
                //img.save();
                imagesOfPiece.add(img);
            }else{
                img = imgCheck.get(0);
                img.name = image.get("Name").getTextValue();
                img.description = image.get("Desc").getTextValue();
                try{
                	img.focus = (image.get("FocusX").getTextValue()+"% "+image.get("FocusY").getTextValue()+"%");
                }catch(NullPointerException ex){
                	img.focus = image.get("Focus").getTextValue();
                }
                Logger.info("Image was already in the database, updated info");
                //img.update();
                imagesOfPiece.add(img);
            }
            //img.save();
        }
        piece.setImages(imagesOfPiece);
        
        piece.save();
        
		return ok();
	}
	
	@SecureSocial.SecuredAction
	public static Result getAuthCode(){
		return ok(GoogleDriveHandler.getAuthUrl());
	}

	@SecureSocial.SecuredAction
	public static Result receiveAuthCode(){
		driveHandler = new GoogleDriveHandler(request().getQueryString("code"));
		driveHandlerSet = true;
		
		Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
		String email = user.email().isDefined() ? user.email().get() : "Not available";
				
		if(email.equals(admin)){
			if(driveHandlerSet){
				return ok( views.html.main.render(views.html.admin.admin.render(true),Application.getMeta("admin")) );
			}else
				return ok( views.html.main.render(views.html.admin.admin.render(false),Application.getMeta("admin")) );
		}
		return forbidden();
	}

}
