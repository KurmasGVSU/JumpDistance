import os
#CD to "DataFiles" folder
os.chdir("DataFiles")
#open JD in read mode, file is a txt file
f = open("jumpDistanceHist")
for line in f:
    lines = f.readline()
    print (lines)
f.close()
print ("**Done!")
