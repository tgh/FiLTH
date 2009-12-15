/*
 * Copyright © 2009 Tyler Hayes
 * ALL RIGHTS RESERVED
 * [This program is licensed under the "3-clause ('new') BSD License"]
 * Please see the file COPYING in the source
 * distribution of this software for license terms.
 */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

#define LINE_LENGTH 100

void CheckFileReadError (FILE * source);

int main (int argc, char ** argv)
{	
	//used for reading the file by line
	char buffer[LINE_LENGTH];
    char clone[LINE_LENGTH];

    //check for file from command line
    if (argc != 2)
    {
        printf("Error: need a file argument.\n");
        exit(0);
    }
    
	//open the source file
	FILE * source = fopen(argv[1], "r");
	if (!source)
	{
		printf("\nError in opening file.\n");
		exit(-1);
	}
	
    //for catching return values of fgets. (only used to shut-up
    // compiler warnings since CheckFileReadError does the error check)
    char * fgets_catcher = NULL;

	while (!feof(source))
	{
        int i = 0;
        int j = 0;
        int k = 0;

		//read the next line in the file
		fgets_catcher = fgets(buffer, LINE_LENGTH, source);
		CheckFileReadError(source);

        //check for name first
        if (buffer[0] == '`')
        {
            //COMMENT OUT UNTIL continue; for Linux
            //remove \r from Windows
            for(i; buffer[i] != '\r'; ++i)
                ;
            buffer[i] = '\n';
            buffer[i+1] = '\0';
            printf("%s", buffer);
            continue;
        }

        //check for '~' or '*'
        if (buffer[0] == '~' || buffer[0] == '*')
            i = 1;

        //capatalize first letter
        buffer[i] = toupper(buffer[i]);

        for (i= i+1; buffer[i] != '('; ++i)
        {
            //: the
            if (buffer[i] == ':'
                && buffer[i+1] == ' '
                && buffer[i+2] == 't'
                && buffer[i+3] == 'h'
                && buffer[i+4] == 'e'
                && buffer[i+5] == ' ')
            {
                buffer[i+2] = 'T';
                i += 5;
                continue;
            }
            //: a
            if (buffer[i] == ':'
                && buffer[i+1] == ' '
                && buffer[i+2] == 'a'
                && buffer[i+3] == ' ')
            {
                buffer[i+2] = 'A';
                i += 3;
                continue;
            }
            //- or .
            if ((buffer[i] == '-' || buffer[i] == '.')
                && 97 <= buffer[i+1]
                && buffer[i+1] <= 122)
            {
                buffer[i+1] = toupper(buffer[i+1]);
                ++i;
                continue;
            } 
            // The
            if (buffer[i] == ' '
                && buffer[i+1] == 'T'
                && buffer[i+2] == 'h'
                && buffer[i+3] == 'e'
                && buffer[i+4] == ' ')
            {
                buffer[i+1] = 't';
                i += 4;
                continue;
            }
            // And
            if (buffer[i] == ' '
                && buffer[i+1] == 'A'
                && buffer[i+2] == 'n'
                && buffer[i+3] == 'd'
                && buffer[i+4] == ' ')
            {
                buffer[i+1] = 'a';
                i += 4;
                continue;
            }
            // Of
            if (buffer[i] == ' '
                && buffer[i+1] == 'O'
                && buffer[i+2] == 'f'
                && buffer[i+3] == ' ')
            {
                buffer[i+1] = 'o';
                i += 3;
                continue;
            }
            // At
            if (buffer[i] == ' '
                && buffer[i+1] == 'A'
                && buffer[i+2] == 't'
                && buffer[i+3] == ' ')
            {
                buffer[i+1] = 'a';
                i += 3;
                continue;
            }
            // In
            if (buffer[i] == ' '
                && buffer[i+1] == 'I'
                && buffer[i+2] == 'n'
                && buffer[i+3] == ' ')
            {
                buffer[i+1] = 'i';
                i += 3;
                continue;
            }
            // As
            if (buffer[i] == ' '
                && buffer[i+1] == 'A'
                && buffer[i+2] == 's'
                && buffer[i+3] == ' ')
            {
                buffer[i+1] = 'a';
                i += 3;
                continue;
            }
            // It
            if (buffer[i] == ' '
                && buffer[i+1] == 'I'
                && buffer[i+2] == 't'
                && buffer[i+3] == ' ')
            {
                buffer[i+1] = 'i';
                i += 3;
                continue;
            }
            // By
            if (buffer[i] == ' '
                && buffer[i+1] == 'B'
                && buffer[i+2] == 'y'
                && buffer[i+3] == ' ')
            {
                buffer[i+1] = 'b';
                i += 3;
                continue;
            }
            // On
            if (buffer[i] == ' '
                && buffer[i+1] == 'O'
                && buffer[i+2] == 'n'
                && buffer[i+3] == ' ')
            {
                buffer[i+1] = 'o';
                i += 3;
                continue;
            }
            // An
            if (buffer[i] == ' '
                && buffer[i+1] == 'A'
                && buffer[i+2] == 'n'
                && buffer[i+3] == ' ')
            {
                buffer[i+1] = 'a';
                i += 3;
                continue;
            }
            // To
            if (buffer[i] == ' '
                && buffer[i+1] == 'T'
                && buffer[i+2] == 'o'
                && buffer[i+3] == ' ')
            {
                buffer[i+1] = 't';
                i += 3;
                continue;
            }
            // A
            if (buffer[i] == ' '
                && buffer[i+1] == 'A'
                && buffer[i+2] == ' ')
            {
                buffer[i+1] = 'a';
                i += 2;
                continue;
            }

            if (buffer[i] == ' ')
            {
                buffer[i+1] = toupper(buffer[i+1]);
                ++i;
            }
        }

        //country
/*
        for (i=i; buffer[i] != '<'; ++i)
            ;

        if (buffer[i+1] == 'u'
            && buffer[i+2] == 's'
            && buffer[i+3] == 'a')
        {
            buffer[i+1] = 'U';
            buffer[i+2] = 'S';
            buffer[i+3] = 'A';
        }
        else
            buffer[i+1] = toupper(buffer[i+1]);

        for (i=i+1; buffer[i] != '\0'; ++i)
        {
            if (buffer[i] == ' ')
                buffer[i+1] = toupper(buffer[i+1]);
        }


        for (i; buffer[i] != ')'; ++i)
            ;

        buffer[i+1] = '<';
        buffer[i+2] = '\n';
        buffer[i+3] = '\0';
*/
        printf("%s", buffer);
	}
	
	fclose(source);
	exit(0);
}


/* 
 * checks that any read from file did not result in an error
 */
void CheckFileReadError(FILE * source)
{
	if (ferror(source))
	{
		printf("\nError in reading movieRatings.txt.\n");
		fclose(source);
		exit(-1);
	}
}
