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

#define LINE_LENGTH 82

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

        if (buffer[0] == '`')
        {
            //capatalize first letter
            buffer[1] = toupper(buffer[1]);

            for (i=2; buffer[i] != '\n'; ++i)
            {
                if (buffer[i] == ' ' || buffer[i] == '-')
                    buffer[i+1] = toupper(buffer[i+1]);
            }

            printf("%s", buffer);
            continue;
        }

        if (buffer[0] == '~')
        {
            i = 1;
            buffer[1] = toupper(buffer[1]);
        }
        else
        {
            buffer[0] = toupper(buffer[0]);
        }

        for (i; buffer[i] != '(' && (buffer[i+1] < 48 || buffer[i+1] > 57); ++i)
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
                ++i;
                continue;
            }
            //: a
            if (buffer[i] == ':'
                && buffer[i+1] == ' '
                && buffer[i+2] == 'a'
                && buffer[i+3] == ' ')
            {
                buffer[i+2] = 'A';
                ++i;
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
            //the
            if (buffer[i] == ' '
                && buffer[i+1] == 't'
                && buffer[i+2] == 'h'
                && buffer[i+3] == 'e'
                && buffer[i+4] == ' ')
            {
                continue;
            }
            //and
            if (buffer[i] == ' '
                && buffer[i+1] == 'a'
                && buffer[i+2] == 'n'
                && buffer[i+3] == 'd'
                && buffer[i+4] == ' ')
            {
                continue;
            }
            //of
            if (buffer[i] == ' '
                && buffer[i+1] == 'o'
                && buffer[i+2] == 'f'
                && buffer[i+3] == ' ')
            {
                continue;
            }
            //at
            if (buffer[i] == ' '
                && buffer[i+1] == 'a'
                && buffer[i+2] == 't'
                && buffer[i+3] == ' ')
            {
                continue;
            }
            //in
            if (buffer[i] == ' '
                && buffer[i+1] == 'i'
                && buffer[i+2] == 'n'
                && buffer[i+3] == ' ')
            {
                continue;
            }
            //as
            if (buffer[i] == ' '
                && buffer[i+1] == 'a'
                && buffer[i+2] == 's'
                && buffer[i+3] == ' ')
            {
                continue;
            }
            //it
            if (buffer[i] == ' '
                && buffer[i+1] == 'i'
                && buffer[i+2] == 't'
                && buffer[i+3] == ' ')
            {
                continue;
            }
            //by
            if (buffer[i] == ' '
                && buffer[i+1] == 'b'
                && buffer[i+2] == 'y'
                && buffer[i+3] == ' ')
            {
                continue;
            }
            //on
            if (buffer[i] == ' '
                && buffer[i+1] == 'o'
                && buffer[i+2] == 'n'
                && buffer[i+3] == ' ')
            {
                continue;
            }
            //an
            if (buffer[i] == ' '
                && buffer[i+1] == 'a'
                && buffer[i+2] == 'n'
                && buffer[i+3] == ' ')
            {
                continue;
            }
            //to
            if (buffer[i] == ' '
                && buffer[i+1] == 't'
                && buffer[i+2] == 'o'
                && buffer[i+3] == ' ')
            {
                continue;
            }
            //a
            if (buffer[i] == ' '
                && buffer[i+1] == 'a'
                && buffer[i+2] == ' ')
            {
                continue;
            }

            if (buffer[i] == ' ')
            {
                buffer[i+1] = toupper(buffer[i+1]);
                ++i;
            }
        }

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