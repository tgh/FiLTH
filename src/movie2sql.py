import sys
import string


def FormatTitle(title):
  """Properly format the movie title. e.g. "Falcon And The Snowman, The" ->
     "The Falcon and the Snowman"
  """
  #split title into separate words
  words = title.split()
  #one word title--no need to format
  if len(words) == 1:
    return title
  #last word is '[short]'
  if words[-1] == '[short]':
    del words[-1]
  #check the number of words again
  if len(words) == 1:
    return words[0]
  #second to last word has a comma at the end (e.g. 'Accused,', 'The')
  if words[-2][-1] == ',' and (words[-1] == 'The' or words[-1] == 'A' or words[-1] == 'An'):
    words[-2] = words[-2].translate(None, ',')
    words.insert(0, words[-1])
    del words[-1]
  #title is only two words--no need to format further
  if len(words) == 2:
    return ' '.join(words)
  #title is 3 words or more:
  #iterate over the words between the first and last words
  for i in range(1,len(words)-1):
    #lower case occurrances of these words:
    if words[i] in ('The','And','Of','At','In','As','It','By','On','An','To','A'):
      words[i] = string.lower(words[i])
  #go back and check for words ending with ':' (e.g. "X-Files: the" -> "X-Files: The")
  for i in range (0,len(words)-1):
    if words[i][-1] == ':':
      words[i+1] = string.capitalize(words[i+1])

  return ' '.join(words)



#------------
#--- MAIN ---
#------------

#check for file from command line
if len(sys.argv) != 2:
  print "**ERROR: need a file argument."
  sys.exit()
#open the file
try:
  f = open(sys.argv[1], 'r')
except IOError:
  print "**ERROR: opening file."
#grab all of the lines in the file
lines = f.readlines()
#close the file
f.close()
#iterate over the lines retrieved from the file
for line in lines:
  #strip ending newline character
  line = line.rstrip('\n')
  #no country is specified for this movie
  if line[-1] == ']':
    title, year, stars, mpaa = line.rsplit(None, 3)
    country = "DEFAULT"
  #this movie does have a country associated with it
  else:
    title, year, stars, mpaa, country = line.rsplit(None, 4)
  #format the title
  title = FormatTitle(title)
  #remove the parens around the year
  year = year[1:-1]
  #remove the brackets around the mpaa rating
  mpaa = mpaa[1:-1]
  #output the sql
  print "INSERT INTO movie VALUES (DEFAULT, '"\
                                  + title + "', "\
                                  + year + ", "\
                                  + stars + ", '"\
                                  + mpaa + "', "\
                                  + country + ");"
