#!/bin/bash

filth_path=~/Projects/FiLTH

# run the OscarParser java program
cd $filth_path/bin/
java -cp $filth_path/bin/:$filth_path/jar/postgresql-8.4-701.jdbc4.jar:$filth_path/jar/tylerhayes.tools.jar:$filth_path/jar/javacsv.jar OscarParser
cd $filth_path/scripts/
