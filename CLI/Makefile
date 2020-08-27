ROOT = $(CURDIR)
SRC = $(ROOT)/src
MODEL = $(ROOT)/uppaal/lib/model.jar
UPPAAL = $(ROOT)/uppaal/uppaal.jar
JLINE = $(ROOT)/lib/jline-3.14.1-SNAPSHOT.jar
JUNIT=$(ROOT)/lib/hamcrest-core-1.3.jar:$(ROOT)/lib/junit-4.13.jar
export CLASSPATH = $(SRC):$(MODEL):$(UPPAAL):$(ANTLR):$(JLINE):$(JLEDIT):$(JUNIT)

bin: build
	jar cvfm cli.jar MANIFEST.MF -C src .

build: $(SRC)/org/uppaal/cli/Makefile
	make -C $(SRC)/org/uppaal/cli build

run: 
	java -cp $(CLASSPATH) org.uppaal.cli.Main

runtest: 
	java -cp $(CLASSPATH) org.uppaal.cli.test.TestMain

clean: src/org/uppaal/cli/Makefile
	make -C src/org/uppaal/cli clean ; find . -name \*-e -exec rm {} \;