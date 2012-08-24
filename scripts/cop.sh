#!/bin/bash

# cop means "compile OscarParser"
# uses the jar files found in jar/ and places the .class file in bin/

filth_path=~/workspace/FiLTH

# remove the 'recompile_OscarParser' file from the temp directory if it
#  exists.  This will then allow the runop.sh script to execute.
if [ -e $filth_path/temp/recompile_OscarParser ]
then
  rm $filth_path/temp/recompile_OscarParser
fi

# compile OscarParser
javac -cp $filth_path/jar/tylerhayes.tools/tylerhayes.tools.jar:$filth_path/jar/postgres/postgresql-8.4-701.jdbc4.jar:$filth_path/jar/csv/javacsv.jar -d $filth_path/bin/ $filth_path/src/data/OscarParser.java
