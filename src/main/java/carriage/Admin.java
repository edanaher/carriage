package carriage;

import java.util.HashMap;
import carriage.Assignment;

public class Admin {
  public Admin() {
  }

  public void render(io.javalin.http.Context ctx) {
     ctx.contentType("text/html");
     HashMap data = new HashMap<String, Assignment>();

     Assignment[] assignments = Assignment.list();
     data.put("assignments", assignments);


     ctx.render("/admin.mustache", data);
  }
}
