SOURCES := $(shell find src/main/java -name '*.java')
BUILD_DIR := bin
MARKER := $(BUILD_DIR)/.compiled
CLASSPATH := lib/*
TEST := ./src/test/java/Main.java

.PHONY: all clean run

build: $(MARKER)

$(MARKER): $(SOURCES)
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) -cp "$(CLASSPATH)" $(SOURCES)
	touch $(MARKER)

run: build
	java -cp "$(BUILD_DIR):$(CLASSPATH)" $(TEST)

clean:
	rm -rf bin
