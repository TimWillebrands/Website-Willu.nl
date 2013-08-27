package models;

import javax.persistence.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import play.Logger;
import play.db.ebean.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tim
 * Date: 12-7-13
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */

@SuppressWarnings("serial")
@Entity
public class Piece extends Model{

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
