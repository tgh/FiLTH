#!/usr/bin/env python

'''
This is a helper script to convert the Movie_Ratings.doc Word document into a
better-looking pdf file using LaTeX.

This Python script takes a filename as the only command-line argument.  The
filename should be that of a text file generated from within the
movie_ratings_to_pdf.sh shell script, which runs antiword on the Movie_Ratings
Word document, and does some cleaning to the resulting text file.  In other
words, this script should not be run directly--it is only run through that
shell script.

WARNING: error checking is not performed.  Use wisely.  Again, do not use
directly, only through the movie_ratings_to_pdf.sh script.
'''

import sys
import string


#------------------------------------------------------------------------------


def LaTeXFormat(line):
  '''Coverts a string (movie or alphabet header) into a LaTeX typesetting
  string and prints it.

  For example,

    "3:10 to Yuma (2007) **** [R] USA"

  becomes

    "\par \noindent \hangpara{0.15in}{1}\color{OliveGreen} \textbf{3:10 to Yuma (2007) ****}"
  '''

  #remove the newline at the end (or any other whitespace)
  lineText = string.rstrip(line)
  #replace '&' with '\&' since '&' needs to be escaped in LaTeX
  if string.find(lineText, '&') != -1:
    lineText = string.replace(lineText, '&', '\\&')
  #the line is an alphabet header (e.g. "A" for all movies starting with 'A')
  if len(lineText) == 1 or lineText == 'X, Y, Z':
    print '\n\\section*{\\color{Black} ' + lineText + '}'
    return

  #chop off the country of origin of the movie:
  # country is two words
  if string.find(lineText, 'The Netherlands') != -1 or\
     string.find(lineText, 'Czech R') != -1 or\
     string.find(lineText, 'South Africa') != -1 or\
     string.find(lineText, 'New Zealand') != -1:
    words = string.split(lineText)[:-3]
  # no country
  elif lineText[-1] == ']':
    words = string.split(lineText)[:-1]
  # country of one word (e.g. "USA", or "France")
  else:
    words = string.split(lineText)[:-2]

  #combine the words back into a big string
  lineText = string.join(words)

  #color the line based on star rating
  # **** = green
  if string.find(lineText, '****') != -1:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{OliveGreen} \\textbf{' + lineText + '}'
  # *** or ***1/2 = blue
  elif string.find(lineText, '***') != -1:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{Blue} ' + lineText
  # ** or **1/2 = purple
  elif string.find(lineText, '**') != -1:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{Purple} ' + lineText
  # * or *1/2 = orange
  elif string.find(lineText, ' *') != -1:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{Orange} ' + lineText
  # N/A = black
  elif string.find(lineText, 'N/A') != -1:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{Black} ' + lineText
  # no stars or 1/2* = red
  else:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{Red} ' + lineText


#------------------------------------------------------------------------------


#it is assumed that a command-line argument was given, that it is of a file that
# exists, and that the file is well-formed as expected
f = open(sys.argv[1], 'r')
#print out appropriate LaTeX header formatting stuff
print '\\documentclass{article}\n'
print '\\usepackage[usenames]{color}'
print '\\usepackage{hanging}'
print '\\usepackage{multicol}\n'
print '\\addtolength{\oddsidemargin}{-1.5in}'
print '\\addtolength{\evensidemargin}{-1.75in}'
print '\\addtolength{\\textwidth}{2.75in}'
print '\\addtolength{\\topmargin}{-1.0in}'
print '\\addtolength{\\textheight}{2.5in}\n'
print '\\begin{document}\n'
print '\\begin{multicols}{3}\n'
print '\\section*{\\#}'
#convert each line of the file to its appropriate corresponding line for LaTeX
lines = f.readlines()
map(LaTeXFormat, lines[1:])
#print appropriate LaTeX footer formatting stuff
print '\n\\end{multicols}'
print '\n\\end{document}'
f.close()
