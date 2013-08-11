package models;

import javax.persistence.*;

import play.db.ebean.*;
import play.data.validation.*;

@SuppressWarnings("serial")
@Entity
public class PieceImage extends Model {

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

    @ManyToOne
    @Constraints.Required
    public Piece piece;

    public static Finder<Long,PieceImage> find = new Finder<>(
            Long.class, PieceImage.class
    );

}