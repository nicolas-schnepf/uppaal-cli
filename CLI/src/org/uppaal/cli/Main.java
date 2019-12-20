package org.uppaal.cli;

import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.EngineStub;
import com.uppaal.engine.Problem;
import com.uppaal.engine.QueryFeedback;
import com.uppaal.engine.QueryResult;
import com.uppaal.model.core2.Data2D;
import com.uppaal.model.core2.DataSet2D;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.PrototypeDocument;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryData;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.concrete.ConcreteTrace;
import com.uppaal.model.system.symbolic.SymbolicTrace;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main/entry class implementing the command line interface.
 * @author Marius Mikucionis <marius@cs.aau.dk>
 */
public class Main
{
    private boolean done = false;
    private PrintStream out = null;
    private Engine engine = null;
    private String version = null;
    private Document doc = null;
    private UppaalSystem system = null;
    
    public static File getUppaalPath(){
        String cp = System.getProperty("java.class.path");
        if (cp == null || cp.isBlank()) {
            System.err.println("Error: java.class.path is not set, cannot find uppaal.jar");
            System.exit(1);
        }
        String uppaal = null;
        for (String path: cp.split(":"))
            if (path.endsWith("uppaal.jar")) {
                uppaal = path;
                break;
            }
        if (uppaal == null) {
            System.err.println("Error: uppaal.jar not found in the java.class.path.");
            System.exit(1);
        }
        return new File(uppaal).getParentFile();
    }
    
    public void connect() throws EngineException, IOException
    {
        String os = System.getProperty("os.name");
        File path = getUppaalPath();
        if ("Linux".equals(os)) {
            path = new File(new File(path, "bin-Linux"), "server");
        } else if (os.startsWith("Mac")) {
            path = new File(new File(path, "bin-Darwin"), "server");
        } else if (os.startsWith("Windows")) {
            path = new File(new File(path, "bin-Windows"), "server.exe");
        } else {
            System.err.println("Unknown operating system.");
            System.exit(1);
        }
        engine = new Engine();
        engine.setServerPath(path.getPath());
        engine.setServerHost("localhost");
        engine.setConnectionMode(EngineStub.BOTH);
        engine.connect();
        version = engine.getVersion();
    }
    public UppaalSystem compile(Engine engine, Document doc)
        throws EngineException, IOException
    {
        // compile the model into system:
        ArrayList<Problem> problems = new ArrayList<>();
        UppaalSystem sys = engine.getSystem(doc, problems);
        if (!problems.isEmpty()) {
            boolean fatal = false;
            out.println("There are problems with the document:");
            for (Problem p : problems) {
                out.println(p.getType()+": "+p.toString());
                if (!"warning".equals(p.getType())) { // ignore warnings
                    fatal = true;
                }
            }
            if (fatal) {
                return null;
            }
        }
        return sys;
    }

    public void load(String args) {
        if (args.isEmpty()) {
            out.println("Error: expecting an URL or a file path as an argument.");
            return;
        }
        try {
            // try URL scheme (useful to fetch from Internet):
            doc = new PrototypeDocument().load(new URL(args));
        } catch (MalformedURLException ex) {
            try {
                // not URL, retry as it were a local filepath:
                doc = new PrototypeDocument().load(new URL("file", null, args));
            } catch (IOException ex1) {
                out.println("Error: "+ex1.getMessage());
                return;
            }
        } catch (IOException ex) {
            out.println("Error: "+ex.getMessage());
            return;
        }
        try {
            system = compile(engine, doc);
        } catch (EngineException | IOException ex) {
            out.println("Error: "+ex.getMessage());
            return;
        }
        out.println("OK.");
    }
    public static final String options = "order 0\n"
                + "reduction 1\n"
                + "representation 0\n"
                + "trace 1\n"
                + "extrapolation 0\n"
                + "hashsize 27\n"
                + "reuse 1\n"
                + "smcparametric 1\n"
                + "modest 0\n"
                + "statistical 0.01 0.01 0.05 0.05 0.05 0.9 1.1 0.0 0.0 1280.0 0.01";
    SymbolicTrace strace;
    ConcreteTrace ctrace;
    public QueryFeedback qf = new QueryFeedback()
    {
        @Override
        public void setProgressAvail(boolean availability)
        {
        }

        @Override
        public void setProgress(int load, long vm, long rss, long cached, long avail, long swap, long swapfree, long user, long sys, long timestamp)
        {
        }

        @Override
        public void setSystemInfo(long vmsize, long physsize, long swapsize)
        {
        }

        @Override
        public void setLength(int length)
        {
        }

        @Override
        public void setCurrent(int pos)
        {
        }

        @Override
        public void setTrace(char result, String feedback,
            SymbolicTrace trace, QueryResult queryVerificationResult)
        {
            strace = trace;
        }

        public void setTrace(char result, String feedback,
            ConcreteTrace trace, QueryResult queryVerificationResult)
        {
            ctrace = trace;
        }

        @Override
        public void setFeedback(String feedback)
        {
            if (feedback != null && feedback.length() > 0) {
                System.out.println("Feedback: " + feedback);
            }
        }

        @Override
        public void appendText(String s)
        {
            if (s != null && s.length() > 0) {
                System.out.println("Append: " + s);
            }
        }

        @Override
        public void setResultText(String s)
        {
            if (s != null && s.length() > 0) {
                System.out.println("Result: " + s);
            }
        }
    };
    
