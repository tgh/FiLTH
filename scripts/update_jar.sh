#!/bin/bash

# copy the tylerhayes.tools.jar file from the JavaTools project
cp ~/Projects/JavaTools/tylerhayes.tools.jar ~/workspace/FiLTH/jar/
# create an empty file in the temp directory which the runop.sh script will
#  check for.  runop.sh will stop execution if the file is there
#  (indicating that OscarParser needs to be recompiled with this new jar
#  before runop.sh can execute).  cop.sh will remove the file.
touch ~/workspace/FiLTH/temp/recompile_OscarParser
