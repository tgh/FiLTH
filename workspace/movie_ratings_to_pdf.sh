#!/bin/bash

antiword -w 120 ~/Projects/FiLTH/doc/Movie_Ratings.doc > ./movie_ratings.txt

sed -i "s/’/'/g" ./movie_ratings.txt
sed -i "s/‘/'/g" ./movie_ratings.txt
sed -i "s/…/.../g" ./movie_ratings.txt
sed -i "s/♥/Heart/g" ./movie_ratings.txt
sed -i "s/½/1\/2/g" ./movie_ratings.txt

./movie_ratings_to_LaTeX.py ./movie_ratings.txt > movie_ratings.tex

sed -i "s/I Heart/I $\\\\heartsuit$/g" ./movie_ratings.tex
sed -i "s/1\/2/\\\\small\\\\begin{math}\\\\frac{1}{2}\\\\end{math}\\\\normalsize/g" ./movie_ratings.tex
sed -i "s/NO STARS/\\\\footnotesize NO STARS \\\\normalsize/g" ./movie_ratings.tex

latex ./movie_ratings
dvipdf movie_ratings.dvi movie_ratings.pdf
rm movie_ratings.aux movie_ratings.log movie_ratings.dvi movie_ratings.txt movie_ratings.tex
