import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.File;

import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.PrototypeDocument;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.SetPropertyCommand;
import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.engine.Problem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.net.MalformedURLException;
import java.net.URL;


public class Test {
public static void main (String[] args) throws Exception {
com.uppaal.model.io2.XMLReader.setXMLResolver(new com.uppaal.model.io2.UXMLResolver());
	PrototypeDocument doc_loader = new PrototypeDocument();
		//URL location = new URL("file", null, args[0]);
	URL location = new URL("file://localhost"+System.getProperty("user.dir")+"/train-gate.xml");
	Document document = doc_loader.load(location);
	document.save("queries.q");
}
}