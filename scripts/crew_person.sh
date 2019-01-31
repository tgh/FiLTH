#!/bin/bash

source "$(dirname $0)/common.sh"

FILTH_PATH=~/workspace/FiLTH
FILTH_TEMP_PATH=~/workspace/FiLTH/temp

$FILTH_PATH/bin/nameFormatter $FILTH_PATH/data/crew_person.txt > $FILTH_TEMP_PATH/temp
$FILTH_PATH/scripts/crew2sql.py $FILTH_TEMP_PATH/temp > $FILTH_PATH/sql/crew_person.sql
