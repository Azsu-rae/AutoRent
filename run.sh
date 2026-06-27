#! /usr/bin/env bash

set -e

source ./build.sh

java -cp $CLASSPATH gui.MainApp
