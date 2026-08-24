#!/usr/bin/env bash

set -e

SRC="$(find "./src/main/java/" -type f -name "*.java")"

CLASSPATH="./bin:$(find './lib/' -type f | paste -sd: -)"
javac -g -cp "$CLASSPATH" -d "./bin" $SRC
