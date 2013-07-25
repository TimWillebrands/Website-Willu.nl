package models;

import javax.persistence.*;

import play.Logger;
import play.db.ebean.*;
import play.data.validation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tim
 * Date: 12-7-13
 * Time: 14:27
 * To change this template use File | Settings | File Templates.
 */

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

    public String thumbnail;

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
