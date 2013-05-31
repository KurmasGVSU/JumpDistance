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
size = len(jumpDistanceList)
while (len(jumpDistanceList) != 0):
    counter = counter + 1
    firstLoc = random.choice(locationHistLib.keys())
    visits = locationHistLib.get(firstLoc)
    #print "Disc Index:"+str(firstLoc)+" has ("+str(visits)+") visits"
    while True:
        randIndex = random.randint(0,len(jumpDistanceList) -1)
        distance = jumpDistanceList[randIndex]
        jumpLoc = (firstLoc+distance) % discSize
        if jumpLoc != firstLoc:
            break
    
    if jumpLoc in locationHistLib:
        #print "true"
        listOfSets.append([firstLoc,jumpLoc,distance])
        print "["+str(firstLoc)+","+str(jumpLoc)+","+str(distance)+"] :"+str(len(jumpDistanceList))+"/"+str(size)
        jumpDistanceList.remove(distance)
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
        #cls()
        #print counter
        if (counter < -1) or (len(jumpDistanceList) < 5000):
            break

#print listOfSets
print "\nlimit reached, breaking..."
print str(len(listOfSets))+" sets found after: "+str(counter)+" passes"
print ("\n**Done!")

