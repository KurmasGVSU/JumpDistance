#import os
import sys
import random

if len(sys.argv) < 3:
    print("Incorrect # of Input Arguments Found, closing...")
    sys.exit(0)

file1 = sys.argv[1]
file2 = sys.argv[2]


#open files in read mode, file is assumed a txt file
print("Attempting to open: '"+file1+"'")
jumpDistanceFile = open(file1)
print("Attempting to open: '"+file2+"'")
locationHistFile = open(file2)
print("Both files are open!")

#parse jumpDistanceFile
jumpDistanceList = []
for line in jumpDistanceFile:
    oneLine = line.split()
    oneLine.reverse()
    qty = int(oneLine.pop())
    distance = int(oneLine.pop())
    for x in range(0, qty):
        jumpDistanceList.append(distance)

#parse locationHistFile
locationHistLib = {}
for line in locationHistFile:
    oneLine = line.split()
    oneLine.reverse()
    qty = int(oneLine.pop())
    loc = int(oneLine.pop())
    locationHistLib[loc] = qty

print("Input Libaries Created!")
print("Attempting Brute Force Calculation of Trace...\n")

while (len(jumpDistanceList) != 0):
    distance = jumpDistanceList[-1]
    print "Jump Distance:"+str(distance)
    tempkey = random.choice(locationHistLib.keys())
    location = locationHistLib.pop(tempkey)
    print str(location)+" Visits to Disc Index:"+str(tempkey)
    jumpLoc = location+jumpDistanceList[-1]
    #if jumpLoc is in location histogram remove both start and end jumps
    #otherwise get a new start location
    
    break


print ("\n**Done!")

