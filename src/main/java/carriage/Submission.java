package carriage;

import io.javalin.core.util.FileUtil;
import io.javalin.http.UploadedFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import carriage.Submission;
import carriage.SubmissionException;


public class Submission {
  private static final Pattern TEST_OUTPUT_PATTERN = Pattern.compile("(\\d+) tests successful.*(\\d+) tests failed", Pattern.DOTALL);
  private static final Pattern TEST_FAILURE_PATTERN = Pattern.compile("JUnit Jupiter:[^:]*Test:(.*?)\\(\\).*?expected: <(.*?)> but was: <(.*?)>", Pattern.DOTALL);
  private static final String REPORT_TEMPLATE = "### %s submitted at %s\n* **Passed:** %d\n* **Failed:** %d\n* **Score:** %d%%\n";
  private static final String STUDENT_REPORT_TEMPLATE = "Submitted successfully for %s.\n* Passed: %d\n* Failed: %d\n* Score: %d%%\n";
  private static final String FAILURE_TEMPLATE = "* Failed test: %s\n  Expected: <%s>\n  Actual:   <%s>\n";
  private static final String STUDENT_FAILURE_TEMPLATE = "* Failed test: %s\n";
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd MMMMMMMMMM yyyy hh:mma z");
  private static String OS = System.getProperty("os.name").toLowerCase();

  private String assignmentName;
  private String workspace;
  private String testOutput;

