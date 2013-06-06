#number of vertices
#number of edges
#maximum degree (most edges on a single vertex
#average degree (avg edges across all vertices

import os
import sys
import networkx as nx

def cls():
    os.system('cls')

if len(sys.argv) < 2:
    print("Incorrect # of Input Arguments Found, closing...")
    sys.exit(0)

file1 = sys.argv[1]
graph = nx.Graph()



#open files in read mode, file is assumed a txt file
print("Attempting to open: '"+file1+"'")
graphFile = open(file1)
print("File Successfully opened")

for line in graphFile:
    oneLine = line.split()
    oneLine.reverse()
    startLoc = int(oneLine.pop())
    #print("sLoc:"+str(startLoc))
    jd = int(oneLine.pop())
    #print("jd:"+str(jd))
    endLoc = int(oneLine.pop())
    #print("eLoc:"+str(endLoc))
    if (startLoc+jd == endLoc):
        graph.add_node(startLoc)
        #graph.add_node(jd)
        graph.add_node(endLoc)
        #graph.add_edge(startLoc,jd)
        #graph.add_edge(jd,endLoc)
        graph.add_edge(startLoc,endLoc)
        #print(nx.info(graph))

max = 0
for node in graph:
    degree = nx.degree(node)
    if degree > max:
        max = degree

print(nx.info(graph))
print("done!")
