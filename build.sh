#!/usr/bin/env bash

set -e

mkdir -p ./data
AUTORENT_DB_PATH="./data/WhatAreTheyWorth.db"
export AUTORENT_DB_PATH

SRC=""
packages=('orm' 'model' 'util' 'mannara' 'gui')
for package in "${packages[@]}"; do
    SRC="$(find "./src/main/java/$package/" -type f -name "*.java") $SRC"
done

CLASSPATH="./bin:$(find './lib/' -type f | paste -sd: -)"

javac -g -cp "$CLASSPATH" -d "./bin" $SRC
