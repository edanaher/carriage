package carriage;

import java.util.stream.Stream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class Assignment {
  String name;

  public Assignment(String name) {
    this.name = name;
  }

  public static Assignment[] list() {
    try {
      Stream<Path> dirs = Files.list(Paths.get("assignments"));
      return dirs.map(path -> new Assignment(path.getFileName().toString())).toArray(Assignment[]::new);
    } catch (java.io.IOException ex) {
      return new Assignment[]{};
    }
  }


}
