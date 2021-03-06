import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


import java.io.PrintStream;
import java.io.ByteArrayOutputStream;


@TestMethodOrder(OrderAnnotation.class)
public class ForLoopPracticeTest {

private final PrintStream standardOut = System.out;
private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
 
@BeforeEach
public void setUp() {
    System.setOut(new PrintStream(outputStreamCaptor));
}

@Test
@Order(1)
void part_1_check_output_120() {
    //System.out.println("Running test");
    ForLoopPractice.oneToTwenty();
    String studentPrinted = outputStreamCaptor.toString();
    System.out.println("Student printed: " + studentPrinted);
    fail("The thingy failed");
    assertEquals("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20", studentPrinted.trim());
}


@Test
@Order(2)
void part_2_check_output_even120() {
    ForLoopPractice.evenOneToTwenty();
    String studentPrinted = outputStreamCaptor.toString();
    System.out.println("Student printed: " + studentPrinted);
    assertEquals("2 4 6 8 10 12 14 16 18 20", studentPrinted.trim());
}


@Test
@Order(3)
void part_3_check_output_sum120(){
  ForLoopPractice.sumOneToTwenty();
  String studentPrinted = outputStreamCaptor.toString();
  System.out.println("Student printed: "+ studentPrinted);
  assertEquals("210", studentPrinted.trim());
}
  
@Test
@Order(4)
void part_4_check_output_bla5times(){
  ForLoopPractice.blaFiveTimes();
  String studentPrinted = outputStreamCaptor.toString();
  System.out.println("Student printed: "+studentPrinted);
  assertEquals("BLA BLA BLA BLA BLA", studentPrinted.trim());
}

@Test 
@Order(5)
void part_5_check_output_120_noSpaceAtEnd() {
    ForLoopPractice.oneToTwenty();
    String studentPrinted = outputStreamCaptor.toString();
    System.out.println();
    System.out.println("************************************");
    System.out.println("Part V - Please only work on passing these next tests regarding\n eliminating the spaces after you've finished parts 1-4.");
    System.out.println("************************************");
    System.out.println();
    System.out.println("Student printed: " + studentPrinted);
    assertEquals("1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20", studentPrinted);
}

@Test
@Order(6)
void part_5_check_output_even120_noSpaceAtEnd() {
    ForLoopPractice.evenOneToTwenty();
    String studentPrinted = outputStreamCaptor.toString();
    System.out.println("Student printed: " + studentPrinted);
    assertEquals("2 4 6 8 10 12 14 16 18 20", studentPrinted.trim());
}

@Test
@Order(7)
void part_5_check_output_bla5times_noSpaceAtEnd(){
  ForLoopPractice.blaFiveTimes();
  String studentPrinted = outputStreamCaptor.toString();
  System.out.println("Student printed: "+studentPrinted);
  assertEquals("BLA BLA BLA BLA BLA", studentPrinted);
}


}
