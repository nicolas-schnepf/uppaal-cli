CLASSPATH = src:../uppaal-4.1.24/lib/model.jar:../uppaal-4.1.24/uppaal.jar:../jledit-0.2.2/jline.jar:../jledit-0.2.2/core.jar

JUNITPATH=../junit/hamcrest-core-1.3.jar:../junit/junit-4.13.jar

CLI = \
src/org/uppaal/cli/ConsoleManager.java \
src/org/uppaal/cli/Main.java

COMMANDS = \
src/org/uppaal/cli/commands/Command.java \
src/org/uppaal/cli/commands/CommandResult.java \
src/org/uppaal/cli/commands/Context.java \
src/org/uppaal/cli/commands/Expert.java \
src/org/uppaal/cli/commands/AbstractExpert.java \
src/org/uppaal/cli/commands/DocumentExpert.java \
src/org/uppaal/cli/commands/QueryExpert.java \
src/org/uppaal/cli/commands/TemplateExpert.java \
src/org/uppaal/cli/commands/LocationExpert.java \
src/org/uppaal/cli/commands/EdgeExpert.java

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
src/org/uppaal/cli/exceptions/WrongArgumentException.java \
src/org/uppaal/cli/exceptions/UnknownModeException.java \
src/org/uppaal/cli/exceptions/ExtraArgumentException.java \
src/org/uppaal/cli/exceptions/WrongExtensionException.java \
src/org/uppaal/cli/exceptions/WrongFormatException.java \
src/org/uppaal/cli/exceptions/ExistingElementException.java \
src/org/uppaal/cli/exceptions/MissingElementException.java

TEST = \
src/org/uppaal/cli/test/EditorHandlerTest.java \
src/org/uppaal/cli/test/TestMain.java

build: cli

run: 
	java -cp $(CLASSPATH) org.uppaal.cli.Main

runtest: 
	java -cp $(CLASSPATH):$(JUNITPATH) org.uppaal.cli.test.TestMain

clean: $(CLI:.java=.class) $(HANDLERS:.java=.class) $(EXCEPTIONS:.java=.class)
	find . -name \*.class -exec rm {} \;

cli: $(CLI) commands exceptions handlers test
	javac -Xlint:unchecked -cp $(CLASSPATH) $(CLI)

commands : $(COMMANDS)
	javac -Xlint:unchecked -cp $(CLASSPATH) $(COMMANDS)

handlers : $(HANDLERS)
	javac -Xlint:unchecked -cp $(CLASSPATH) $(HANDLERS)

exceptions: $(EXCEPTIONS)
	javac -Xlint:unchecked -cp $(CLASSPATH) $(EXCEPTIONS)

test: $(TEST)
	javac -Xlint:unchecked -cp $(CLASSPATH):$(JUNITPATH) $(TEST)