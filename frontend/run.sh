#! /usr/bin/env bash

set -e

source ./backend/build.sh
source ./frontend/build.sh

java -cp $CLASSPATH gui.MainApp
