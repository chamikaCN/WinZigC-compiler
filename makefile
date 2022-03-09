JFLAGS = -g -cp . -d .
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	src/LexAnalyzerException.java \
	src/ParserException.java \
	src/TokenType.java \
	src/Token.java \
	src/Node.java \
	src/LexScanner.java \
	src/Parser.java \
	src/winzigc.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	find . -name "*.class" -type f -delete