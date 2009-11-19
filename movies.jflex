
%%

%public
%class MovieScanner
%extends Object
%unicode
%line
%function nextToken
%standalone

Title = [^()\n\r\*]*
TitleDelimiter = "("
YearDelimiter = ")"
Year = [1-2][09][0-9][0-9]
StarRating = "NO STARS" | "N/A" | [*½][*½]?[*½]?[*½]?
MpaaRating = 
Newline = \n

%init{
        System.out.println("------------------------------------");
        System.out.println("-- Insert movies into movie table --");
        System.out.println("------------------------------------");
        System.out.print("\n");
%init}

%%

<YYINITIAL> {

    {TitleDelimiter}    { }

    {YearDelimiter}     { }

    {Year}              {System.out.print(" - YEAR: " + yytext());}

    {StarRating}        {System.out.print(" - RATING: " + yytext());} 

    {Title}             {System.out.print("INSERT INTO movie " + yytext());}

    {Newline}           {System.out.print("\n");}

}
