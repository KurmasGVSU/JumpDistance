import os
import sys
import random

def cls():
    os.system('cls')

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

#discSize = locationHistLib[-1]
discSize = 2790133
print "discSize:"+str(discSize)

listOfSets = []
counter = 0
while (len(jumpDistanceList) != 0):
    counter = counter + 1
    distance = jumpDistanceList[-1]
    #print "Jump Distance:"+str(distance)
    firstLoc = random.choice(locationHistLib.keys())
    visits = locationHistLib.get(firstLoc)
    #print "Disc Index:"+str(firstLoc)+" has ("+str(visits)+") visits"
    jumpLoc = (firstLoc+jumpDistanceList[-1]) % discSize
    #print jumpLoc
    if jumpLoc in locationHistLib:
        #print "true"
        listOfSets.append([firstLoc,jumpLoc,distance])
        #print listOfSets
        jumpDistanceList.remove(distance)
        #remove firstLoc 
        if visits > 1:
            locationHistLib[firstLoc] = locationHistLib.get(firstLoc) -1
        else:
            locationHistLib.pop(firstLoc)
            
        visits = locationHistLib.get(jumpLoc)
        if visits > 1:
            locationHistLib[jumpLoc] = locationHistLib.get(jumpLoc) -1
        else:
            locationHistLib.pop(jumpLoc)

        
    else:
        cls()
        print counter
        if (counter == 5000) or (len(jumpDistanceList) < 100):
            print "limit reached, breaking"
            break

print listOfSets
print ("\n**Done!")

