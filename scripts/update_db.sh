#!/bin/bash

source common.sh

LOG_FILE=$FILTH_PATH/logs/update_db.log
TEMP_SQL_FILE=$FILTH_TEMP_PATH/update_db_temp.sql

function getDbRowCount() {
    table=$1
    tableRowCount=`psql -U filth -d filth -t -c "select count(*) from filth.$table;"`
    tableRowCount="$(echo -e "${tableRowCount}" | tr -d '[[:space:]]')" #remove whitespace
    echo -e "--- $1 has $tableRowCount rows in the db" >> $LOG_FILE
}

function getFileLineCount() {
    file="$1.sql"
    fileLineCount=`wc -l $FILTH_SQL_PATH/$file | awk {'print $1'}`
    echo -e "--- $file has $fileLineCount lines" >> $LOG_FILE
}

function doesTableNeedSequenceUpdated() {
    if [ "$1" == "crew_person" -o "$1" == "list" -o "$1" == "movie" -o "$1" == "oscar" -o "$1" == "tag" -o "$1" == "tyler" -o "$1" == "mpaa" -o "$1" == "country" ]
    then
        updateSequence=$TRUE
    else
        updateSequence=$FALSE
    fi
}

function getLastDbId() {
    table=$1
    tableId=$2
    lastDbId=`psql -U filth -d filth -t -c "select $tableId from filth.$table order by $tableId desc limit 1;"`
    lastDbId="$(echo -e "${lastDbId}" | tr -d '[[:space:]]')" #remove whitespace
    echo -e "--- $1's last id is $lastDbId in the db" >> $LOG_FILE
}

function getLastFileId() {
    file="$1.sql"
    lastFileId=`tac $FILTH_SQL_PATH/$file | egrep -m 1 . | sed -E "s/.*\(([0-9]+),.*/\\1/"`
    echo -e "--- $file's last id is $lastFileId" >> $LOG_FILE
}

function updateSequence() {
    table=$1
    tableId=$2
    sequenceValue=$3
    sequence=${table}_${tableId}_seq
    echo -e "... updating sequence: psql -U filth -d filth -c \"select setval('filth.$sequence', $sequenceValue);\"" >> $LOG_FILE
    psql -U filth -d filth -c "select setval('filth.$sequence', $sequenceValue);" > /dev/null 2>>$LOG_FILE
}

function updateTable() {
    echo "Processing $1..."; sleep 0.5;
    echo -e "\n$1" >> $LOG_FILE

    doesTableNeedSequenceUpdated $1

    if [ "$updateSequence" -eq "$TRUE" ]
    then
        getLastDbId $1 $2
        getLastFileId $1

        if [ "$lastFileId" -gt "$lastDbId" ]
        then
            echo "Updating $1..."; sleep 0.5;
            difference=`expr $lastFileId - $lastDbId`
            echo -e ">>> New data detected for $1: lastFileId - lastDbid = $difference" >> $LOG_FILE

            getFileLineCount $1
            i=1
            id=$lastFileId
            while [ "$id" -gt "$lastDbId" ]
            do
                i=$((i + 1))
                lineNum=`expr $fileLineCount - $i + 1`
                line=`sed -n -e ${lineNum}p $FILTH_SQL_PATH/$1.sql`
                echo -e "\t... Read line $lineNum from $FILTH_SQL_PATH/$1.sql: $line" >> $LOG_FILE
                id=`echo $line | sed -E "s/.*\(([0-9]+),.*/\\1/"`
                echo -e "\t--- id from last line: $id" >> $LOG_FILE
            done
            i=$((i - 1))
            echo -e "--- Last $i lines from $FILTH_SQL_PATH/$1.sql:" >> $LOG_FILE
            echo "SQL to be executed:"
            tail -n $i $FILTH_SQL_PATH/$1.sql | tee -a $LOG_FILE

            tail -n $i $FILTH_SQL_PATH/$1.sql > $TEMP_SQL_FILE
            echo -e "... Executing last $i lines from $FILTH_SQL_PATH/$1.sql in db" >> $LOG_FILE
            psql -U filth -d filth -f $TEMP_SQL_FILE > /dev/null 2>>$LOG_FILE

            echo "Added $i rows to $1."; sleep 0.5;

            updateSequence $1 $2 $lastFileId
            echo "Updated id sequence for $1."; sleep 0.5;
        else
            echo "No update necessary for $1."; sleep 0.5;
            echo -e ">>> Nothing new for $1, skipping" >> $LOG_FILE
        fi
    else
        getDbRowCount $1
        getFileLineCount $1

        if [ "$fileLineCount" -gt "$tableRowCount" ]
        then
            echo "Updating $1..."; sleep 0.5;
            numLines=`expr $fileLineCount - $tableRowCount`

            echo -e ">>> New data detected for $1: fileLineCount - tableRowCount = $numLines" >> $LOG_FILE
            echo -e "--- Last $numLines lines from $FILTH_SQL_PATH/$1.sql:" >> $LOG_FILE

            echo "SQL to be executed:"
            tail -n $numLines $FILTH_SQL_PATH/$1.sql | tee -a $LOG_FILE

            tail -n $numLines $FILTH_SQL_PATH/$1.sql > $TEMP_SQL_FILE
            echo -e "... Executing last $numLines lines from $FILTH_SQL_PATH/$1.sql in db" >> $LOG_FILE
            psql -U filth -d filth -f $TEMP_SQL_FILE > /dev/null 2>>$LOG_FILE

            echo "Added $numLines rows to $1."; sleep 0.5;
        else
            echo "No update necessary for $1."; sleep 0.5;
            echo -e ">>> nothing new for $1, skipping" >> $LOG_FILE
        fi
    fi
}

> $LOG_FILE # clear the log file

# integrity tables
updateTable "country"
updateTable "movie_link_type"
updateTable "mpaa"
updateTable "position"
updateTable "star_rating"
# entity tables
updateTable "crew_person" "cid"
updateTable "list" "lid"
updateTable "movie" "mid"
updateTable "oscar" "oid"
updateTable "tag" "tid"
updateTable "tyler" "tid"
# relationship tables
updateTable "list_contains"
updateTable "movie_link"
updateTable "oscar_given_to"
updateTable "tag_given_to"
updateTable "tyler_given_to"
updateTable "worked_on"
