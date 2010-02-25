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

#define LINE_LENGTH 101

void formatTitle(char * buffer);

int main (int argc, char ** argv)
{
	char buffer[LINE_LENGTH];   //used for reading the file line by line
    char title[85];
    char year[9];
    char star[12];
    char mpaa[8];
    char country[25];

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
        int skipCountry = 0;

		//read the next line in the file
		fgets_catcher = fgets(buffer, LINE_LENGTH, source);
	    if (fgets_catcher < 0)
	    {
		    printf("\nError in reading file.\n");
		    fclose(source);
		    exit(-1);
	    }

        //format the title
        formatTitle(buffer);

        //copy the title
        for (i=0; buffer[i+1] != '('; ++i)
            title[i] = buffer[i];
        title[i] = '\0';

        //copy the year
        j = 0;
        for (i=i+2; buffer[i] != ')'; ++i)
        {
            year[j] = buffer[i];
            ++j;
        }
        year[j] = '\0';

        //copy the star rating
        j = 0;
        for (i=i+2; buffer[i+1] != '['; ++i)
        {
            star[j] = buffer[i];
            ++j;
        }
        star[j] = '\0';

        //copy mpaa rating
        j = 0;
        for (i=i+2; buffer[i] != ']'; ++i)
        {
            mpaa[j] = buffer[i];
            ++j;
        }
        mpaa[j] = '\0';

        //check for no country
        if (buffer[i+1] == '\n')
            skipCountry = 1;

        //copy country
        if (!skipCountry)
        {
            j = 0;
            for (i=i+2; buffer[i] != '\n'; ++i)
            {
                country[j] = buffer[i];
                ++j;
            }
            country[j] = '\0';
        }

        if (skipCountry)
        {
            printf("INSERT INTO movie VALUES ");
            printf("(DEFAULT, \'%s\', %s, \'%s\', \'%s\', DEFAULT);\n",
                title, year, star, mpaa);
        }
        else
        {
            printf("INSERT INTO movie VALUES ");
            printf("(DEFAULT, \'%s\', %s, \'%s\', \'%s\', \'%s\');\n",
                title, year, star, mpaa, country);
        }
	}
	
	fclose(source);
	exit(0);
}


void formatTitle(char * buffer)
{
    char clone[LINE_LENGTH];
    int i = 0;
    int j = 0;
    int k = 0;

    for (i=0; buffer[i] != '('; ++i)
    {
        // the
        if (buffer[i] == ','
            && buffer[i+1] == ' '
            && buffer[i+2] == 'T'
            && buffer[i+3] == 'h'
            && buffer[i+4] == 'e'
            && buffer[i+5] == ' '
            && (buffer[i+6] == '(' || buffer[i+6] == '['))
        {
            strcpy(clone, buffer);

            k = 4;
            for (j=0; j != i; ++j)
            {
                buffer[k] = clone[j];
                ++k;
            }
            buffer[0] = 'T';
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
            && buffer[i+2] == 'A'
            && buffer[i+3] == ' '
            && buffer[i+4] == '(')
        {
            strcpy(clone, buffer);

            k = 2;
            for (j=0; j != i; ++j)
            {
                buffer[k] = clone[j];
                ++k;
            }
            buffer[0] = 'A';
            buffer[1] = ' ';

            for (i=k; buffer[i] != '\0'; ++i)
                buffer[i] = buffer[i+1];

            break;
        }

        // an
        if (buffer[i] == ','
            && buffer[i+1] == ' '
            && buffer[i+2] == 'A'
            && buffer[i+3] == 'n'
            && buffer[i+4] == ' '
            && buffer[i+5] == '(')
        {
            strcpy(clone, buffer);

            k = 3;
            for (j=0; j != i; ++j)
            {
                buffer[k] = clone[j];
                ++k;
            }
            buffer[0] = 'A';
            buffer[1] = 'n';
            buffer[2] = ' ';

            for (i=k; buffer[i] != '\0'; ++i)
                buffer[i] = buffer[i+1];

            break;
        }
    }

    //capatalize certain words in title
    for (i=0; buffer[i] != '('; ++i)
    {
        //: the
        if (buffer[i] == ':'
            && buffer[i+1] == ' '
            && (buffer[i+2] == 't' || buffer[i+2] == 'T')
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
            && (buffer[i+2] == 'a' || buffer[i+2] == 'A')
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
    }
}

