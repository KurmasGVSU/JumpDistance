package com.timjaroch;

import java.io.*;
import java.lang.System;
import java.util.*;

public class GraphGenerator {
    private HashMap<Integer, Integer> jd_map;
    private HashMap<Integer, Integer> loc_map;
    private HashSet<Integer> jd_set;
    private HashSet<Integer> loc_set;
    private ArrayList<Integer> jd_list;
    private ArrayList<Integer> loc_list;

    private enum EdgeRule { COMPATIBLE, INCOMPATIBLE }

    private int vertices;
    private int edges;

    private static GraphGenerator gen;

    private static final String WORKING_DIR = "\\GitHub\\JumpDistance\\data\\";
    private static final int LINES_TO_READ = 200;

    public static void main(String[] args) {
        gen = new GraphGenerator();
        gen.run(args);
    }

    public GraphGenerator(){}

    public void run(String[] args){
        if (args.length == 0){
            System.out.println("No arguments specified exiting..");
        } else {
            System.out.println("Arguments found, attempting to open \'"+ WORKING_DIR +args[0]+"\'");
            gen.readTrace(args[0]);     //gen.readTrace("OpenMail_LU056000_trace");
            //createJumpSet(jd_map, loc_map);
            //System.out.println(createEdgeSet(createJumpSet(jd_map, loc_map), 0).size());
        }
        HashSet<Node> jS1 = createJumpSet(jd_map, loc_map);
        HashSet<Edge> eS1 = createEdgeSet(jS1, EdgeRule.COMPATIBLE, true);
        HashSet<Edge> eS2 = createEdgeSet(jS1, EdgeRule.INCOMPATIBLE, true);
        System.out.println(eS1.size());
        System.out.println(eS2.size());
        //this.loc_Map = createMap(loc_Hist);
        //writeFile(correlatedGraph(loc_Hist, jd_hist), "output.data");
        //System.out.println("DONE!!!!!");
    }

    private void readTrace(String fileName){
        jd_map = new HashMap<Integer, Integer>();
        loc_map = new HashMap<Integer, Integer>();
        jd_set = new HashSet<Integer>();
        loc_set = new HashSet<Integer>();
        jd_list = new ArrayList<Integer>();
        loc_list = new ArrayList<Integer>();
        String oneLine;
        int startLoc, nextLoc, jump, linesRead = 0;
        try{
	    String fullFilePath = fileName;
	    if (!fileName.startsWith("/")) {
		fullFilePath = WORKING_DIR +File.separator+fileName;
	    }
            File inputFile = new File(fullFilePath);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            System.out.println("'"+inputFile.getName()+"' found and successfully opened!");
            oneLine = reader.readLine();
            oneLine = oneLine.trim().replaceAll("\\s+", " ");
            startLoc = Integer.parseInt(oneLine.split(" ")[1]);
            oneLine = reader.readLine(); linesRead++;
            while (oneLine != null && linesRead != LINES_TO_READ){
                oneLine = oneLine.trim().replaceAll("\\s+", " ");
                nextLoc = Integer.parseInt(oneLine.split(" ")[1]);
                jump = startLoc - nextLoc;
                if (jd_map.containsKey(jump)){
                    jd_map.put(jump, jd_map.get(jump)+1);
                } else
                    jd_map.put(jump, 1);

                if (loc_map.containsKey(startLoc)){
                    loc_map.put(startLoc, loc_map.get(startLoc)+1);
                } else
                    loc_map.put(startLoc, 1);

                jd_list.add(jump);
                jd_set.add(jump);
                loc_list.add(startLoc);
                loc_set.add(startLoc);
                startLoc = nextLoc;
                oneLine = reader.readLine(); linesRead++;
            }
        } catch (Exception e){
            System.out.println(e);
            System.out.println("Error Reading File");
            System.out.print("Exiting...");
            System.exit(1);
        }
        System.out.println("Jump Distance and Location Histograms Created Successfully.");
    }