  static {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("America/New_York"));
  }

  public Submission(String an, String ws) {
    assignmentName = an;
    workspace = ws;
  }

  // copy the uploaded file and any other files necessary for compilation and testing
  public void copy(UploadedFile upload) throws Exception {
    // System.err.println("Copying in " + workspace);

    // copy the submission to the work directory
    String submission = Paths.get(workspace, upload.getFilename()).toString();
    FileUtil.streamToFile(upload.getContent(), submission);

    // copy the test to the work directory
    String test = Paths.get(workspace, assignmentName + "Test.java").toString();
    Files.copy(new File("assignments/" + assignmentName + "/" + assignmentName + "Test.java").toPath(), new File(test).toPath());

    // copy the dependencies to the work directory
    // NOTE(edanaher): This used to be stored in target/classes/META-INF/lib and loaded as a resource, but
    // that directory has a tendency to disappear for no apparent reason on repl.it.  So just load them
    // directly from target/runtime-dependencies and hope that this is more stable.
    Stream<Path> dependencies = Files.list(Paths.get("target", "runtime-dependencies"));
    Stream<Boolean> copySuccesses = dependencies.map(path -> {
      try {
        Files.copy(path, Paths.get(workspace, path.getFileName().toString()));
        return true;
      } catch (IOException ex) {
         ex.printStackTrace(System.err);
         return false;
      }
    });
    // Throwing an exception inside a lambda is ugly.  So check that everything succeeded.
    if (copySuccesses.anyMatch(x -> {return !x; }))
      throw new SubmissionException("Error copying dependencies.  Please ask course staff to restart the autograder.");
  }

  public String compile() throws Exception {
    // System.err.println("Compiling in " + workspace);

    ProcessBuilder pb = new ProcessBuilder();
    if (OS.contains("win"))
      pb.command("javac", "-cp", ".;*", "*.java");
    else
      pb.command("javac", "-cp", ".:*", assignmentName + ".java", assignmentName + "Test.java");
    pb.directory(new File(workspace));
    pb.redirectErrorStream(true);
    Process process = pb.start();

    String result = new String(process.getInputStream().readAllBytes(), StandardCharsets.US_ASCII);
    process.waitFor();
    return result;
  }

  public String test() throws Exception {
    // System.err.println("Testing in " + workspace);

    ProcessBuilder pb = new ProcessBuilder();
    pb.command("java",
        "-jar", "junit-platform-console-standalone-1.7.0.jar",
        "-cp", ".",
        "-c", assignmentName + "Test",
        "--disable-ansi-colors", "--disable-banner",
        "--details=summary", "--details-theme=ascii"
    );
    pb.directory(new File(workspace));
    pb.redirectErrorStream(true);
    Process process = pb.start();

    String result = new String(process.getInputStream().readAllBytes(), StandardCharsets.US_ASCII);
    process.waitFor();
    testOutput = result;
    return result;
  }

  public void report() throws Exception {
    Matcher matcher = TEST_OUTPUT_PATTERN.matcher(testOutput);
    if (!matcher.find())
      throw new IllegalStateException("Unable to match test output:" + testOutput);

    int passed = Integer.parseInt(matcher.group(1));
    int failed = Integer.parseInt(matcher.group(2));

    int score = (int) ((((double) passed) / ((double) (passed + failed))) * 100);
    // System.err.println("score: " + score);

    String code = Files.readString(Paths.get(workspace, assignmentName + ".java"));
    String sn = extractStudentNumber(code);

    String date = DATE_FORMAT.format(new Date());

    String report = String.format(REPORT_TEMPLATE, sn, date, passed, failed, score);
    // System.err.println(report);

    Matcher failureMatcher = TEST_FAILURE_PATTERN.matcher(testOutput);
    while(failureMatcher.find()) {
      String testName = failureMatcher.group(1);
      String expected = failureMatcher.group(2);
      String actual = failureMatcher.group(3);
      report += String.format(FAILURE_TEMPLATE, testName, expected, actual);
    }


    save(sn, report, workspace);
  }


  public String studentReport() throws Exception {
    Matcher matcher = TEST_OUTPUT_PATTERN.matcher(testOutput);
    if (!matcher.find())
      throw new IllegalStateException("Unable to match test output");

    int passed = Integer.parseInt(matcher.group(1));
    int failed = Integer.parseInt(matcher.group(2));


    int score = (int) ((((double) passed) / ((double) (passed + failed))) * 100);
    // System.err.println("score: " + score);

    String code = Files.readString(Paths.get(workspace, assignmentName + ".java"));
    String sn = extractStudentNumber(code);

    String date = DATE_FORMAT.format(new Date());

    String report = String.format(STUDENT_REPORT_TEMPLATE, sn, passed, failed, score);
    // System.err.println(report);

    Matcher failureMatcher = TEST_FAILURE_PATTERN.matcher(testOutput);
    while(failureMatcher.find()) {
      String testName = failureMatcher.group(1);
      report += String.format(STUDENT_FAILURE_TEMPLATE, testName);
    }


    return report;
  }

  public void save(String sn, String report, String workspace) throws Exception {
    // slight sanity check on the directory to create...
    sn = sn.replaceAll("[^a-zA-Z0-9]", "_");

    File submissionsdir = new File("submissions/");
    if (!submissionsdir.exists())
      submissionsdir.mkdir();

    String basedirName = "submissions/" + assignmentName + "/";
    File basedir = new File(basedirName);
    if (!basedir.exists())
      basedir.mkdir();

    File dir = null;
    for (int i = 0; i < 10_000; i++) {
      dir = new File(basedirName + sn + ((i == 0) ? "" : ("_" + i)));
      if (!dir.exists())
        break;
    }
    if (dir.exists())
      throw new IllegalStateException("Can't create directory name to store submission");

    if (!dir.mkdir())
      throw new IllegalStateException("Can't create directory to store submission");

    Files.copy(Paths.get(workspace, assignmentName + ".java"), Paths.get(dir.getAbsolutePath(), assignmentName + ".java"));
    Files.writeString(Paths.get("report.md"), report, StandardCharsets.US_ASCII, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

    System.err.println("Submission received from " + sn + "\n" + report);
  }

  private static String extractStudentNumber(String codefile) throws SubmissionException {
    Pattern pattern = Pattern.compile("^//\\s*(\\S+)");
    Matcher matcher = pattern.matcher(codefile);
    if (matcher.find()) {
      String name = matcher.group(1);
      if (name.toLowerCase().equals("firstnamelastname"))
        throw new SubmissionException("Please set your name on the first line of the file");
      return name;
    }

    throw new SubmissionException("No student identifier found on first line");
  }

}
