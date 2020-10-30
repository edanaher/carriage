import io.javalin.Javalin;
import io.javalin.http.UploadedFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import carriage.Submission;
import carriage.SubmissionException;

public class Main {

  // ----------------------------------------------
  // you shouldn't need to edit anything below here
  // ----------------------------------------------


  public static void main(String[] args) {
    Javalin app = Javalin.create().start(8080);
    app.get("/", ctx -> {
         ctx.contentType("text/html");
         ctx.result(Main.class.getResourceAsStream("index.html"));
       })
       .post("/", ctx -> {
         try {
           UploadedFile upload = ctx.uploadedFile("codefile");
           if (upload == null)
             throw new SubmissionException("No file was uploaded");

           if (upload.getSize() > 10_000)
             throw new SubmissionException("Uploaded file is too large");

           String filename = upload.getFilename();
           if (!filename.endsWith(".java"))
             throw new SubmissionException("Uploaded file name must end in .java");

           String submissionName = filename.substring(0, filename.length() - ".java".length());

           File assignmentDir = new File("assignments/" + submissionName + "/");
           System.out.println(assignmentDir.toString());
           if (!assignmentDir.exists())
             throw new SubmissionException("Unknown assignment: " + submissionName);

           String temp = Files.createTempDirectory("autograder_test").toString();
           //new File("autograder_test").mkdirP);
           //String temp = "autograder_test";

           Submission submission = new Submission(submissionName);


           submission.copy(upload, temp);
           submission.compile(temp);
           String testOutput = submission.test(temp);
           submission.report(testOutput, temp);

           String studentRep = submission.studentReport(testOutput, temp, submissionName);
           ctx.contentType("text/plain");
           ctx.result(studentRep);
         } catch (SubmissionException ex) {
           ctx.contentType("text/plain");
           ctx.result("Error: " + ex.getMessage() + "\n");
         } catch (Exception ex) {
           ex.printStackTrace(System.err);

           ctx.contentType("text/plain");
           ctx.result("Sorry, something went wrong");
         }
       });
  }

  private static InputStream resource(String name) {
    return Main.class.getResourceAsStream(name);
  }

}