    public void print(QueryData data) {
		for (String title: data.getDataTitles()) {
			DataSet2D plot = data.getData(title);
			System.out.println("Plot \""+plot.getTitle()+
							   "\" showing \"" + plot.getYLabel() +
							   "\" over \"" + plot.getXLabel()+"\"");
			for (Data2D traj: plot) {
				System.out.print("Trajectory " + traj.getTitle()+":");
				for (Point2D.Double p: traj)
					System.out.print(" ("+p.x+","+p.y+")");
				System.out.println();
			}
		}
	}

    
    public void query(String args) {
        if (args.isEmpty()) {
            out.println("Error: expecting a query as an argument.");
            return;
        }
        if (system == null) {
            out.println("Error: no valid system is loaded (try \"load\" command).");
            return;
        }
        try {
            Query q = new Query(args, "");
            QueryResult res = engine.query(system, args, q, qf);
            out.println("Result: "+res);
            print(res.getData());
        } catch (EngineException ex) {
            out.println("Error: "+ex.getMessage());
        }
    }
    public void help(String args) {
        if (args.isEmpty()) {
            out.println("List of available commands:");
            out.println("  quit  - quits this program.");
            out.println("  help  - prints this help (use \"help <command>\" to investigate <command>).");
            out.println("  load  - loads a model from a Uppaal file.");
            out.println("  query - executes/checks the query.");
        } else {
            Command cmd = new Command(args);
            switch(cmd.getCommand()) {
                case "help":
                    out.println("\"help <command>\" prints description of the specified command.");
                    out.println("For example, \"help help\" prints this description.");
                    out.println("Try \"help\" to get a list of commands.");
                    break;
                case "quit":
                    out.println("\"quit\" terminates this program, it uses no arguments.");
                    break;
                case "load":
                    out.println("\"load <path>\" loads Uppaal model file at the specified path and compiles it");
                    break;
                case "query":
                    out.println("\"query <query>\" executes/checks the specified Uppaal query.");
                    out.println("For example, \"query A[] not deadlock\" checks if the model has no deadlocks.");
                    break;
                default:
                    out.println("No description for the following command: \""+cmd.getCommand()+"\"");
                    break;
            }
        }
    }
    public void interact(String prompt, InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        out.println("Welcome to Uppaal command line interface.");
        out.println(version);
        out.println("Type \"help\" for help.");
        while (!done) {
            out.print(prompt);
            out.flush();
            String line = in.readLine();
            Command cmd = new Command(line);
            switch (cmd.getCommand()) {
                case "quit":
                    engine.disconnect();
                    done = true;
                    break;
                case "help":
                    help(cmd.getArgs());
                    break;
                case "load":
                    load(cmd.getArgs());
                    break;
                case "query":
                    query(cmd.getArgs());
                    break;
                case "": // do nothing
                    break;
                default:
                    out.println("Unrecognized command: \""+cmd.getCommand()+"\"");
                    break;
            }
        }
    }
    
    public Main(PrintStream out) throws EngineException, IOException {
        done = false;
        this.out = out;
        connect();
    }
    
    public static void main(String args[]){
        try {
            com.uppaal.model.io2.XMLReader.setXMLResolver(new com.uppaal.model.io2.UXMLResolver());
            new Main(System.out).interact("> ", System.in);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EngineException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
