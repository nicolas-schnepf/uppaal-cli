CLI = \
src/org/uppaal/cli/Command.java

HANDLERS = \
src/org/uppaal/cli/handlers/Handler.java

EXCEPTIONS = \
src/org/uppaal/cli/exceptions/ConsoleException.java \
src/org/uppaal/cli/exceptions/WrongModeException.java \
src/org/uppaal/cli/exceptions/UnknownCommandException.java \
src/org/uppaal/cli/exceptions/WrongCommandException.java \
src/org/uppaal/cli/exceptions/WrongObjectException.java \
src/org/uppaal/cli/exceptions/MissingArgumentException.java \
src/org/uppaal/cli/exceptions/EditorException.java \
src/org/uppaal/cli/exceptions/MenuException.java

build: cli handlers exceptions
clean: $(CLI:.java=.class) $(HANDLERS:.java=.class) $(EXCEPTIONS:.java=.class)
	find . -name \*.class -exec rm {} \;

cli: $(CLI)
	javac $(CLI)

handlers : $(HANDLERS)
	javac $(HANDLERS)

exceptions: cli $(EXCEPTIONS)
	javac -cp src $(EXCEPTIONS)