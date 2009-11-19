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
    
	//open the source file
	FILE * source =
		fopen("/home/tylerhayes/workspace/scratch/cs386/moviesForDatabase.txt", "r");
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

        for (i=0; buffer[i] != '('; ++i)
        {
            // the
            if (buffer[i] == ','
                && buffer[i+1] == ' '
                && buffer[i+2] == 't'
                && buffer[i+3] == 'h'
                && buffer[i+4] == 'e'
                && buffer[i+5] == '(')
            {
                strcpy(clone, buffer);

                k = 4;
                for (j=0; j != i; ++j)
                {
                    buffer[k] = clone[j];
                    ++k;
                }
                buffer[0] = 't';
                buffer[1] = 'h';
                buffer[2] = 'e';
                buffer[3] = ' ';

                for (i=k; buffer[i] != '\0'; ++i)
                    buffer[i] = buffer[i+1];

                break;
            }

            // a
            if (buffer[i] == ','
                && buffer[i+1] == ' '
                && buffer[i+2] == 'a'
                && buffer[i+3] == '(')
            {
                strcpy(clone, buffer);

                k = 2;
                for (j=0; j != i; ++j)
                {
                    buffer[k] = clone[j];
                    ++k;
                }
                buffer[0] = 'a';
                buffer[1] = ' ';

                for (i=k; buffer[i] != '\0'; ++i)
                    buffer[i] = buffer[i+1];

                break;
            }

            // an
            if (buffer[i] == ','
                && buffer[i+1] == ' '
                && buffer[i+2] == 'a'
                && buffer[i+3] == 'n'
                && buffer[i+4] == '(')
            {
                strcpy(clone, buffer);

                k = 3;
                for (j=0; j != i; ++j)
                {
                    buffer[k] = clone[j];
                    ++k;
                }
                buffer[0] = 'a';
                buffer[1] = 'n';
                buffer[2] = ' ';

                for (i=k; buffer[i] != '\0'; ++i)
                    buffer[i] = buffer[i+1];

                break;
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
