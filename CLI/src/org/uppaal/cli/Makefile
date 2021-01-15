DIRS = context exceptions commands frontend test
build :  subdirs *.java
	javac -d ${ROOT}/bin -Xlint:unchecked -cp $(CLASSPATH) *.java

clean: *.class
	rm *.class  ; for dir in $(DIRS) ; do make -C $${dir} clean ; done

subdirs:
	for dir in $(DIRS) ; do if ! make -C $${dir} build ; then exit 1 ; fi ; done
