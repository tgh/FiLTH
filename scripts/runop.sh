#!/bin/bash

source "$(dirname $0)/common.sh"

# check for args
if [ ! $# = 2 ]
then
  echo -e "\n\tusage: runop.sh <database name> <database password>\n"
  exit 1
fi

# check for the file 'recompile_OscarParser' in the temp directory
if [ -e $FILTH_TEMP_PATH/recompile_OscarParser ]
then
  echo -e "\n\tThe required jar file has been updated.  You must recompile OscarParser before executing.\n"
  exit 1
fi

# run the OscarParser java program
cd $FILTH_PATH/bin/
java -cp $FILTH_PATH/bin/:$FILTH_PATH/jar/postgres/postgresql-8.4-701.jdbc4.jar:$FILTH_PATH/jar/tylerhayes.tools/tylerhayes.tools.jar:$FILTH_PATH/jar/csv/javacsv.jar OscarParser $1 $2
cd -

# combine the created movie sql file (if created) with movie.sql
if [ -e $FILTH_SQL_PATH/movie2.sql ]
then
  cat $FILTH_SQL_PATH/movie2.sql >> $FILTH_SQL_PATH/movie.sql
  rm $FILTH_SQL_PATH/movie2.sql
fi

# combine the created crew_person sql file (if created) with crew_person.sql
if [ -e $FILTH_SQL_PATH/crew_person2.sql ]
then
  cat $FILTH_SQL_PATH/crew_person2.sql >> $FILTH_SQL_PATH/crew_person.sql
  rm $FILTH_SQL_PATH/crew_person2.sql
fi
