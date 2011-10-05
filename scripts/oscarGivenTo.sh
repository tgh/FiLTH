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
1927/28,writing (original story
1930/31,writing (original story
1931/32,writing (original story
1932/33,writing (original story
1934,writing (original story
writing (adaptation
writing (story and screenplay
writing (screenplay
writing (adapted
writing,' $filth_path/data/oscars.csv >> $filth_path/data/oscarsOfCategory.csv
# extract foreign language film nominees
echo extracting foreign language film nominees...
grep -i "foreign language film" $filth_path/data/oscars.csv >> $filth_path/data/oscarsOfCategory.csv
# extract documentary features
echo extracting documentary feature nominees...
fgrep -i 'documentary,
documentary (feature)' $filth_path/data/oscars.csv >> $filth_path/data/oscarsOfCategory.csv
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
sed -i -r "s/WRITING \(.*Adapt.*\)|WRITING \(Screenplay.*[bB]ased on.*([Mm]edium|Published)\)|WRITING \(Screenplay\)/Best Adapted Screenplay/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/WRITING/Best Screenplay/g" $filth_path/data/oscarsOfCategory.csv
# screenplay special cases: since these years have a 'story' category and a 'WRITING (Screenplay)'
#  category only, the screenplay category should be Best Screenplay instead of Best Adapted Screenplay
sed -i -r "s/1935,Best Adapted Screenplay/1935,Best Screenplay/g" $filth_path/data/oscarsOfCategory.csv
sed -i -r "s/1936,Best Adapted Screenplay/1936,Best Screenplay/g" $filth_path/data/oscarsOfCategory.csv
sed -i -r "s/1937,Best Adapted Screenplay/1937,Best Screenplay/g" $filth_path/data/oscarsOfCategory.csv
sed -i -r "s/1938,Best Adapted Screenplay/1938,Best Screenplay/g" $filth_path/data/oscarsOfCategory.csv
sed -i -r "s/1939,Best Adapted Screenplay/1939,Best Screenplay/g" $filth_path/data/oscarsOfCategory.csv
sed -i -r "s/1948,Best Adapted Screenplay/1948,Best Screenplay/g" $filth_path/data/oscarsOfCategory.csv
# rewrite best foreign langauge film...
echo rewriting best foreign film category as \"Best Foreign Language Film\"...
sed -i "s/FOREIGN LANGUAGE FILM/Best Foreign Language Film/g" $filth_path/data/oscarsOfCategory.csv
# rewrite best documentary feature...
echo rewriting documentary \(feature\) as \"Best Documentary\"...
sed -i "s/DOCUMENTARY (Feature)/Best Documentary/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/DOCUMENTARY,/Best Documentary,/g" $filth_path/data/oscarsOfCategory.csv
# rewrite NO/YES to 0/1
echo rewriting status as 0 or 1...
sed -i "s/YES$/1/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/NO$/0/g" $filth_path/data/oscarsOfCategory.csv
# remove "(Comedy Picture)" and "(Dramatic Picture) strings from 1928 Best Director category
echo removing unwanted substrings...
sed -i "s/ (Comedy Picture)//g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/ (Dramatic Picture)//g" $filth_path/data/oscarsOfCategory.csv

#-- REWRITING SPECIAL CASES --

echo rewriting special cases...
# change "Good Fellas" to "Goodfellas"
sed -i "s/Good Fellas/Goodfellas/g" $filth_path/data/oscarsOfCategory.csv
# change "Meredith Willson's The Music Man" to "The Music Man"
sed -i "s/Meredith Willson's //g" $filth_path/data/oscarsOfCategory.csv
# change "Sunset Blvd." to "Sunset Boulevard"
sed -i "s/Sunset Blvd./Sunset Boulevard/g" $filth_path/data/oscarsOfCategory.csv
# change "The Chorus (Les Choristes)" to just "The Chorus"
sed -i "s/ (Les Choristes)//g" $filth_path/data/oscarsOfCategory.csv
# change "Au Revoir, Les Enfants (Goodbye, Children)" to just "Au Revoir, Les Enfants"
sed -i "s/ (Goodbye, Children)//g" $filth_path/data/oscarsOfCategory.csv
# change "Federico Fellini's 8-1/2" to "8\u00BD" (which is the unicode escape sequence for one-half)
sed -i "s/Federico Fellini's 8-1\/2/8\\\\u00BD/g" $filth_path/data/oscarsOfCategory.csv
# change "Mulholland Drive" to "Mulholland Dr."
sed -i "s/Mulholland Drive/Mulholland Dr./g" $filth_path/data/oscarsOfCategory.csv
# change "The Postman (Il Postino)" to just "Il Postino"
sed -i "s/The Postman (Il Postino)/Il Postino/g" $filth_path/data/oscarsOfCategory.csv
# change "G. I. Joe" to "The Story of G. I. Joe"
sed -i "s/,G. I. Joe/,The Story of G. I. Joe/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/,\"G. I. Joe/,\"The Story of G. I. Joe/g" $filth_path/data/oscarsOfCategory.csv
# change Conrad L. Hall to just Conrad Hall
sed -i "s/Conrad L. Hall/Conrad Hall/g" $filth_path/data/oscarsOfCategory.csv
# change Noriyuki 'Pat' Morita to just Pat Morita
sed -i "s/Noriyuki 'Pat' Morita/Pat Morita/g" $filth_path/data/oscarsOfCategory.csv
# remove the nickname from Barney 'Chick' McGill
sed -i "s/Barney 'Chick'/Barney/g" $filth_path/data/oscarsOfCategory.csv
# change James Stewart to Jimmy Stewart
sed -i "s/James Stewart/Jimmy Stewart/g" $filth_path/data/oscarsOfCategory.csv
# change Charles Chaplin to Charlie Chaplin
sed -i "s/Charles Chaplin/Charlie Chaplin/g" $filth_path/data/oscarsOfCategory.csv
# change Sir Laurence Olivier to just Laurence Olivier
sed -i "s/Sir Laurence Olivier/Laurence Olivier/g" $filth_path/data/oscarsOfCategory.csv
# change ", Jr." to just " Jr." (e.g. Robert Downey, Jr. -> Robert Downey Jr.)
sed -i "s/, Jr./ Jr./g" $filth_path/data/oscarsOfCategory.csv
# change ", Sr." to just " Sr."
sed -i "s/, Sr./ Sr./g" $filth_path/data/oscarsOfCategory.csv
# remove "[came in 2nd]", "[came in 3rd]", etc.
sed -i "s/ \[came in.*\]//g" $filth_path/data/oscarsOfCategory.csv
# change F. Fellini to Federico Fellini
sed -i "s/F. Fellini/Federico Fellini/g" $filth_path/data/oscarsOfCategory.csv
# change Harry Stradling to Harry Stradling Sr. (cinematographer
sed -i "s/Harry Stradling,/Harry Stradling Sr.,/g" $filth_path/data/oscarsOfCategory.csv
# change Coen brothers records
sed -i "s/,Joel Coen,/,Joel and Ethan Coen,/g" $filth_path/data/oscarsOfCategory.csv
sed -i -r "s/Ethan Coen, Joel Coen|Ethan Coen &amp; Joel Coen|Joel Coen and Ethan Coen|Ethan Coen and Joel Coen|Joel Coen &amp; Ethan Coen/Joel and Ethan Coen/g" $filth_path/data/oscarsOfCategory.csv
# remove Donald Kaufman since he doesn't exist
sed -i "s/ and Donald Kaufman//g" $filth_path/data/oscarsOfCategory.csv
# change cinematographer Joe MacDonald to Joseph MacDonald
sed -i "s/Joe MacDonald/Joseph MacDonald/g" $filth_path/data/oscarsOfCategory.csv
# remove "; Ballet Photography by..."
sed -i "s/; Ballet Photo.*,/,/g" $filth_path/data/oscarsOfCategory.csv
# clean up screenplay recipient attribute values (they're so inconsistent it's rediculous)
sed -i "s/\"Story by.*; /\"/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/,Story by.*; /,/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/Story and Screenplay by //g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/;in collaboration with.*,/,/g" $filth_path/data/oscarsOfCategory.csv
sed -i -r "s/Screenplay by //g" $filth_path/data/oscarsOfCategory.csv
sed -i -r "s/; Adaptation by.*\"|; Story by.*\"|; Screen [Ss]tory by.*\"|; Original [Ss]tory by.*\"/\"/g" $filth_path/data/oscarsOfCategory.csv
sed -i -r "s/; Adaptation by.*,|; Story by.*,|; Screen [Ss]tory by.*,|; Stories by.*,|; Dialogue by.*,/,/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/Written by //g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/Written for the [Ss]creen by //g" $filth_path/data/oscarsOfCategory.csv
# change I. A. L. Diamond (screenwriter)
sed -i "s/I\. A\. L\. Diamond/I\.A\.L\. Diamond/g" $filth_path/data/oscarsOfCategory.csv
# fix titles where a token starts with a "'" but doesn't end with one (e.g. "Give 'em Hell Harry!", "Adalen '31")
#  this prevents a SQL syntax error when using Postgres full-text search in OscarParser.java
sed -i "s/Give 'em Hell/Give 'em' Hell/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/dalen '31/dalen '31'/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/Summer of '42/Summer of '42'/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/Casanova '70/Casanova '70'/g" $filth_path/data/oscarsOfCategory.csv
# change the status of ties from 1 to 2
#  1932 Best Actor
sed -i "s/{Champ\"\"}\"\"\",1/{Champ\"\"}\"\"\",2/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/Mr. Hyde\"\"}\"\"\",1/Mr. Hyde\"\"}\"\"\",2/g" $filth_path/data/oscarsOfCategory.csv
#  1968 Best Actress
sed -i "s/Brice\"\"}\"\"\",1/Brice\"\"}\"\"\",2/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/Aquitaine\"\"}\"\"\",1/Aquitaine\"\"}\"\"\",2/g" $filth_path/data/oscarsOfCategory.csv
#  1986 Best Documentary
sed -i "s/Justice, Producers\",1/Justice, Producers\",2/g" $filth_path/data/oscarsOfCategory.csv
sed -i "s/Berman, Producer\",1/Berman, Producer\",2/g" $filth_path/data/oscarsOfCategory.csv
# "The Invaders" (1942) should be "49th Parallel"
sed -i "s/The Invaders/49th Parallel/g" $filth_path/data/oscarsOfCategory.csv
# "All That Money Can Buy" should be "The Devil and Daniel Webster"
sed -i "s/All That Money Can Buy/The Devil and Daniel Webster/g" $filth_path/data/oscarsOfCategory.csv
# Albert Basserman should be Albert Bassermann (with 2 n's) (actor)
sed -i "s/Basserman/Bassermann/g" $filth_path/data/oscarsOfCategory.csv
# "Adventures of Robinson Crusoe" should be "Robinson Crusoe"
sed -i "s/Adventures of Robinson Crusoe/Robinson Crusoe/g" $filth_path/data/oscarsOfCategory.csv
# change "Victor/Victoria" to "Victor Victoria"
sed -i "s/Victor\/Victoria/Victor Victoria/g" $filth_path/data/oscarsOfCategory.csv
# "Tucker The Man and His Dream" should be "Tucker: The Man and His Dream"
sed -i "s/Tucker /Tucker: /g" $filth_path/data/oscarsOfCategory.csv
# remove Dame from actress names
sed -i "s/,Dame /,/g" $filth_path/data/oscarsOfCategory.csv
# "Smash-Up--The Story of a Woman" should be "Smash-Up: The Story of a Woman"
sed -i "s/Smash-Up--/Smash-Up: /g" $filth_path/data/oscarsOfCategory.csv
# there is a "鬯" encoding for some reason in Penelope Cruz's nomination for "Vicky Cristina Barcelona"
sed -i "s/鬯/elo/g" $filth_path/data/oscarsOfCategory.csv
# "Gaby - A True Story" should be "Gaby: A True Story"
sed -i "s/Gaby -/Gaby:/g" $filth_path/data/oscarsOfCategory.csv
# change "Jacqueline Susann's Once Is Not Enough" to just "Once Is Not Enough"
sed -i "s/Jacqueline Susann's //g" $filth_path/data/oscarsOfCategory.csv
# "Five Fingers" should be "5 Fingers"
sed -i "s/Five Fingers/5 Fingers/g" $filth_path/data/oscarsOfCategory.csv
# "Hallelujah" should be "Hallelujah!"
sed -i "s/Hallelujah/Hallelujah!/g" $filth_path/data/oscarsOfCategory.csv
# Robert Leonard should be Robert Z. Leonard (director)
sed -i "s/Robert Z\./Robert/g" $filth_path/data/oscarsOfCategory.csv
# William Wellman should be William A. Wellman (director)
sed -i "s/William A\./William/g" $filth_path/data/oscarsOfCategory.csv
# Willard Van Der Veer should be Willard Van der Veer (cinematographer)
sed -i "s/Van Der Veer/Van der Veer/g" $filth_path/data/oscarsOfCategory.csv
# "Tabu" should be "Tabu: A Story of the South Seas"
sed -i "s/Tabu/Tabu: A Story of the South Seas/g" $filth_path/data/oscarsOfCategory.csv
# Gaetano Gaudio should be Tony Gaudio (cinematographer)
sed -i -r "s/Gaetano|Gaetano \(Tony\)/Tony/g" $filth_path/data/oscarsOfCategory.csv
# William Daniels should be William H. Daniels (cinematographer)
sed -i "s/William Daniels/William H. Daniels/g" $filth_path/data/oscarsOfCategory.csv
# John Seitz should be John F. Seitz (cinematographer)
sed -i "s/John Seitz/John F. Seitz/g" $filth_path/data/oscarsOfCategory.csv
# "Four Devils" should be "4 Devils"
sed -i "s/Four Devils/4 Devils/g" $filth_path/data/oscarsOfCategory.csv
# Charles Lang (cinematographer)
sed -i -r "s/Charles Bryant Lang Jr.|Charles B. Lang Jr.|Charles Lang Jr.|Charles B. Lang/Charles Lang/g" $filth_path/data/oscarsOfCategory.csv
# George Folsey should be George J. Folsey (cinematographer)
sed -i "s/George Folsey/George J. Folsey/g" $filth_path/data/oscarsOfCategory.csv
# Joseph August should be Joseph H. August (cinematographer)
sed -i "s/Joseph August/Joseph H. August/g" $filth_path/data/oscarsOfCategory.csv
# Hal Rossen should be Harold Rossen (cinematographer)
sed -i "s/Hal Rosson/Harold Rosson/g" $filth_path/data/oscarsOfCategory.csv
# remove Bernard Knowles from cinematographer nomination since he was just a cameraman
sed -i "s/, Bernard Knowles//g" $filth_path/data/oscarsOfCategory.csv
# Allen Davey should be Allen M. Davey (cinematographer)
sed -i "s/Allen Davey/Allen M. Davey/g" $filth_path/data/oscarsOfCategory.csv
# Charles Clarke should be Charles G. Clarke
sed -i "s/Charles Clarke/Charles G. Clarke/g" $filth_path/data/oscarsOfCategory.csv
# Arthur Arling should be Arthur E. Arling (cinematographer)
sed -i "s/Arthur Arling/Arthur E. Arling/g" $filth_path/data/oscarsOfCategory.csv
# Franz Planer (cinematographer)
sed -i -r "s/Frank Planer|Franz F. Planer/Franz Planer/g" $filth_path/data/oscarsOfCategory.csv
# William Mellor should be William C. Mellor
sed -i "s/William Mellor/William C. Mellor/g" $filth_path/data/oscarsOfCategory.csv
# Winton Hoch should be Winton C. Hoch (cinematographer)
sed -i "s/Winton Hoch/Winton C. Hoch/g" $filth_path/data/oscarsOfCategory.csv
# Freddie Young (cinematographer)
sed -i -r "s/F. A. Young|Fred A. Young/Freddie Young/g" $filth_path/data/oscarsOfCategory.csv
# Daniel Fapp should be Daniel L. Fapp (cinematographer)
sed -i "s/Daniel Fapp/Daniel L. Fapp/g" $filth_path/data/oscarsOfCategory.csv
# Robert Surtees (cinematographer)
sed -i -r "s/Bruce Surtees|Robert L. Surtees/Robert Surtees/g" $filth_path/data/oscarsOfCategory.csv
# Eugen Shuftan should be Eugen Shufftan (cinematographer)
sed -i "s/Eugen Shuftan/Eugen Shufftan/g" $filth_path/data/oscarsOfCategory.csv
# remove Henri Persin from "The Longest Day" cinematographer nomination
sed -i "s/, (Henri Persin)//g" $filth_path/data/oscarsOfCategory.csv
# Philip Lathrop should be Philip H. Lathrop (cinematographer)
sed -i "s/Philip Lathrop/Philip H. Lathrop/g" $filth_path/data/oscarsOfCategory.csv
# Fred Koenekamp should be Fred J. Koenekamp (cinematographer)
sed -i "s/Fred Koenekamp/Fred J. Koenekamp/g" $filth_path/data/oscarsOfCategory.csv
# replace " and " with ", " in cinematography nomination for "The Reader" (2008)
sed -i "s/Chris Menges and Roger Deakins/\"Chris Menges, Roger Deakins\"/g" $filth_path/data/oscarsOfCategory.csv
# "This above All" should be "This Above All"
sed -i "s/This above All/This Above All/g" $filth_path/data/oscarsOfCategory.csv
# remove "Screenplay and Dialogue by "
sed -i "s/Screenplay and Dialogue by //g" $filth_path/data/oscarsOfCategory.csv
# "Open City" should be "Rome, Open City"
sed -i "s/Open City/\"Rome, Open City\"/g" $filth_path/data/oscarsOfCategory.csv
# "Shoe-Shine" should be "Shoeshine"
sed -i "s/Shoe-Shine/Shoeshine/g" $filth_path/data/oscarsOfCategory.csv
# Richard Breen should be Richard L. Breen
sed -i "s/Richard Breen/Richard L. Breen/g" $filth_path/data/oscarsOfCategory.csv
# change T. E. B. Clarke to T.E.B. Clarke
sed -i "s/T. E. B. Clarke/T.E.B. Clarke/g" $filth_path/data/oscarsOfCategory.csv
# "The Big Carnival" should be "Ace in the Hole"
sed -i "s/The Big Carnival/Ace in the Hole/g" $filth_path/data/oscarsOfCategory.csv
# Ian McLellan Hunter, John Dighton should be Dalton Trumbo
sed -i "s/Ian McLellan Hunter, John Dighton/Dalton Trumbo/g" $filth_path/data/oscarsOfCategory.csv
# S.J. Perelman should be S. J. Perelman
sed -i "s/S.J. Perelman/S. J. Perelman/g" $filth_path/data/oscarsOfCategory.csv
# "Vitelloni" should be "I Vitelloni"
sed -i "s/Vitelloni/I Vitelloni/g" $filth_path/data/oscarsOfCategory.csv
# Scarpelli should be Furio Scarpelli
sed -i "s/, Scarpelli/, Furio Scarpelli/g" $filth_path/data/oscarsOfCategory.csv
# Frank R. Pierson should be Frank Pierson
sed -i "s/Frank R. Pierson/Frank Pierson/g" $filth_path/data/oscarsOfCategory.csv
# "Those Magnificent Men in Their Flying Machines" should be
# "Those Magnificent Men in Their Flying Machines or How I Flew from London to Paris in 25 Hours 11 Minutes"
sed -i "s/Machines/Machines or How I Flew from London to Paris in 25 Hours 11 Minutes/g" $filth_path/data/oscarsOfCategory.csv
# "La Guerre Est Finie" should be "The War is Over"
sed -i "s/La Guerre Est Finie/The War is Over/g" $filth_path/data/oscarsOfCategory.csv
# Lonne Elder, III should be just Lonne Elder
sed -i "s/, III//g" $filth_path/data/oscarsOfCategory.csv
# Jay Allen should be Jay Presson Allen
sed -i "s/Jay ALlen/Jay Presson Allen/g" $filth_path/data/oscarsOfCategory.csv
# P.H. Vazak should be Robert Towne
sed -i "s/P.H. Vazak/Robert Towne/g" $filth_path/data/oscarsOfCategory.csv
# remove the apostrophe from Crocodile' Dundee
sed -i "s/Crocodile'/Crocodile/g" $filth_path/data/oscarsOfCategory.csv
# Frances Walsh should be Fran Walsh
sed -i "s/Frances Walsh/Fran Walsh/g" $filth_path/data/oscarsOfCategory.csv
# Steve Zaillian should be Steven Zaillian
sed -i "s/e Zai/en Zai/g" $filth_path/data/oscarsOfCategory.csv
# Borat should have a colon after it
sed -i "s/Borat/Borat:/g" $filth_path/data/oscarsOfCategory.csv
# change Guillermo del Toro to Guillermo Del Toro
sed -i "s/del Toro/Del Toro/g" $filth_path/data/oscarsOfCategory.csv

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

# one more special case (since this one contained encoded accented characters):
# remove "; in collaboration with"
sed -i "s/Luis Bunuel; in collaboration with Jean-Claude Carriere/\"Luis Bunuel, Jean-Claude Carriere\"/g" $filth_path/data/oscarsOfCategory.csv
