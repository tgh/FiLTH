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
  let insertions = toSql contents
  mapM putStr insertions


toSql :: (Either ParseError CSV) -> [String]
toSql (Right records) = map sqlConversion records


sqlConversion :: [String] -> String
sqlConversion (w:x:y:zs) = "INSERT INTO crewperson VALUES (DEFAULT, '" ++ w ++ "', '" ++ x ++ "', '" ++ y ++ "');\n"
sqlConversion (x:y:zs) = "INSERT INTO crewperson VALUES (DEFAULT, '" ++ x ++ "', '" ++ y ++ "', DEFAULT);\n"
sqlConversion (y:zs) = "INSERT INTO crewperson VALUES (DEFAULT, '" ++ y ++ "', DEFAULT, DEFAULT);\n"
