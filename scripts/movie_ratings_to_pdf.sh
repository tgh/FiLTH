#!/bin/bash

if [ -z ${FILTH_PATH} ]
then
    FILTH_PATH=~/workspace/FiLTH
fi
FILTH_TEMP_PATH=$FILTH_PATH/temp

antiword -w 120 $FILTH_PATH/data/Movie_Ratings.doc > $FILTH_TEMP_PATH/movie_ratings.txt

sed -i "s/’/'/g" $FILTH_TEMP_PATH/movie_ratings.txt
sed -i "s/‘/'/g" $FILTH_TEMP_PATH/movie_ratings.txt
sed -i "s/…/.../g" $FILTH_TEMP_PATH/movie_ratings.txt
sed -i "s/♥/Heart/g" $FILTH_TEMP_PATH/movie_ratings.txt
sed -i "s/½/1\/2/g" $FILTH_TEMP_PATH/movie_ratings.txt

$FILTH_PATH/scripts/movie_ratings_to_LaTeX.py $FILTH_TEMP_PATH/movie_ratings.txt > $FILTH_TEMP_PATH/movie_ratings.tex

sed -i "s/I Heart/I $\\\\heartsuit$/g" $FILTH_TEMP_PATH/movie_ratings.tex
sed -i "s/1\/2/\\\\small\\\\begin{math}\\\\frac{1}{2}\\\\end{math}\\\\normalsize/g" $FILTH_TEMP_PATH/movie_ratings.tex
sed -i "s/NO STARS/\\\\footnotesize NO STARS \\\\normalsize/g" $FILTH_TEMP_PATH/movie_ratings.tex

latex --output-directory=$FILTH_TEMP_PATH/ $FILTH_TEMP_PATH/movie_ratings.tex
dvipdf $FILTH_TEMP_PATH/movie_ratings.dvi $FILTH_PATH/pdf/movie_ratings.pdf
rm $FILTH_TEMP_PATH/movie_ratings.aux $FILTH_TEMP_PATH/movie_ratings.log $FILTH_TEMP_PATH/movie_ratings.dvi $FILTH_TEMP_PATH/movie_ratings.txt $FILTH_TEMP_PATH/movie_ratings.tex
