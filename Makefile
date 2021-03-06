# Makefile for Assignment2
# david

JAVAC = /usr/bin/javac
.SUFFIXES: .java .class
SRCDIR = src
BINDIR = bin
TESTDIR = testing
OUTPUTDIR = out

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES = Score.class WordDictionary.class WordRecord.class HighScore.class WordManager.class WordPanel.class WordApp.class 
CLASS_FILES = $(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	 rm $(BINDIR)/*.class

run: $(CLASS_FILES)
	java -cp $(BINDIR) WordApp $(totalWords) $(noWords) $(dict)

