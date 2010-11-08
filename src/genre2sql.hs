module Main where
import System.Environment (getArgs)
import System.IO


main = do
  (file:args) <- getArgs
  contents <- readFile file
  let genres = lines contents
      -- e.g. ["Drama","Comedy","Documentary",...]
      sqlStatements = toSql genres
  mapM putStr sqlStatements



toSql :: [String] -> [String]
toSql genres = map sqlConversion genres

sqlConversion :: String -> String
sqlConversion genre = "INSERT INTO genre VALUES (DEFAULT, '" ++ genre ++ "');\n"
