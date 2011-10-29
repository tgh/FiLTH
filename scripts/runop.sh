#!/bin/bash

# check for args
if [ ! $# = 2 ]
then
  echo -e "\n\tusage: runop.sh <database name> <database password>\n"
  exit 1
fi

filth_path=~/Projects/FiLTH

# check for the file 'recompile_OscarParser' in the temp directory
if [ -e $filth_path/temp/recompile_OscarParser ]
then
  echo -e "\n\tThe required jar file has been updated.  You must recompile OscarParser before executing.\n"
  exit 1
fi

# run the OscarParser java program
cd $filth_path/bin/
java -cp $filth_path/bin/:$filth_path/jar/postgresql-8.4-701.jdbc4.jar:$filth_path/jar/tylerhayes.tools.jar:$filth_path/jar/javacsv.jar OscarParser $1 $2
cd $filth_path/scripts/

# combine the created movie sql file (if created) with movie.sql
if [ -e $filth_path/sql/movie2.sql ]
then
  cat $filth_path/sql/movie2.sql >> $filth_path/sql/movie.sql
  rm $filth_path/sql/movie2.sql
fi

# combine the created crew_person sql file (if created) with crew_person.sql
if [ -e $filth_path/sql/crew_person2.sql ]
then
  cat $filth_path/sql/crew_person2.sql >> $filth_path/sql/crew_person.sql
  rm $filth_path/sql/crew_person2.sql
fi

