#!/bin/bash

filth_path=~/Projects/FiLTH


#-------------------------------------------------------------------------------
# PHASE 1: EXTRACTION                                                         --
#-------------------------------------------------------------------------------

# extract best picture nominees
echo extracting best picture nominees...
fgrep -i 'best picture
outstanding production
best motion picture
outstanding picture
outstanding motion picture' $filth_path/data/oscars.csv > $filth_path/data/oscarsOfCategory.csv
# extract actor nominees (and remove unwanted lines with "factory")
echo extracting actor nominees...
grep -i actor $filth_path/data/oscars.csv > $filth_path/temp/temp
grep -i -v factory $filth_path/temp/temp >> $filth_path/data/oscarsOfCategory.csv
# extract actress nominees
echo extracting actress nominees...
grep -i actress $filth_path/data/oscars.csv >> $filth_path/data/oscarsOfCategory.csv
# extract director nominees
echo extracting director nominees...
grep -i directing $filth_path/data/oscars.csv >> $filth_path/data/oscarsOfCategory.csv
# extract best cinematography nominees
echo extracting cinematography nominees...
grep -i cinematography $filth_path/data/oscars.csv >> $filth_path/data/oscarsOfCategory.csv
# extract screenplay nominees
echo extracting screenplay nominees...
fgrep -i 'writing (original screenplay
writing (original story
writing (adaptation
writing (story and screenplay
writing (screenplay
writing (adapted' $filth_path/data/oscars.csv >> $filth_path/data/oscarsOfCategory.csv
# extract foreign language film nominees
echo extracting foreign language film nominees...
grep -i "foreign language film" $filth_path/data/oscars.csv >> $filth_path/data/oscarsOfCategory.csv
# removing "1953,COSTUME DESIGN...The Actress..." line
echo removing unwanted lines...
sed -i "/COSTUME DESIGN/d" $filth_path/data/oscarsOfCategory.csv


#-------------------------------------------------------------------------------
# PHASE 2: REWRITING                                                          --
#-------------------------------------------------------------------------------

# rewrite dual years as one year (i.e. 1927/1928 -> 1928)
echo rewriting dual-years as one year...
sed -i "s/[23][0-9]\///g" $filth_path/data/oscarsOfCategory.csv
# rewrite best picture categories as "Best Picture"
echo rewriting best picture categories as \"Best Picture\"...
sed -i -r "s/OUTSTANDING.*[EN],|BEST.*PICTURE,/Best Picture,/g" $filth_path/data/oscarsOfCategory.csv
# rewrite best actor categories as "Best Actor"
echo rewriting best actor categories as \"Best Actor\"...
sed -i -r "s/ACTOR,|ACTOR IN A LEADING ROLE,/Best Actor,/g" $filth_path/data/oscarsOfCategory.csv
# rewrite best supporting actor as "Best Supporting Actor"
echo rewriting best supporting actor as \"Best Supporting Actor\"...
sed -i "s/ACTOR.*ROLE/Best Supporting Actor/g" $filth_path/data/oscarsOfCategory.csv
# rewrite best actress categories as "Best Actress"
echo rewriting best actress categories as \"Best Actress\"...
sed -i -r "s/ACTRESS,|ACTRESS IN A LEADING ROLE,/Best Actress,/g" $filth_path/data/oscarsOfCategory.csv
# rewrite best supporting actress as "Best Supporting Actress"
echo rewriting best supporting actress as \"Best Supporting Actress\"...
sed -i "s/ACTRESS.*ROLE/Best Supporting Actress/g" $filth_path/data/oscarsOfCategory.csv
# rewrite best director category as "Best Director"
echo rewriting best director category as \"Best Director\"...
sed -i "s/DIRECTING/Best Director/g" $filth_path/data/oscarsOfCategory.csv
# rewrite best cinematography category as "Best Cinematography..."
echo rewriting best cinematography categories...
sed -i "s/CINEMATOGRAPHY.*White)/Best Cinematography (black and white)/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/CINEMATOGRAPHY (Color)/Best Cinematography (color)/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/CINEMATOGRAPHY,/Best Cinematography,/g" $filth_path/data/oscarsOfCategory.csv
# rewrite best screenplay categories...
echo rewriting best screenplay categories...
sed -i -r "s/WRITING \(Original S.*\)|WRITING \(Screenplay Written Directly for the Screen.*\)|WRITING \(Story and Screenplay.*\)|WRITING \(Screenplay--Original\)/Best Original Screenplay/g" $filth_path/data/oscarsOfCategory.csv
sed -i -r "s/WRITING \(.*Adapt.*\)|WRITING \(Screenplay.*[bB]ased on.*\)|WRITING \(Screenplay\)/Best Adapted Screenplay/g" $filth_path/data/oscarsOfCategory.csv
# rewrite best foreign langauge film...
echo rewriting best foregin film category as \"Best Foreign Language Film\"...
sed -i "s/FOREIGN LANGUAGE FILM/Best Foreign Language Film/g" $filth_path/data/oscarsOfCategory.csv
# rewrite NO/YES to 0/1
echo rewriting status as 0 or 1...
sed -i "s/YES$/1/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/NO$/0/g" $filth_path/data/oscarsOfCategory.csv
# remove "(Comedy Picture)" and "(Dramatic Picture) strings from 1928 Best Director category
echo removing unwanted substrings...
sed -i "s/ (Comedy Picture)//g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/ (Dramatic Picture)//g" $filth_path/data/oscarsOfCategory.csv

#-------------------------------------------------------------------------------
# PHASE 3: DECODING ESCAPE SEQUENCES                                          --
#-------------------------------------------------------------------------------

# decode ampersands
echo "decoding ampersand escape sequence to \"&\"..."
sed -i "s/\&amp;/\&/g" $filth_path/data/oscarsOfCategory.csv
# decode accented characters
echo "decoding escape sequences for accented characters..."
#sed -i "s/&Atilde;&copy;/é/g" $filth_path/data/oscarsOfCategory.csv
#sed -i "s/&Atilde;&laquo;/ë/g" $filth_path/data/oscarsOfCategory.csv
#sed -i "s/&Atilde;&frac14;/ü/g" $filth_path/data/oscarsOfCategory.csv
#sed -i "s/&Atilde;&para;/ö/g" $filth_path/data/oscarsOfCategory.csv
#sed -i "s/&Atilde;&sup3;/ó/g" $filth_path/data/oscarsOfCategory.csv
#sed -i "s/&Atilde;&uml;/è/g" $filth_path/data/oscarsOfCategory.csv
#sed -i "s/&Atilde;&plusmn;/ñ/g" $filth_path/data/oscarsOfCategory.csv
#sed -i "s/&Atilde;&curren;/ä/g" $filth_path/data/oscarsOfCategory.csv
#sed -i "s/&Atilde;&iexcl;/á/g" $filth_path/data/oscarsOfCategory.csv
#sed -i "s/&Atilde;&hellip;/Å/g" $filth_path/data/oscarsOfCategory.csv
#sed -i "s/&acirc;&euro;&trade;/''/g" $filth_path/data/oscarsOfCategory.csv

sed -i "s/&Atilde;&copy;/e/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/&Atilde;&laquo;/e/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/&Atilde;&frac14;/u/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/&Atilde;&para;/o/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/&Atilde;&sup3;/o/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/&Atilde;&uml;/e/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/&Atilde;&plusmn;/n/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/&Atilde;&curren;/a/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/&Atilde;&iexcl;/a/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/&Atilde;&hellip;/A/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/&acirc;&euro;&trade;/''/g" $filth_path/data/oscarsOfCategory.csv

#-------------------------------------------------------------------------------
# PHASE 4: PARSING THE CSV FILE                                               --
#-------------------------------------------------------------------------------

# run the OscarParser java program
#echo "running OscarParser..."
#java -cp $filth_path/bin/:$filth_path/jar/postgresql-8.4-701.jdbc4.jar:$filth_path/jar/tylerhayes.tools.jar:$filth_path/jar/javacsv.jar $filth_path/bin/OscarParser
