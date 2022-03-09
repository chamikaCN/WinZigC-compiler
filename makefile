JFLAGS = -g -cp . -d .
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	src/main/java/LexAnalyzerException.java \
	src/main/java/ParserException.java \
	src/main/java/TokenType.java \
	src/main/java/Token.java \
	src/main/java/Node.java \
	src/main/java/LexScanner.java \
	src/main/java/Parser.java \
	src/main/java/winzigc.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	find . -name "*.class" -type f -delete