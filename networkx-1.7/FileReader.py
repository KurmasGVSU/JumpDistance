#import os
import sys

if len(sys.argv) < 3:
    print("Incorrect # of Input Arguments Found, closing...")
    sys.exit(0)

file1 = sys.argv[1]
file2 = sys.argv[2]


#open files in read mode, file is assumed a txt file
print("Attempting to open: '"+file1+"'")
f1 = open(file1)
print("Attempting to open: '"+file2+"'")
f2 = open(file2)
print("Both files are open!")
for line in f1:
    lines = f1.readline()
    #print (lines)
f1.close()
print ("**Done!")
