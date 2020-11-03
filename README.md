CAR Repl.It Auto-Grader of Excellence
=====================================

A tool with a silly name designed to be used on repl.it for autograding student assignments, along with the
[Carriage template project](https://github.com/edanaher/carriage-template)..

Originally based on the [Repl.it suggested autograder](https://docs.repl.it/Teams/CentralizedAutograder-java),
it has diverged quite a bit and should be significantly more robust and easy to use.

Installing
----------
The Repl.it "version control" integration seems a bit flaky.  So ignore it and use manual git commands in the
console:

```
rm Main.java
git clone https://github.com/edanaher/carriage/ .
```

Run it once to install all the dependencies, and you should be ready to add an assignment.


Creating a new assignment
-------------------------
To create a new assignment, simply create a new directory and test file under assignments.  This should use
JUnit to run some automated tests; see the two examples (Addition and ForLoopExample) for guidance.

Don't forget to change the name of the class inside the file to match the filename.

Once the Test file exists, you should be able to submit files matching that filename using the template
project.  For example, if you created `assignments/NewProject/NewProjectTest.java`, you can create a new
Repl.it project based off of the carriage template with NewProject.java (and ASSIGNMENT set to NewProject) in
the .replit in that project), and submission should Just Work.

Editing a assignment's tests
----------------------------

Just edit the ...Test.java file.  You don't need to restart the server; it will automatically grade every
assignment against the newest test.

Note that this means that if you edit a test mid-assignment, students will get the newer version immediately.
So be careful with this!

Running an assignment's tests
-----------------------------

There's no automated mechanism yet, but if you put a solution (or starter code) in the directory for an
assignment, you can run the following in the console to compile and run it to see if the tests work:

```
cd assignments/AssignmentName
javac -cp  ".:../../target/runtime-dependencies/*" AssignmentName.java AssignmentNameTest.java 
java -jar ../../target/runtime-dependencies/junit-platform-console-standalone-1.7.0.jar -cp ".:../../target/runtime-dependencies/*" -c AssignmentNameTest
```

Potential future features
-------------------------
In no particular order:

- Better error handling.  There's probably still room for improvement.
- General code cleanup.  This is still borderline prototype-quality code, aiming to be functional, not pretty.
- (Per-project?) configurable output for students: Should they get "submission successful", "3/4 tests
passed", "thisOneTest failed", or "thisOneTest failed with expected output '123'".
- Multi-file projects: it should be possible to zip up the contents on submission and unzip them here.  There
are some nonobvious questions around how to determine main function, etc.
- Better analytics on student submissions, rather than just report.md.
- Better testing for tests; e.g., allowing a solution to be created with the tests for an assignment, and an
admin UI that can run the solutiosn against the tests and verify that they match.
- Better organization of submissions and reports
  - Have a report for each project/student (or real UI based on database)
  - Conveniently track each student's latest submission
- Better parser of errors to handle other types of failed assertions and exceptions.
- Sort tests in the reports so they're sanely ordered.  (Or make Junit run the in order?)
