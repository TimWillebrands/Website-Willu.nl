package models;

import javax.persistence.*;

import play.Logger;
import play.db.ebean.*;
import play.data.validation.*;
import service.ThumbnailFactory;

@Entity
public class PieceImage extends Model {

	private static final long serialVersionUID = -5794045853838851482L;

	@Id
    @Constraints.Required
    public Long id;

    @Constraints.Required
    public String name;

    @Constraints.Required
    public String description;

    @Constraints.Required
    public String focus;

    @Constraints.Required
    public String url;

    @Constraints.Required
    public String thumbnail;

    @ManyToOne
    @Constraints.Required
    public Piece piece;
    
    public void setUrl(final String url){
    	this.url = url;
    	this.thumbnail = "public/images/thumbnail_"+this.name.replace(" ", "_")+".png";
    	
    	ThumbnailFactory.createThumbnail(name.replace(" ", "_"), url);
    }

    public static Finder<Long,PieceImage> find = new Finder<>(
            Long.class, PieceImage.class
    );

}