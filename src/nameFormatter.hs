module Main where
import System.Environment
import System.IO

{- This program takes a list of names and outputs those names in lastname, firstname
  form.  It was written specifically for text files that looked like this:

  Tyler Hayes
  John Lennon
  Paul McCartney
  Johann Sebastian Bach
  Joel and Ethan Coen
  ...
  
  from which it would output the following:

  Hayes, Tyler
  Lennon, John
  McCartney, Paul
  Bach, Johann Sebastian
  Coen, Joel and Ethan

  It does not alphabetize them.
-}

main = do
  (file:args) <- getArgs
  contents <- readFile file
  let names = lines contents
      -- e.g. ["Christian Bale","Phillip Seymour Hoffman","Joel and Ethan Coen",...]
      seperatedNames = map words names
      -- e.g. [["Christian","Bale"],["Phillip","Seymour","Hoffman"],...]
      reorderedNames = map reName seperatedNames
      -- e.g. ["Bale, Christian\n", "Hoffman, Phillip Seymour\n","Coen, Joel and Ethan\n",...]
  mapM putStr reorderedNames
  

reName:: [String] -> String
-- four word pattern: i.e. ["Joel","and","Ethan","Coen"] -> "Coen, Joel and Ethan\n"
reName (v:w:x:y:zs) = y ++ ", " ++ (unwords [v,w,(x ++ "\n")])
-- three name pattern with "De" in the last name: i.e. ["Robert","De","Niro"] -> "De Niro, Robert"
reName (w:"De":x:zs) = "De " ++ x ++ ", " ++ w ++ "\n"
-- three name pattern with "Del" in the last name: i.e. ["Benicio Del Toro"] -> "Del Toro, Benicio"
reName (w:"Del":x:zs) = "Del " ++ x ++ ", " ++ w ++ "\n"
-- three word pattern with "Jr.": i.e. ["Robert","Downy","Jr."] -> "Downy Jr., Robert\n"
reName (w:x:"Jr.":zs) = x ++ " Jr., " ++ w ++ "\n"
-- three word pattern with "Van" in last name: i.e. ["Gus","Van","Sant"] -> "Van Sant, Gus"
reName (w:"Van":x:zs) = "Van " ++ x ++ ", " ++ w ++ "\n"
-- three word pattern with "Von" in last name: i.e. ["Lars","Von","Trier"] -> "Von Trier, Lars"
reName (w:"Von":x:zs) = "Von " ++ x ++ ", " ++ w ++ "\n"
-- three name pattern: i.e. ["Phillip","Seymour","Hoffman"] -> "Hoffman, Phillip Seymour\n"
reName (w:x:y:zs) = y ++ ", " ++ w ++ ", " ++ x ++ "\n"
-- two name pattern: i.e. ["Christian","Bale"] -> "Bale, Christian\n"
reName (x:y:zs) = y ++ ", " ++ x ++ "\n"
-- one name pattern: i.e. ["Madonna"] -> "Madonna\n"
reName (x:xs) = x ++ "\n"
-- newline space
reName [] = ""
