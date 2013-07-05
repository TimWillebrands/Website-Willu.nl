package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.*;

public class Application extends Controller {
  
    public static Result index() {
        return ok(
                views.html.index.render("Your new application isasd ready.")
        );
    }
  
}
