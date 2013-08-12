package controllers;

import models.Piece;
import models.PieceImage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Iterator;
import java.util.List;

public class Application extends Controller {

    public static Result werk() {
        List<Piece> allPieces = Piece.find.all();
        return ok(
                views.html.werk.render(allPieces)
        );
    }
    
    public static Result getSubsite(String name){
    	Logger.info(name);
    	
    	if(name.equals("Sieraden")){
    		List<Piece> allPieces = Piece.find.all();
            return ok(views.html.werk.render(allPieces));
    	}else if(name.equals("Accessoires")){
    		List<Piece> allPieces = Piece.find.all();
            return ok(views.html.werk.render(allPieces));
    	}else if(name.equals("Willu")){
            return ok(views.html.willu.render());
    	}else if(name.equals("Contact")){
	        return ok(views.html.cont.render());
		}
    	
    	List<Piece> allPieces = Piece.find.all();
        return ok(views.html.werk.render(allPieces)); 
    }

    @SuppressWarnings("unchecked")
	public static Result getItem(Long pieceId) {

        Piece piece = Piece.find.byId(pieceId);
        JSONObject jsonPiece = new JSONObject();
        JSONArray images = new JSONArray();
        jsonPiece.put("name",piece.name);
        jsonPiece.put("desc",piece.description);
        jsonPiece.put("kind",piece.kind);
        Iterator<PieceImage> itr = piece.getImages().iterator();
        while(itr.hasNext()){
            PieceImage image = itr.next();
            JSONObject img = new JSONObject();
            img.put("name",image.name);
            img.put("focus",image.focus.replace("=", ""));
            img.put("url",image.url);
            images.add(img);
        }
        jsonPiece.put("images",images);

        return ok(jsonPiece.toJSONString());
    }
  
}
