import os
import sys
import random
import timeit

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
jdList = []
for line in jumpDistanceFile:
    oneLine = line.split()
    oneLine.reverse()
    qty = int(oneLine.pop())
    distance = int(oneLine.pop())
    for x in range(0, qty):
        jdList.append(distance)

#parse locationHistFile
locDict = {}
for line in locationHistFile:
    oneLine = line.split()
    oneLine.reverse()
    qty = int(oneLine.pop())
    loc = int(oneLine.pop())
    locDict[loc] = qty

##locList = []
##for line in locationHistFile:
##    oneLine = line.split()
##    oneLine.reverse()
##    oneLine.pop() #throw away the qty
##    loc = int(oneLine.pop())
##    locList.append(loc)

print("Input Libaries Created!")
print("Attempting Brute Force Calculation of Trace...")
f = open("output.data", 'w') 
indepSetStack = []
startTime = timeit.default_timer()
for startLoc in locDict:
    checkTime = timeit.default_timer()
    print "Time Elapsed:"+str(checkTime - startTime)
    #insert file write and dump stack to prevent memory overflow
    for jd in jdList:
        if (startLoc+jd) in locDict.keys():
            set = [startLoc,startLoc+jd,jd]
            indepSetStack.append(set)


print "Graph created..."
print "printing to file"

f = open("output.data", 'w')         
for triple in indepSetStack:
    f.write(str(triple)+"\n")
f.close()


print ("\n**Done!")

