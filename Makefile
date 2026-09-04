SOURCES := $(shell find src/main/java -name '*.java')
BUILD_DIR := bin
MARKER := $(BUILD_DIR)/.compiled
CLASSPATH := lib/*
TEST := ./src/test/java/Main.java
MODELS := $(shell find ./src/main/java/model/ -type f -exec basename -s .java {} \;)
DB_PATH := ./data/WhatAreTheyWorth.db

.PHONY: all clean run

build: $(MARKER)

$(MARKER): $(SOURCES)
	mkdir -p $(BUILD_DIR)
	javac -d $(BUILD_DIR) -cp "$(CLASSPATH)" $(SOURCES)
	touch $(MARKER)

run: build
	AUTORENT_DB_PATH=$(DB_PATH) java -ea -cp "$(BUILD_DIR):$(CLASSPATH)" $(TEST) $(MODELS)

clean:
	rm -rf bin
	rm $(DB_PATH)
