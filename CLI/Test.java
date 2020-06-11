import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader;
import java.io.File;
import org.jline.builtins.Nano;
import java.util.Scanner;

public class Test {
public static void main (String[] args) throws IOException {
File temp = File.createTempFile("Uppaal", "");
LineReader reader = LineReaderBuilder.builder().build();
BufferedWriter writer= new BufferedWriter(new FileWriter(temp));
writer.write("toto");
writer.close();
Nano nano = new Nano(reader.getTerminal(), temp);
nano.open(temp.getAbsolutePath());
nano.run();
nano.open(temp.getAbsolutePath());
nano.run();
Scanner scanner = new Scanner(temp);
scanner.useDelimiter("\\Z");
System.out.println(scanner.next());
}
}
