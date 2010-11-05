module Main where
import System.Environment (getArgs)
import System.IO


main = do
  (file:args) <- getArgs
  contents <- readFile file
  let countries = lines contents
      -- e.g. ["Best Picture","Best Actor",...]
      sqlStatements = toSql countries
  mapM putStr sqlStatements



toSql :: [String] -> [String]
toSql categories = map sqlConversion categories

sqlConversion :: String -> String
sqlConversion category = "INSERT INTO oscar VALUES (DEFAULT, '" ++ category ++ "');\n"
