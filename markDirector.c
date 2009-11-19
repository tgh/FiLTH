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


    int i = 0;
    int cnt = 0;

	while (!feof(source))
	{
		//read the next line in the file
		fgets_catcher = fgets(buffer, LINE_LENGTH, source);
		CheckFileReadError(source);

        cnt = 0;

        for(i=0; buffer[i] != '\0'; ++i)
        {
            if (buffer[i] == '(')
                ++cnt;
        }
        
        if (cnt == 0)
        {
            for (i=i; i >= 0; --i)
                buffer[i+1] = buffer[i];
            
            buffer[0] = '`';
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
