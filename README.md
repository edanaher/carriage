CAR Repl.It Auto-Grader of Excellence
=====================================

A tool with a silly name designed to be used on repl.it for autograding student assignments, along with the
[Carriage template project](https://github.com/edanaher/carriage-template)..

Originally based on the [Repl.it suggested autograder](https://docs.repl.it/Teams/CentralizedAutograder-java),
it has diverged quite a bit and should be significantly more robust and easy to use.

Installing
----------
TODO once I've installed it once.

Creating a new assignment
-------------------------
To create a new assignment, simply create a new directory and test file under assignments.  This should use
JUnit to run some automated tests; see the two examples (Addition and ForLoopExample) for guidance.

Once the Test file exists, you should be able to submit files matching that filename using the template
project.  For example, if you created `assignments/NewProject/NewProjectTest.java`, you can create a new
Repl.it project based off of the carriage template with NewProject.java (and ASSIGNMENT set to NewProject) in
the .replit in that project), and submission should Just Work.

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
