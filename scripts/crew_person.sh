#!/bin/bash

filth_path=~/Projects/FiLTH
filth_temp_path=~/Projects/FiLTH/temp

$filth_path/bin/nameFormatter $filth_path/data/crew_person.txt > $filth_temp_path/temp
$filth_path/scripts/crew2sql.py $filth_temp_path/temp > $filth_path/sql/crew_person.sql