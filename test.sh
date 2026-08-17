#!/usr/bin/env bash

set -e

source .env.pub.sh

MODELS="$( find ./src/main/java/model/ -type f -exec basename -s .java {} \; )"
java -ea -classpath "$CLASSPATH" src/test/java/Main.java "$MODELS"
