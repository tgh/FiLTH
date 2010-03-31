module Main where
import System.IO
import System.Environment (getArgs)
import CSV (CSV, parseCSVFromFile)
import Text.ParserCombinators.Parsec (ParseError)

{- want result to be:
  ["INSERT INTO crewperson VALUES (DEFAULT, 'Bale', 'Christian', DEFAULT);\n",
   "INSERT INTO crewperson VALUES (DEFAULT, 'Hoffman', 'Phillip', 'Seymour');\n",
   "INSERT INTO crewperson VALUES (DEFAULT, 'Coen', 'Joel and Ethan', DEFAULT);\n",
   "INSERT INTO crewperson VALUES (DEFAULT, 'Jackson', 'Samuel', 'L.');\n",...]
-}

main = do
  (file:args) <- getArgs
  contents <- parseCSVFromFile file
  -- contents is now a list of lists of strings (a list of records)
  -- i.e. [["Bale","Christian"],["De Niro","Robert"],...]
  let contents' = removeLast contents
      contents'' = apostrophize contents'
      insertions = toSql contents''
  mapM putStr insertions


removeLast :: (Either ParseError CSV) -> [[String]]
removeLast (Right records) = (reverse . drop 1 . reverse) records

--------------------------------------------------------------------------------

{- Since sql uses apostrophes when inserting strings, a name with an apostrophe
   (such as O'Toole) would cause a problem.  These apostrophize functions finds
   an apostrophe in a name, and inserts an additional apostrophe (this escapes
   the real apostrophe in sql)--i.e. "O'Toole" -> "O''Toole".
-}
apostrophize :: [[String]] -> [[String]]
apostrophize names = map apostrophize' names

apostrophize' :: [String] -> [String]
apostrophize' name = map apostrophize'' name

apostrophize'' :: String -> String
apostrophize'' name | elem '\'' name = fix name
apostrophize'' other = other

fix :: String -> String
fix (x:xs) = if x == '\''
               then x:'\'':(fix xs)
               else x:(fix xs)
fix [] = []

-------------------------------------------------------------------------------


toSql :: [[String]] -> [String]
toSql records = map sqlConversion records


sqlConversion :: [String] -> String
sqlConversion (w:x:y:zs) = "INSERT INTO crewperson VALUES (DEFAULT, '" ++ w ++ "', '" ++ x ++ "', '" ++ y ++ "');\n"
sqlConversion (x:y:zs) = "INSERT INTO crewperson VALUES (DEFAULT, '" ++ x ++ "', '" ++ y ++ "', DEFAULT);\n"
sqlConversion (y:zs) = "INSERT INTO crewperson VALUES (DEFAULT, '" ++ y ++ "', DEFAULT, DEFAULT);\n"
