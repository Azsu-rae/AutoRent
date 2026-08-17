#!/usr/bin/env bash

set -e

SRC=""
packages=('orm' 'model' 'util' 'mannara' 'gui')
for package in "${packages[@]}"; do
    SRC="$(find "./src/main/java/$package/" -type f -name "*.java") $SRC"
done

CLASSPATH="./bin:$(find './lib/' -type f | paste -sd: -)"
javac -g -cp "$CLASSPATH" -d "./bin" $SRC
