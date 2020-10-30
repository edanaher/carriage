import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AdditionTest {

  @Test
  void add_zeros() {
    assertEquals(0, Addition.add(0, 0));;
  }

  @Test
  void increment_10() {
    assertEquals(11, Addition.add(10, 1));
  }

  @Test
  void ten_plus_twenty() {
    assertEquals(30, Addition.add(10, 20));
  }

  @Test
  void ten_plus_minus_one() {
    assertEquals(9, Addition.add(10, -1));
  }


}
