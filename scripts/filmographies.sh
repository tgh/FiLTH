#!/bin/bash

source "$(dirname $0)/common.sh"

temp=$FILTH_TEMP_PATH/filmographies_temp.txt

#convert the director filmographies Word document to a text file
antiword -w 120 $FILTH_PATH/data/Director_Filmographies.doc > $temp
#remove empty lines
sed -i '' '/^$/d' $temp
#remove the '~' markings (indicating I've seen the movie)
sed -i '' 's/~//g' $temp
#remove special characters
sed -i '' 's/ñ/n/g' $temp
sed -i '' 's/é/e/g' $temp
#put quotes around the movie title, remove the parens around the year, and add the year after the title (comma-separated, of course)
sed 's/\(.*\) (\([0-9][0-9][0-9][0-9]\))/"\1",\2,/g' $temp | $FILTH_SCRIPTS_PATH/filmographies.py > $FILTH_PATH/data/filmographies.csv
#append actor filmographies
#cat $FILTH_PATH/data/actor_filmographies.csv >> $FILTH_PATH/data/filmographies.csv
rm $temp
