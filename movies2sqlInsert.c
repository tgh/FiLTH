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
    char title[75];
    char year[9];
    char star[9];
    char country[21];
    
	//open the source file
	FILE * source =
        //fopen("/home/tylerhayes/workspace/cs386/temp", "r");
		fopen("/home/tylerhayes/workspace/cs386/moviesForDatabase.txt", "r");
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

		//read the next line in the file
		fgets_catcher = fgets(buffer, LINE_LENGTH, source);
		CheckFileReadError(source);

        //title
        for (i = 0; buffer[i] != '('; ++i)
            title[i] = buffer[i];
        title[i] = '\0';

        //year
        j = 0;
        for (i=i+1; buffer[i] != ')'; ++i)
        {
            year[j] = buffer[i];
            ++j;            
        }
        year[j] = '\0';

        //star
        j = 0;
        for (i=i+1; buffer[i] != '<'; ++i)
        {
            star[j] = buffer[i];
            ++j;
        }
        star[j] = '\0';

        //country
        j = 0;
        for (i=i+1; buffer[i] != '\n'; ++i)
        {
            country[j] = buffer[i];
            ++j;
        }
        country[j] = '\0';

        //output the sql statement
        if (strcmp(country, "DEFAULT")!=0)
        {
            printf("INSERT INTO movie VALUES ");
            printf("(\"%s\", \"%s\", \"%s\", \"%s\", \"seen\");\n",
                title, year, star, country);
        }
        else
        {
            printf("INSERT INTO movie VALUES ");
            printf("(\'%s\', %s, \'%s\', DEFAULT, \'seen\');\n",
                title, year, star);
        }
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
