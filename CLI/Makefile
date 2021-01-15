export ROOT=$(CURDIR)
SRC = $(ROOT)/src
BIN = $(ROOT)/bin
MODEL = $(ROOT)/uppaal/lib/model.jar
UPPAAL = $(ROOT)/uppaal/uppaal.jar
JLINE = $(ROOT)/lib/jline-3.14.1-SNAPSHOT.jar
JUNIT=$(ROOT)/lib/hamcrest-core-1.3.jar:$(ROOT)/lib/junit-4.13.jar
export CLASSPATH = $(BIN):$(MODEL):$(UPPAAL):$(ANTLR):$(JLINE):$(JLEDIT):$(JUNIT)

archives: build doc
	jar cvfm cli.jar MANIFEST.MF -C bin . ; jar cvf cli-javadoc.jar -C javadoc .

build: $(SRC)/org/uppaal/cli/Makefile
	make -C $(SRC)/org/uppaal/cli build

doc: javadoc
	javadoc -d javadoc `find src -name \*.java`

javadoc:
	mkdir javadoc

run: 
	java -cp $(CLASSPATH) org.uppaal.cli.Main

test: 
	java -cp $(CLASSPATH):tests org.uppaal.cli.test.TestMain

clean: src/org/uppaal/cli/Makefile
	make -C src/org/uppaal/cli clean ; find . -name \*-e -exec rm {} \;