#!/bin/bash

filth_path=~/Projects/FiLTH
filth_temp_path=~/Projects/FiLTH/temp

antiword -w 120 $filth_path/data/Movie_Ratings.doc > $filth_temp_path/movie_ratings.txt

sed -i "s/’/'/g" $filth_temp_path/movie_ratings.txt
sed -i "s/‘/'/g" $filth_temp_path/movie_ratings.txt
sed -i "s/…/.../g" $filth_temp_path/movie_ratings.txt
sed -i "s/♥/Heart/g" $filth_temp_path/movie_ratings.txt
sed -i "s/½/1\/2/g" $filth_temp_path/movie_ratings.txt

$filth_path/scripts/movie_ratings_to_LaTeX.py $filth_temp_path/movie_ratings.txt > $filth_temp_path/movie_ratings.tex

sed -i "s/I Heart/I $\\\\heartsuit$/g" $filth_temp_path/movie_ratings.tex
sed -i "s/1\/2/\\\\small\\\\begin{math}\\\\frac{1}{2}\\\\end{math}\\\\normalsize/g" $filth_temp_path/movie_ratings.tex
sed -i "s/NO STARS/\\\\footnotesize NO STARS \\\\normalsize/g" $filth_temp_path/movie_ratings.tex

latex $filth_temp_path/movie_ratings.tex
dvipdf $filth_temp_path/movie_ratings.dvi $filth_path/pdf/movie_ratings.pdf
rm $filth_temp_path/movie_ratings.aux $filth_temp_path/movie_ratings.log $filth_temp_path/movie_ratings.dvi $filth_temp_path/movie_ratings.txt $filth_temp_path/movie_ratings.tex