    private boolean writeFile(List<Triple> outputList, String fileName){
        try{
            File outputFile = new File(fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            Iterator<Triple> iter = outputList.iterator();
            Triple tri;
            while (iter.hasNext()){
                tri = iter.next();
                writer.write(tri.start+" "+tri.mid+" "+ tri.end);
                writer.newLine();
            }
        } catch (Exception e){
            System.out.println(e);
            System.out.println("Error Writing File");
            System.out.print("Exiting...");
            System.exit(1);
        }

        return true;
    }

    private HashSet<Node> createJumpSet(HashMap<Integer, Integer> jumps, HashMap<Integer, Integer> locations) {
        HashSet<Node> jumpSet = new HashSet<Node>();
        Collections.sort(loc_list);
        Collections.sort(jd_list);
        int el;
        for (int sl : loc_list){
            for (int jd : jd_list){
                el = sl+jd;
                if (loc_list.contains(el)){
                    for (int x = loc_list.indexOf(sl); x < loc_list.lastIndexOf(sl); x++){
                        for (int y = jd_list.indexOf(jd); y < jd_list.lastIndexOf(jd); y++){
                            for (int z = loc_list.indexOf(el); z < loc_list.lastIndexOf(el); z++){
                                jumpSet.add(new Node(x,y,z));
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Set size: " + jumpSet.size());
        return jumpSet;
/*
        HashSet<Node> jumpSet = new HashSet<Node>();
        Iterator<Integer> locIter = locations.keySet().iterator();
        Iterator<Integer> jdIter;
        int startLoc, jd, finalLoc;
        int slCounter = 0;
        int jCounter = 0;
        int flCounter = 0;

        while (locIter.hasNext()) {
            startLoc = locIter.next();
            slCounter++;
            jdIter = jumps.keySet().iterator();
            while (jdIter.hasNext()) {
                jd = jdIter.next();
                jCounter++;
                if (jd != 0){
                    finalLoc = startLoc + jd;
                    if (locations.containsKey(finalLoc)) {

                        for (int i = slCounter; i < slCounter + locations.get(startLoc); i++){
                            for (int j = jCounter; j < jCounter + locations.get(jd); j++){
                                for (int k = flCounter; k < flCounter + locations.get(finalLoc); k++){
                                    jumpSet.add(new Node (i, j, k));
                                }
                            }
                        }
		                //copies = (locations.get(startLoc)*jumps.get(jd)*locations.get(finalLoc));
                        //vertices = vertices+copies;

                        //for (int copy = copies; copy > 0; copy--){
                        //jumpSet.add(new Node(startLoc, jd, finalLoc));
                        //System.out.println("Node:"+startLoc+","+jd+","+finalLoc+","+copy);
                        //}
                    }
                }
                jCounter = jCounter + locations.get(jd);
            }
            slCounter = slCounter + locations.get(startLoc);
        }
    //vertices = jumpSet.size();
    System.out.println("Vertices: " + vertices);
	System.out.println("Set size: " + jumpSet.size());
	return jumpSet;
	*/
    }

    private HashSet<Edge> createEdgeSet(HashSet<Node> jumpSet, EdgeRule type, boolean countOnly){
        HashSet<Edge> edgeSet = new HashSet<Edge>();
	    int count = 0;
            for (Node n1 :jumpSet){
                for (Node n2: jumpSet){
                    if (type == EdgeRule.COMPATIBLE){
                        if ((n1.startLoc != n2.startLoc) && (n1.jumpDistance != n2.jumpDistance) && (n1.endLocation != n2.endLocation)){
                            if (!countOnly)  {
                                edgeSet.add(new Edge(n1, n2));
                            }
                            count++;
                        }
                    } else {
                        if ((n1.startLoc == n2.startLoc) || (n1.jumpDistance == n2.jumpDistance) || (n1.endLocation == n2.endLocation)) {
                            if (!countOnly) {
                                edgeSet.add(new Edge(n1, n2));
                            }
                            count++;
                        }
                    }
                    if (count % 1000000 == 0) {
                        System.out.print(".");
                    }
                }
            }

	    System.out.println("\nNumber of edges: " + count);
        return edgeSet;
    }

    /**
     1)
     v1 and v2 have an edge between them if (v1.start != v2.start && v1.jd != v2.jd && v1.end != v2.end)

     2)
     v1 and v2 have an edge between them if v1.start == v2.start || v1.jd == v2.jd || v1.end == v2.end)
     */
}

class Node implements Comparable<Node>{
    public int startLoc, jumpDistance, endLocation;

    public Node(){}

    public Node(int start, int jump, int end){
        this.startLoc = start;
        this.jumpDistance = jump;
        this.endLocation = end;
    }

    @Override
    public int compareTo(Node o) {
        return startLoc - o.startLoc;
    }
}

class Edge{
    public Node n1, n2;

    public Edge(){}

    public Edge(Node n1, Node n2){
        this.n1 = n1;
        this.n2 = n2;
    }

    @Override
    public String toString(){
        String val = n1.startLoc+","+n2.startLoc;
        return val;
    }
}

class Triple implements Comparable<Triple> {
    public int start, mid, end;

    public Triple(){}

    public Triple(int start, int mid, int end){
        this.start = start;
        this.mid = mid;
        this.end = end;
    }

    @Override
    public int compareTo(Triple o) {
        return this.start - o.start;
    }
}
class Pair implements Comparable<Pair>{
    public int count, index;

    public Pair(){}

    public Pair(int count, int index){
        this.count = count;
        this.index = index;
    }

    @Override
    public int compareTo(Pair o) {
        return this.index - o.index;
    }
}

/**
 * \/ OLD CODE \/
 */

/**
 private List<Pair> readFile(String fileName, String identifier){
 List<Pair> histogram = new LinkedList<Pair>();
 String oneLine;
 int count, location;
 try{
 File inputFile = new File(fileName);
 BufferedReader reader = new BufferedReader(new FileReader(inputFile));
 System.out.println("'"+inputFile.getName()+"' successfully opened!");
 oneLine = reader.readLine();
 while(oneLine != null){
 oneLine = oneLine.trim();
 count = Integer.parseInt(oneLine.split(" ")[0]);
 location = Integer.parseInt(oneLine.split(" ")[1]);
 //System.out.println(oneLine+": count:"+count+identifier+location);
 histogram.add(new Pair(count, location));
 oneLine = reader.readLine();
 }
 System.out.println("All lines successfully read!");
 } catch (Exception e){
 System.out.println(e);
 System.out.println("Error Reading File");
 System.out.print("Exiting...");
 System.exit(1);
 }
 return histogram;
 }**/
