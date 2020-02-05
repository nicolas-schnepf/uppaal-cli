CLASSPATH = src:../uppaal-4.1.24/lib/model.jar:../uppaal-4.1.24/uppaal.jar:../jledit-0.2.2/jline.jar:../jledit-0.2.2/core.jar

CLI = \
src/org/uppaal/cli/Command.java \
src/org/uppaal/cli/CommandResult.java \
src/org/uppaal/cli/Context.java \
src/org/uppaal/cli/ConsoleManager.java \
src/org/uppaal/cli/Main.java

HANDLERS = \
src/org/uppaal/cli/handlers/Handler.java \
src/org/uppaal/cli/handlers/AbstractHandler.java \
src/org/uppaal/cli/handlers/CommandHandler.java \
src/org/uppaal/cli/handlers/EditorHandler.java \
src/org/uppaal/cli/handlers/SimulatorHandler.java \
src/org/uppaal/cli/handlers/VerifierHandler.java

EXCEPTIONS = \
src/org/uppaal/cli/exceptions/ConsoleException.java \
src/org/uppaal/cli/exceptions/WrongModeException.java \
src/org/uppaal/cli/exceptions/UnknownCommandException.java \
src/org/uppaal/cli/exceptions/WrongCommandException.java \
src/org/uppaal/cli/exceptions/WrongObjectException.java \
src/org/uppaal/cli/exceptions/MissingArgumentException.java \
src/org/uppaal/cli/exceptions/EditorException.java \
src/org/uppaal/cli/exceptions/MenuException.java \
src/org/uppaal/cli/exceptions/WrongArgumentException.java

build: cli handlers exceptions

run: 
	java -cp $(CLASSPATH) org.uppaal.cli.Main

clean: $(CLI:.java=.class) $(HANDLERS:.java=.class) $(EXCEPTIONS:.java=.class)
	find . -name \*.class -exec rm {} \;

cli: $(CLI)
	javac -cp $(CLASSPATH) $(CLI)

handlers : $(HANDLERS)
	javac -cp $(CLASSPATH) $(HANDLERS)

exceptions: cli $(EXCEPTIONS)
	javac -cp $(CLASSPATH) $(EXCEPTIONS)