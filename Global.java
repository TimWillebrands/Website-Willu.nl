import models.PieceImage;
import models.Piece;
import play.*;
import play.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.String;
import java.net.URL;
import java.net.MalformedURLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.commons.io.IOUtils;

public class Global extends GlobalSettings {

    private String DBUrl = "http://pastebin.com/raw.php?i=MdL3TeQg";
    private Locale dutchLoc = new Locale("nl", "NL");

    public String getDatabase() throws MalformedURLException, IOException{
        InputStream in = new URL(DBUrl).openStream();

        try {
            String db = IOUtils.toString( in );
            return db;
            //return ""
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public void onStart(Application app) {
        Logger.info("Application has started");
        JSONParser parser = new JSONParser();

        try{
            String db=getDatabase();
            JSONArray jsonDb = (JSONArray) parser.parse(db);
            Iterator<JSONObject> iterator = jsonDb.iterator();

            while (iterator.hasNext()) {
                JSONObject obj = iterator.next();

                String pieceName = (String) obj.get("Name");
                Piece parentPiece;

                List<Piece> pieces = Piece.find
                        .where()
                        .eq("name",pieceName)
                        .findList();


                if(pieces.isEmpty()){
                    parentPiece = new Piece();
                    parentPiece.name = pieceName;
                    parentPiece.addeddate = (String) obj.get("Date");
                    parentPiece.description = (String) obj.get("Desc");
                    parentPiece.kind = (String) obj.get("Kind");
                    parentPiece.thumbnail = (String) obj.get("Thumbnail");
                }else{
                    parentPiece = pieces.get(0);
                    parentPiece.addeddate = (String) obj.get("Date");
                    parentPiece.description = (String) obj.get("Desc");
                    parentPiece.kind = (String) obj.get("Kind");
                    parentPiece.thumbnail = (String) obj.get("Thumbnail");
                }

                JSONArray images = (JSONArray) obj.get("Images");
                Iterator<JSONObject> imgIterator = images.iterator();
                List<PieceImage> imagesOfPiece = new ArrayList<>();
                while (imgIterator.hasNext()) {
                    JSONObject image = (JSONObject) imgIterator.next();

                    List<PieceImage> imgCheck = PieceImage.find
                            .where()
                            .eq("url",image.get("Url"))
                            .findList();

                    PieceImage img;
                    if(imgCheck.isEmpty()){
                        img = new PieceImage();
                        img.name = (String) image.get("Name");
                        img.description = (String) image.get("Desc");
                        img.focus = ((String) image.get("FocusX")+"% "+(String)image.get("FocusY")+"%").replace("=", "");
                        img.url = (String) image.get("Url");
                        imagesOfPiece.add(img);
                    }else{
                        img = imgCheck.get(0);
                        img.name = (String) image.get("Name");
                        img.description = (String) image.get("Desc");
                        img.focus = ((String) image.get("FocusX")+"% "+(String)image.get("FocusY")+"%").replace("=", "");
                        imagesOfPiece.add(img);
                    }
                    //img.save();
                }
                parentPiece.setImages(imagesOfPiece);
                parentPiece.save();
            }

        }catch (MalformedURLException ex){
            Logger.info(ex.getMessage());
        } catch (FileNotFoundException e) {
            Logger.info(e.getMessage());
        } catch (IOException e) {
            Logger.info(e.getMessage());
        } catch (ParseException e) {
            Logger.info(e.getMessage());
        }
    }
}