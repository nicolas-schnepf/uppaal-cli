import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.File;

public class Test {
public static void main (String[] args) throws Exception {

// create the document builder and parse the xml options

DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
DocumentBuilder builder = factory.newDocumentBuilder();
Document document = builder.parse(new File("options.xml"));

// retrieve the information from the document

Element root = document.getDocumentElement();
NodeList options = root.getChildNodes();

for (int i = 0;i<options.getLength();i++) {

	Element option = (Element)options.item(i);
	System.out.println("name = " + option.getAttribute("name")+
	"\ntype = "+option.getAttribute("type")
	+"\ndefault = "+option.getAttribute("default")
	+ "\ndisplay = "+option.getAttribute("display") + "\n");

	switch (option.getAttribute("type")) {
		case "choice":
		NodeList choices = option.getChildNodes();
		for (int j = 0;j<choices.getLength();j++) {
			Element choice = (Element) choices.item(j);
			System.out.println("name: "+choice.getAttribute("name")
			+"\ndisplay: "+ choice.getAttribute("display"));
		}
		break;

		case "parameterset":
		NodeList parameters = option.getChildNodes();
		for (int j = 0;j<parameters.getLength();j++) {
			Element parameter = (Element) parameters.item(j);
			System.out.println("name: "+parameter.getAttribute("name")
			+"\ndisplay: "+ parameter.getAttribute("display")
			+"\ntype: "+ parameter.getAttribute("type")
			+"\nrangemin: "+ parameter.getAttribute("rangemin")
			+"\nrangemax: "+ parameter.getAttribute("rangemax")
			+"\ndefault: "+ parameter.getAttribute("default")
			+"\nfracmin: "+ parameter.getAttribute("fracmin")
			+"\nfracmax: "+ parameter.getAttribute("fracmax")+"\n"
			);
		}
		break;
		}
}
}
}