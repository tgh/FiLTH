#!/usr/bin/env python

import sys
import string


def LaTeXFormat(line):
  lineText = string.rstrip(line)
  if string.find(lineText, '&') != -1:
    lineText = string.replace(lineText, '&', '\\&')
  if len(lineText) == 1 or lineText == 'X, Y, Z':
    print '\n\\section*{\\color{Black} ' + lineText + '}'
    return

  if string.find(lineText, 'The Netherlands') != -1 or\
     string.find(lineText, 'Czech R') != -1 or\
     string.find(lineText, 'South Africa') != -1 or\
     string.find(lineText, 'New Zealand') != -1:
    words = string.split(lineText)[:-3]
  elif lineText[-1] == ']':
    words = string.split(lineText)[:-1]
  elif string.find(lineText, 'Total:') != -1 or\
       string.find(lineText, ' shorts') != -1 or\
       string.find(lineText, 'N/A') != -1:
    words = string.split(lineText)
  else:
    words = string.split(lineText)[:-2]

  lineText = string.join(words)

  if string.find(lineText, '****') != -1:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{OliveGreen} \\textbf{' + lineText + '}'
  elif string.find(lineText, '***') != -1:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{Blue} ' + lineText
  elif string.find(lineText, '**') != -1:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{Purple} ' + lineText
  elif string.find(lineText, ' *') != -1:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{Orange} ' + lineText
  elif string.find(lineText, 'Total:') != -1:
    sys.stdout.write('\n~\\\\ %force and empty line space\n\\color{Black} ' + lineText)
  elif string.find(lineText, ' shorts') != -1:
    print ', ' + lineText + '\\\\'
  elif string.find(lineText, 'N/A') != -1:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{Black} ' + lineText
  else:
    print '  \\par \\noindent \\hangpara{0.15in}{1}\\color{Red} ' + lineText


f = open(sys.argv[1], 'r')
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
lines = f.readlines()
map(LaTeXFormat, lines[1:])
print '\n\\end{multicols}'
print '\n\\end{document}'
