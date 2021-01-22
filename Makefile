MAIN = Flow

LIB = lib
CP = classes
SRC = src

compil: $(MAIN).java

$(MAIN).java: src/*.java
	javac --source-path $(SRC) --class-path $(CP):$(LIB)/* -d $(CP) ./src/$(MAIN).java

run: compil
	java -classpath $(CP):$(LIB)/* $(MAIN)

clean:
	rm -rf classes/