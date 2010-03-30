module Main where
import System.IO
import System.Environment (getArgs)
import CSV
import Text.ParserCombinators.Parsec

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
  let contents' = apostrophize contents
      insertions = toSql contents'
  mapM putStr insertions


apostrophize :: (Either ParseError CSV) -> [[String]]
apostrophize (Right names) = map apostrophize' names

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


toSql :: [[String]] -> [String]
toSql records = map sqlConversion records


sqlConversion :: [String] -> String
sqlConversion (w:x:y:zs) = "INSERT INTO crewperson VALUES (DEFAULT, '" ++ w ++ "', '" ++ x ++ "', '" ++ y ++ "');\n"
sqlConversion (x:y:zs) = "INSERT INTO crewperson VALUES (DEFAULT, '" ++ x ++ "', '" ++ y ++ "', DEFAULT);\n"
sqlConversion (y:zs) = "INSERT INTO crewperson VALUES (DEFAULT, '" ++ y ++ "', DEFAULT, DEFAULT);\n"
