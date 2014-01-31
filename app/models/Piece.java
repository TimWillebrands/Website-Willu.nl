package models;

import javax.persistence.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import play.db.ebean.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Piece extends Model{

    /**
	 * 
	 */
	private static final long serialVersionUID = 2442741811145760358L;


	@Id
    @Constraints.Required
    public Long id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String description;

    @Constraints.Required
    public String kind;

    @Constraints.Required
    public String addeddate;

    @Constraints.Required
    public String thumbnail;
    
	@SuppressWarnings("unchecked")
	public JSONObject getJson(){
    	JSONObject json = new JSONObject();
    	JSONArray images = new JSONArray();
    	json.put("Name", name);
    	json.put("Desc", description);
    	json.put("Kind", kind);
    	json.put("Date", addeddate);
    	json.put("Thumb", thumbnail);
    	java.util.Iterator<PieceImage> imgItr = getImages().iterator();
    	while(imgItr.hasNext()){
    		JSONObject img = new JSONObject();
    		PieceImage pImg = imgItr.next();
    		img.put("Name", pImg.name);
    		img.put("Focus", pImg.focus);
    		img.put("Url", pImg.url);
    		img.put("Desc", pImg.description);
    		images.add(img);
    	}
    	json.put("images",images);
    	return json;
    }
	
	public void updateWithJson(JSONObject json){
    	this.name = json.get("Name").toString();
    	this.description = json.get("Desc").toString();
    	this.kind = json.get("Kind").toString();
    	this.addeddate = json.get("Date").toString();
    	try{
    		this.thumbnail = json.get("Thumb").toString().replace("&amp;", "&");
        }catch(NullPointerException ex){
        	this.thumbnail = json.get("Thumbnail").toString().replace("&amp;", "&");
        }

    	for(PieceImage image : this.images){
    		image.delete();
    	}
    	    	
    	for(Object imageObj : ((JSONArray) json.get("images")).toArray()){
    		JSONObject image = (JSONObject) imageObj;
    		PieceImage img = new PieceImage();
    		img.name = image.get("Name").toString();
    		img.description = image.get("Desc").toString();
    		img.setUrl(image.get("Url").toString().replace("&amp;", "&"));
    		try{
            	img.focus = (image.get("FocusX").toString()+"% "+image.get("FocusY").toString()+"%");
            }catch(NullPointerException ex){
            	img.focus = image.get("Focus").toString();
            }
    		this.images.add(img);
    	}
    	this.save();
    }

    @OneToMany(cascade=CascadeType.ALL)
    public List<PieceImage> images = new ArrayList<>();

    public List<PieceImage> getImages() {
        return images;
    }

    public void setImages(List<PieceImage> images) {
        this.images = images;
    }

    public static Model.Finder<Long,Piece> find = new Model.Finder<>(
            Long.class, Piece.class
    );
}
