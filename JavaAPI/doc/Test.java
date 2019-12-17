import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import console.UppaalContext;

public class Test {
public static void main (String[] args) throws Exception {
UppaalContext context = new UppaalContext();
context.connectEngine();
Engine engine = context.getEngine();
System.out.println(engine.getOptionsInfo());
}
}