import java.text.DecimalFormat;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Test {

	public static final String xmlFilePath = "test.xml";

	public static void main(String argv[]) {
	DecimalFormat format = new DecimalFormat("0.00E0");
	System.out.println(format.format(0.00001));
	System.exit(1);
}
}