package com.timjaroch;

import java.io.*;
import java.lang.System;
import java.util.*;
import org.jgrapht.*;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.*;

public class GraphGenerator {
    private HashMap<Integer, Integer> jd_map;
    private HashMap<Integer, Integer> loc_map;
    private HashMap<Integer, Pair> indexed_loc_map;
    private HashMap<Integer, Pair> indexed_jd_map;
    private HashSet<Integer> jd_set;
    private HashSet<Integer> loc_set;
    private ArrayList<Integer> jd_list;
    private ArrayList<Integer> loc_list;

    private UndirectedGraph<Node, DefaultEdge> graph = new SimpleGraph<Node, DefaultEdge>(DefaultEdge.class);

    private enum EdgeRule { COMPATIBLE, INCOMPATIBLE }
    private enum DiagnosticRule { COUNT_ONLY, PRINT_ALL, NO_PRINT }

    private int vertices;
    private int edges;

    private static GraphGenerator gen;

    private static final String WORKING_DIR = "\\GitHub\\JumpDistance\\data\\";
    private static final int LINES_TO_READ = 11;

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
        //createJumpSet(jd_map, loc_map, DiagnosticRule.NO_PRINT);
        addVertices(EdgeRule.INCOMPATIBLE);
        //addEdges(EdgeRule.COMPATIBLE);
        addEdges(EdgeRule.INCOMPATIBLE);
        BronKerboschCliqueFinder finder = new BronKerboschCliqueFinder(graph);
        Collection<Set<Integer>> result = finder.getBiggestMaximalCliques();
        System.out.println(result.toString());
        //TreeSet<Node> jS1 = createJumpSet(jd_map, loc_map, DiagnosticRule.COUNT_ONLY);
        //TreeSet<Edge> eS1 = createEdgeSet(jS1, EdgeRule.COMPATIBLE, DiagnosticRule.COUNT_ONLY);
        //HashSet<Edge> eS2 = createEdgeSet(jS1, EdgeRule.INCOMPATIBLE, DiagnosticRule.PRINT_ALL);
        //writeFile(eS1, jS1, "eS1.mis");
    }

    private void readTrace(String fileName){
        jd_map = new HashMap<Integer, Integer>();
        loc_map = new HashMap<Integer, Integer>();
        indexed_loc_map = new HashMap<Integer, Pair>();
        indexed_jd_map = new HashMap<Integer, Pair>();
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
            do {
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
            } while (oneLine != null && linesRead != LINES_TO_READ);

            /** Account for end of file and track final location **/
            loc_list.add(nextLoc);
            loc_set.add(nextLoc);

            if (loc_map.containsKey(nextLoc)){
                loc_map.put(nextLoc, loc_map.get(nextLoc)+1);
            } else
                loc_map.put(nextLoc, 1);


            /** Create indexed maps **/
            int item;
            Collections.sort(loc_list);
            for (int index = 0; index < loc_list.size(); index++){
                item = loc_list.get(index);
                if (indexed_loc_map.containsKey(item)){
                    indexed_loc_map.put(item, new Pair(indexed_loc_map.get(item).count + 1, indexed_loc_map.get(item).index));
                } else
                    indexed_loc_map.put(item, new Pair(1, index));
            }

            Collections.sort(jd_list);
            for (int index = 0; index < jd_list.size(); index++){
                item = jd_list.get(index);
                if (indexed_jd_map.containsKey(item)){
                    indexed_jd_map.put(item, new Pair(indexed_jd_map.get(item).count + 1, indexed_jd_map.get(item).index));
                } else
                    indexed_jd_map.put(item, new Pair(1, index));
            }

        } catch (Exception e){
            System.out.println(e);
            System.out.println("Error Reading File");
            System.out.print("Exiting...");
            System.exit(1);
        }
        System.out.println("Jump Distance and Location Histograms Created Successfully.");
    }

    private boolean writeFile(TreeSet<Edge> edgeSet, TreeSet<Node> nodeSet, String fileName){
        Edge edge;
        try{
            File outputFile = new File(fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

            writer.write("p edge " + loc_set.size() + " " + edgeSet.size());
            //System.out.print("p edge " + loc_set.size() + " " + edgeSet.size());

            Iterator<Edge> iter = edgeSet.iterator();
            while (iter.hasNext()){
                edge = iter.next();
                writer.write("\ne " + (nodeSet.headSet(edge.n1).size()+1) + " " + (nodeSet.headSet(edge.n2).size()+1));
                //System.out.print("\ne " + (nodeSet.headSet(edge.n1).size()+1) + " " + (nodeSet.headSet(edge.n2).size()+1));
            }
            writer.newLine();
            writer.close();
        } catch (Exception e){
            System.out.println(e);
            System.out.println("Error Writing File");
            System.out.print("Exiting...");
            System.exit(1);
        }

        return true;
    }

    private TreeSet<Node> createJumpSet(HashMap<Integer, Integer> jumps, HashMap<Integer, Integer> locations, DiagnosticRule dRule) {
        TreeSet<Node> jumpSet = new TreeSet<Node>();
        Iterator<Integer> locIter = indexed_loc_map.keySet().iterator();
        Iterator<Integer> jdIter;

        int sl, jd, el;     //sl = starting point, jd = jump distance, el = ending point
        while (locIter.hasNext()){
            sl = locIter.next();
            jdIter = indexed_jd_map.keySet().iterator();
            while (jdIter.hasNext()){
                jd = jdIter.next();
                el = sl + jd;
                if (indexed_loc_map.containsKey(el)){
                    for (int i = 0; i < indexed_loc_map.get(sl).count; i++){
                        for (int j = 0; j < indexed_jd_map.get(jd).count; j++){
                            for (int k = 0; k < indexed_loc_map.get(el).count; k++){
                                jumpSet.add(new Node(indexed_loc_map.get(sl).index + i, indexed_jd_map.get(jd).index + j, indexed_loc_map.get(el).index + k));
                                graph.addVertex(new Node(indexed_loc_map.get(sl).index + i, indexed_jd_map.get(jd).index + j, indexed_loc_map.get(el).index + k));
                            }
                        }
                    }
                }
            }
        }

        if (dRule == DiagnosticRule.COUNT_ONLY){
            System.out.println("Set size: " + jumpSet.size());
        } else if (dRule == DiagnosticRule.PRINT_ALL){
            System.out.println("Set size: " + jumpSet.size());
            for (Node n: jumpSet){
                System.out.println(n.toString());
            }
            System.out.println();
        }
        return jumpSet;

    }

    private void addVertices(EdgeRule eRule){
        //System.out.println("Size of locList:"+loc_list.size());
        //System.out.println("Size of jdList:"+jd_list.size());
        //Iterator<Integer> locIter = indexed_loc_map.keySet().iterator();
        Iterator<Integer> locIter = loc_map.keySet().iterator();
        Iterator<Integer> jdIter;

        int sl, jd, el;     //sl = starting point, jd = jump distance, el = ending point
        while (locIter.hasNext()){
            sl = locIter.next();
            jdIter = jd_map.keySet().iterator();
            while (jdIter.hasNext()){
                jd = jdIter.next();
                el = sl + jd;
                if (loc_map.containsKey(el)){
                    for (int i = 0; i < loc_map.get(sl); i++){
                        for (int j = 0; j < jd_map.get(jd); j++){
                            for (int k = 0; k < loc_map.get(el); k++){
                                graph.addVertex(new Node(sl, jd, el));
                            }
                        }
                    }
                }
            }
        }
    }

    private void addEdges(EdgeRule eRule){
        for (Node n1: graph.vertexSet()){
            for (Node n2: graph.vertexSet()){
                if (eRule == EdgeRule.COMPATIBLE){
                    /** v1 and v2 have an edge between them if (v1.start != v2.start && v1.jd != v2.jd && v1.end != v2.end) **/
                    if ((n1.startLoc != n2.startLoc) && (n1.jumpDistance != n2.jumpDistance) && (n1.endLocation != n2.endLocation)){
                        graph.addEdge(n1, n2);
                    }
                } else {
                    /** v1 and v2 have an edge between them if v1.start == v2.start || v1.jd == v2.jd || v1.end == v2.end) **/
                    if (((n1.startLoc == n2.startLoc) || (n1.jumpDistance == n2.jumpDistance) || (n1.endLocation == n2.endLocation)) && ( ! n1.equals(n2))) {
                        graph.addEdge(n1, n2);
                    }
                }
            }
        }
        //System.out.println(eRule.toString());
        System.out.println(graph.toString());
        //System.out.println("Vertices: "+graph.vertexSet().size());
        //System.out.println("edges:" +graph.edgeSet().size());

    }

    private TreeSet<Edge> createEdgeSet(TreeSet<Node> jumpSet, EdgeRule eRule, DiagnosticRule dRule){
        HashSet<Edge> edgeHashSet = new HashSet<Edge>();
        TreeSet<Edge> edgeTreeSet = new TreeSet<Edge>();

            for (Node n1 :jumpSet){
                for (Node n2: jumpSet){
                    if (eRule == EdgeRule.COMPATIBLE){
                        /** v1 and v2 have an edge between them if (v1.start != v2.start && v1.jd != v2.jd && v1.end != v2.end) **/
                        if ((n1.startLoc != n2.startLoc) && (n1.jumpDistance != n2.jumpDistance) && (n1.endLocation != n2.endLocation)){
                            //if (!edgeSet.contains(new Edge(n1, n2))){
                            edgeHashSet.add(new Edge(n1, n2));
                            edgeTreeSet.add(new Edge(n1, n2));
                            //}
                        }
                    } else {
                        /** v1 and v2 have an edge between them if v1.start == v2.start || v1.jd == v2.jd || v1.end == v2.end) **/
                        if (((n1.startLoc == n2.startLoc) || (n1.jumpDistance == n2.jumpDistance) || (n1.endLocation == n2.endLocation)) && ( ! n1.equals(n2))) {
                            //if (!edgeSet.contains(new Edge(n1, n2))){
                            edgeHashSet.add(new Edge(n1, n2));
                            edgeTreeSet.add(new Edge(n1, n2));
                            //}
                        }
                    }
                }
            }

        if (dRule == DiagnosticRule.COUNT_ONLY){
            System.out.println("Number of edges =" + edgeTreeSet.size() + " using "+eRule.toString() + " rule.");
        } else if (dRule == DiagnosticRule.PRINT_ALL) {
            System.out.println("Number of edges =" + edgeTreeSet.size() + " using "+eRule.toString() + " rule.");
            for (Edge e: edgeTreeSet){
                System.out.println(e.n1.toString()+":"+e.n2.toString());
            }
        }

        return edgeTreeSet;
    }
}

