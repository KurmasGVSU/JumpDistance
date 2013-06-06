FileReader.py expects two arguments

argv[1] is the "Jump Distance Histogram"
the format expected is:
#qty #distance
Example:
5 -512451
2 379088


argv[2] is the "Location Histogram"
the format expected is:
#qty #location
Example:
234 2816
131 2832





The File is simply terminated by end of file or a blank line

Currently this script does not check the integrity of the data files
They are assumed sorted and without double whitespaces etc.
Sorting needs to be done based on the "distance" or "location" integer