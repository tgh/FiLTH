module Main where
import System.Environment (getArgs)
import System.IO


main = do
  (file:args) <- getArgs
  contents <- readFile file
  let countries = lines contents
      -- e.g. ["USA","England","Italy",...]
      sqlStatements = toSql countries
  mapM putStr sqlStatements



toSql :: [String] -> [String]
toSql countries = map sqlConversion countries

sqlConversion :: String -> String
sqlConversion country = "INSERT INTO country VALUES ('" ++ country ++ "');\n"
