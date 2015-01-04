#!/bin/bash
THIS_SCRIPT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $THIS_SCRIPT_DIR
java -classpath scalafx_2.11-8.0.20-R6.jar:scala-library.jar:scala-reflect.jar:timeslicerfx.jar se.timeslicer.ui.TimeslicerMain 
