import org.jline.builtins.Builtins;
import org.jline.builtins.CommandRegistry;
import org.jline.builtins.Completers;
import org.jline.builtins.Completers.SystemCompleter;
import org.jline.builtins.Completers.TreeCompleter;
import org.jline.builtins.Completers.TreeCompleter.Node;
import org.jline.builtins.Options.HelpException;
import org.jline.builtins.Widgets.ArgDesc;
import org.jline.builtins.Widgets.AutopairWidgets;
import org.jline.builtins.Widgets.AutosuggestionWidgets;
import org.jline.builtins.Widgets.CmdDesc;
import org.jline.builtins.Widgets.CmdLine;
import org.jline.builtins.Widgets.TailTipWidgets;
import org.jline.builtins.Widgets.TailTipWidgets.TipType;
import org.jline.keymap.KeyMap;
import org.jline.reader.*;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReader.SuggestionType;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.DefaultParser.Bracket;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.terminal.Cursor;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;
import org.jline.utils.InfoCmp.Capability;
import org.jline.utils.Status;
import org.jline.builtins.Completers;
import org.jline.builtins.Completers.RegexCompleter;
import java.util.HashMap;
import java.util.LinkedList;

public class Main {
public static void main (String[] args) {

	Node name_node = new Node(new StringsCompleter("toto", "titi"), new LinkedList<Node>());
	LinkedList<Node> nodes = new LinkedList<Node>();
	nodes.add(name_node);
	Node node1 = new Node (new StringsCompleter("bonjour"), nodes);
	Node node2 = new Node (new StringsCompleter("salut"), nodes);
	TreeCompleter completer = new TreeCompleter(node1, node2);
    LineReader reader = LineReaderBuilder.builder().completer(completer).build();
    String prompt = "toto";
    while (true) {
        String line = null;
        try {
            line = reader.readLine(prompt);
            for (String word: reader.getParsedLine().words()) System.out.println(word);
            ((LineReaderImpl)reader).clearScreen();
        } catch (UserInterruptException e) {
            // Ignore
        } catch (EndOfFileException e) {
            return;
        }
}
}
}
