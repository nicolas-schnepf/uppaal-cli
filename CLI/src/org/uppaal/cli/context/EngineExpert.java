package org.uppaal.cli.context;

/**
* engine expert handling the connection and the disconnection to the uppaal binary
*/

import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.EngineStub;
import java.io.IOException;

public class EngineExpert extends AbstractExpert {
// the engine of this expert
private Engine engine;

public EngineExpert (Context context) {
	super(context);
	this.engine = null;
}


/***
* @return the engine attached to this context
*/

public Engine getEngine () {
	return this.engine;
}

/***
* connect to an uppaal engine based on the path declared in the UPPAALPATH environment variable
*/

public void connectEngine() throws EngineException, IOException {

// compute the path to the engine binary based on the UPPAALPATH environment variable

	String os = System.getProperty("os.name");
		String here = System.getenv("UPPAALPATH");
	String path = null;

// compute the end of the path based on the running operating system

	if ("Linux".equals(os)) {
		path = here+"/bin-Linux/server";
	} else if ("Mac OS X".equals(os)) {
		path = here + "bin-Darwin/server";
	} else {
		path = here+"\\bin-Windows\\server.exe";
	}

// create and configure the new engine

	this.engine = new Engine();
	this.engine.setServerPath(path);
	this.engine.setServerHost("localhost");
	this.engine.setConnectionMode(EngineStub.BOTH);
	this.engine.connect();
	this.context.getOptionExpert().loadOptions();
}

/***
* disconnect the engine of the uppaal console
*/

public void disconnectEngine () {
	if (this.engine == null) return;
	this.engine.disconnect();
	this.engine = null;
}
}
