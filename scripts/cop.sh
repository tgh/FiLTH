#!/bin/bash

# cop means "compile OscarParser"
# uses the jar files found in jar/ and places the .class file in bin/

filth_path=~/Projects/FiLTH

javac -cp $filth_path/jar/tylerhayes.tools.jar:$filth_path/jar/postgresql-8.4-701.jdbc4.jar:$filth_path/jar/javacsv.jar -d $filth_path/bin/ $filth_path/src/OscarParser.java
