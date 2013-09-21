#!/usr/bin/env python

##
# temporary script to change all DEFAULT ids to actual ids in numeric order
#
# For crew person, count must be initialized to 0

import sys
import re
import string

count = 1
for line in sys.stdin:
  newline = re.sub('\(DEFAULT,', '(' + str(count) + ',', line)
  print newline,
  count = count + 1
