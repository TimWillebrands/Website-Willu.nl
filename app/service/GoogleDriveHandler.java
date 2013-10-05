package service;

//import com.google.api.client.http.FileContent;
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
import com.google.api.services.drive.model.File;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import play.Logger;
import play.Play;

public class GoogleDriveHandler {
	private Drive drive;
	
	private static GoogleAuthorizationCodeFlow flow = null;
	private static HttpTransport httpTransport = null;
	private static JsonFactory jsonFactory = null;
	
	private static String CLIENT_ID = "595172328396-t7na0jpiga8nu0pbn3ju81qkt18svt74.apps.googleusercontent.com";
	private static String CLIENT_SECRET = "LB1aYiBXch1ywpyTcxT1aoN2";
	//private static String REDIRECT_URI = "http://web.willu.eu.cloudbees.net/oauth2callback";//production mode
	private static String REDIRECT_URI = "http://willu.nl/oauth2callback";//production mode
	
	private static void setRedirectUri(){
		REDIRECT_URI = Play.isDev() ? "http://localhost:8080/oauth2callback" : "http://willu.nl/oauth2callback";
	}
	
	public static String getAuthUrl(){
		setRedirectUri();
        httpTransport = new NetHttpTransport();
        jsonFactory = new JacksonFactory();
        flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();
        
        return flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
	}
	
	public GoogleDriveHandler(String code){
		setRedirectUri();
		GoogleTokenResponse response;
		try {
			response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();
		    GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
		    drive = new Drive.Builder(httpTransport, jsonFactory, credential).build();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve a list of permissions.
	 *
	 * @param service Drive API service instance.
	 * @param fileId ID of the file to retrieve permissions for.
	 * @return List of permissions.
	*/
	public java.util.List<Permission> retrievePermissions(String fileId) {
	    try {
	      PermissionList permissions = drive.permissions().list(fileId).execute();
	      return permissions.getItems();
	    } catch (IOException e) {
	      System.out.println("An error occurred: " + e);
	    }
	
	    return null;
	}
	
	public String findFolderId(String fileName) throws IOException {
        Drive.Files.List request = drive.files().list()
                .setQ("mimeType='application/vnd.google-apps.folder' " +
                        "and trashed=false " +
                        "and title='" + fileName + "'");

        Iterator<File> fileIter = request.execute().getItems().iterator();
        while ( fileIter.hasNext() ){
        	File file = fileIter.next();
        	Iterator<Permission> permIter = retrievePermissions(file.getId()).iterator();
        	while ( permIter.hasNext() ){
        		Permission perm = permIter.next();
        		if (perm.getId().equals( drive.about().get().execute().getPermissionId() )){
        			return file.getId();
        		}
        	}
        }
        return request.execute().getItems().get(0).getId();
    }

	public String findFileId(String fileName) throws IOException {
		Drive.Files.List request = drive.files().list()
                .setQ("trashed=false " +
                        "and title='" + fileName + "'");
        FileList files = request.execute();
        return files.getItems().get(0).getId();
    }

    @Deprecated
    public String createDownloadLink(String link){
        for(int i =0; i < link.length(); i++)
            if(link.charAt(i) == '=')
                return link.substring(0,i);
        return link;
    }

    public File renameFile(String fileId, String newTitle) {
        try {
            File file = new File();
            file.setTitle(newTitle);

            // Rename the file.
            Drive.Files.Patch patchRequest = drive.files().patch(fileId, file);
            patchRequest.setFields("title");

            File updatedFile = patchRequest.execute();
            return updatedFile;
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
            return null;
        }
    }

    private String downloadFileContent(File file) {

        InputStream is;

        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
            try {
                HttpResponse resp =
                        drive.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
                                .execute();
                is = resp.getContent();
            } catch (IOException e) {
                // An error occurred.
                e.printStackTrace();
                return null;
            }
        } else {
            // The file doesn't have any content stored on Drive.
            return null;
        }

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();
    }

    private String getField(String str,String fieldName){
    	String returnval;
    	try{
            int start = str.lastIndexOf(";"+fieldName+"=");
            returnval = str.substring(start+fieldName.length()+2 , str.indexOf(';',start+1));
    	}catch(java.lang.StringIndexOutOfBoundsException ex){
    		returnval = "MISLUKS: StringIndexOutOfBoundsException :O";
    	}
    	return returnval;
    }

    @SuppressWarnings("unchecked")
    public String folderToJson(String folder) throws IOException {
        Drive.Children.List request = drive.children().list(folder);

        JSONParser parser = new JSONParser();
        JSONArray jsonDb = new JSONArray();

        do {
            try {
                ChildList children = request.execute();

                for (ChildReference piece : children.getItems()) {       // Get items in pieces folder
                    //File pieceFile = drive.files().get(piece.getId()).execute();
                    ChildList pieceList = drive.children().list(piece.getId()).execute();

                    JSONObject jsonPiece = new JSONObject();
                    JSONArray imagesOfPiece = new JSONArray();

                    for (ChildReference child : pieceList.getItems()) {  // Get items in specific piece folder
                        File childFile = drive.files().get(child.getId()).execute();

                        String ext = childFile.getFileExtension();
                        
                        Logger.info(childFile.getTitle());

                        if (childFile.getTitle().equals("conf.json")){
                            JSONObject jsonObject = (JSONObject) parser.parse(downloadFileContent(childFile));
  
                            jsonPiece.put("Name",jsonObject.get("name"));
                            jsonPiece.put("Kind",jsonObject.get("kind"));
                            jsonPiece.put("Date",jsonObject.get("date"));
                            jsonPiece.put("Desc",jsonObject.get("desc"));
                        }else if(childFile.getTitle().substring(0,9).toLowerCase().equals("thumbnail")){
                            jsonPiece.put("Thumbnail","https://drive.google.com/uc?export=download&id=" + childFile.getId().toString());
                        }else{
                            if((ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg")) && !childFile.getTitle().substring(0,9).toLowerCase().equals("thumbnail")){
                                JSONObject image = new JSONObject();
                                image.put("Name",getField(childFile.getTitle(),"name"));
                                image.put("Desc",getField(childFile.getTitle(),"desc"));
                                image.put("FocusX",getField(childFile.getTitle(),"x"));
                                image.put("FocusY",getField(childFile.getTitle(),"y"));
                                image.put("Url","https://drive.google.com/uc?export=download&id=" + childFile.getId().toString());
                                imagesOfPiece.add(image);
                            }
                        }
                    }

                    jsonPiece.put("Images",imagesOfPiece);
                    jsonDb.add(jsonPiece);
                }

                request.setPageToken(children.getNextPageToken());
            } catch (IOException e) {
                System.out.println("An error occurred: " + e);
                request.setPageToken(null);
            } catch (ParseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);

        return jsonDb.toJSONString();
    }

}
