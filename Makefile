export ROOT=$(CURDIR)
export SRC = $(ROOT)/src
export BIN = $(ROOT)/bin

uppaal-cli-javadoc.jar: uppaal-cli.jar doc
	jar cvf uppaal-cli-javadoc.jar -C javadoc .

uppaal-cli.jar: build
	jar cvfm uppaal-cli.jar MANIFEST.MF -C bin .  

install:
	cp uppaal-cli.jar ${UPPAALPATH} ; \
	cp uppaal-cli /usr/local/bin ; \
	cp lib/jline-3.14.1-SNAPSHOT.jar ${UPPAALPATH}/lib

build: $(SRC)/org/uppaal/cli/Makefile
	make -C $(SRC)/org/uppaal/cli build

doc: javadoc
	javadoc -d javadoc `find src -name \*.java`

javadoc:
	mkdir javadoc

test: 
	java -cp $(CLASSPATH):tests org.uppaal.cli.test.TestMain

clean: src/org/uppaal/cli/Makefile
	make -C src/org/uppaal/cli clean ; find . -name \*-e -exec rm {} \;