package org.jline.builtins;

import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.Terminal;
import org.jline.builtins.Options;
import org.jline.builtins.Nano;

import java.util.ArrayList;
import org.jline.reader.ConfigurationPath;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

/**
* extends the jline nano editor in order to load the content of a file without providing it to the buffer
*/

public class PropertyEditor extends Nano {

	public PropertyEditor(Terminal terminal, File root) {
		super(terminal, root.toPath());
	}

	public PropertyEditor(Terminal terminal, Path root) {
		super(terminal, root, null);
	}

	public PropertyEditor(Terminal terminal, Path root, Options opts) {
		super(terminal, root, opts, null);
	}

	public PropertyEditor(Terminal terminal, Path root, Options opts, ConfigurationPath configPath) {
		super(terminal, root, opts, configPath);
	}

public void run () throws IOException {
	Buffer buffer = new Buffer(null);
	buffer.lines = new ArrayList<>();
	buffer.lines.add("");

	buffer.charset = Charset.defaultCharset();
	buffer.computeAllOffsets();
	FileInputStream fis = new FileInputStream (this.root.toString());
	buffer.read(fis);

	this.buffers.add(buffer);
	super.run();
}
}