class Node implements Comparable<Node>{
    public int startLoc, jumpDistance, endLocation;

    public Node(){}

    public Node(int start, int jump, int end){
        this.startLoc = start;
        this.jumpDistance = jump;
        this.endLocation = end;
    }

    public boolean equals(Node o){
        if (this.startLoc == o.startLoc && this.jumpDistance == o.jumpDistance && this.endLocation == o.endLocation)
            return true;

        return false;
    }

    @Override
    public int compareTo(Node o) {
        if (this.startLoc != o.startLoc) {
            return this.startLoc - o.startLoc;
        } else if (this.jumpDistance != o.jumpDistance) {
            return this.jumpDistance - o.jumpDistance;
        } else {
            return this.endLocation - o.endLocation;
        }
    }

    @Override
    public String toString(){
        return startLoc+"."+jumpDistance+"."+endLocation;
        //return "("+startLoc+","+jumpDistance+","+endLocation+")";
    }
}

class Edge implements Comparable<Edge>{
    public Node n1, n2;

    public Edge(){}

    public Edge(Node n1, Node n2){
        if (n1.compareTo(n2) > 0){
            this.n2 = n1;
            this.n1 = n2;
        } else {
            this.n1 = n1;
            this.n2 = n2;
        }
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object arg0) {
        return true; /** assumes no hashcode collisions, not safe on larger data sets **/
    }

    @Override
    public String toString(){
        return n1.toString()+":"+n2.toString();
    }

    @Override
    public int compareTo(Edge o) {
        if (this.n1.compareTo(o.n1) != 0){
            return this.n1.compareTo(o.n1);
        }
        return this.n2.compareTo(o.n2);  //To change body of implemented methods use File | Settings | File Templates.
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
 }


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

 long startTime = System.currentTimeMillis();
 long stopTime = System.currentTimeMillis();
 long runTime = stopTime - startTime;
 System.out.println("Run time: " + runTime);


 **/
