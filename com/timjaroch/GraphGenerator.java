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

    private UndirectedGraph<Node, DefaultEdge> graph;

    private enum EdgeRule { COMPATIBLE, INCOMPATIBLE }
    private enum DiagnosticRule { COUNT_ONLY, PRINT_ALL, NO_PRINT }

    private int vertices;
    private int edges;

    private static GraphGenerator gen;

    private static final int LINES_TO_READ = 11;

    public static void main(String[] args) {
        gen = new GraphGenerator();
        gen.run(args);
    }

    public GraphGenerator(){}

    public void run(String[] args){
        if (args.length == 1){
            System.out.println("Running in standard mode!");
            System.out.println("attempting to open '"+args[0]+"\'");
            File f;
            if ((f = new File(args[0])).exists()) {
                readTrace(f, -1, -1);
            }
        } else if (args.length == 2){
            System.out.println("Running in \'Time Test\' mode!");
            System.out.println("attempting to open '"+args[0]+"\'");
            File f;
            if ((f = new File(args[0])).exists()) {
                System.out.println("File exists...");
                timeTest(f);
            }
        } else if (args.length == 3) {
            System.out.println("Running in \'Block\' mode!");
            int startIndex = -1, lines = -1;
            try {
                startIndex = Integer.parseInt(args[1]);
                lines = Integer.parseInt(args[2]);
                System.out.println("Starting index set to:"+startIndex+" will attempt to read "+lines+" lines.");
            } catch (NumberFormatException e){
                System.out.println("Arguments must be in the following format");
                System.out.println("Filename startingIndex linesToRead");
                System.out.println("Where startingIndex and linesToRead are integers greater than -2");
                System.exit(1);
            } catch (Exception ex){
                System.out.println("Unknown Error Occurred");
                System.out.println(ex);
                System.exit(1);
            }
            System.out.println("Attempting to open \'"+args[0]+"\'");
            File f;
            if ((f = new File(args[0])).exists()) {
                System.out.println("File exists...");
                readTrace(f, startIndex, lines);
            } else {
                System.out.println("That File does not seem to exist\nExiting...");
            }
        } else {
            System.out.println("Invalid number of arguments found\nExiting...");
            System.exit(1);
        }
    }

    private void timeTest(File f){
        System.out.println("Time Test Started...");
        ArrayList<String> outputLines = new ArrayList<String>();
        long startTime, stopTime, compTime = 0, compTotal = 0, incompTime = 0, incompTotal = 0;
        long radix = 10000;
        int lines = 66388;
        BufferedWriter writer;
        //66388
        try{
            System.out.println("Starting Radix:"+radix);
            while(radix > 450){

                for (int r = 0; r < radix; r++){
                    readTrace(f, (int)(r*(lines/radix)), (int)(lines/radix));

                    startTime = System.currentTimeMillis();
                        createGraph(EdgeRule.COMPATIBLE, new SimpleGraph<Node, DefaultEdge>(DefaultEdge.class));
                    stopTime = System.currentTimeMillis();
                    compTime = stopTime - startTime;
                    compTotal += compTime;

                    startTime = System.currentTimeMillis();
                        createGraph(EdgeRule.INCOMPATIBLE, new SimpleGraph<Node, DefaultEdge>(DefaultEdge.class));
                    stopTime = System.currentTimeMillis();
                    incompTime = stopTime - startTime;
                    incompTotal += incompTime;

                    outputLines.add(compTime+";"+incompTime);
                }
                File outputFile = new File("logs"+File.separator+f.getName()+"."+radix+".csv");
                writer = new BufferedWriter(new FileWriter(outputFile));
                writer.write(compTotal+";"+incompTotal+"\n");
                writer.write((compTotal/radix)+";"+(incompTotal/radix));
                for (String s: outputLines){
                    writer.write("\n"+s);
                }

                writer.close();
                System.out.println("radix: "+radix+" is done.");
                if (radix > 1000) radix -= 1000;
                else radix -= 50;
            }
        } catch (Exception e){
            System.out.println("Error Writing File");
            System.out.println(e);
            System.out.print("Exiting...");
            System.exit(1);
        }
    }

    private void writeString(String line){
        try{
            File outputFile = new File("output.log");
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.append(line);
            writer.close();
        } catch (Exception e){
            System.out.println(e);
            System.out.println("Error Writing File");
            System.out.print("Exiting...");
            System.exit(1);
        }

    }

    private boolean readTrace(File f, int start, int lines){
        jd_map = new HashMap<Integer, Integer>();
        loc_map = new HashMap<Integer, Integer>();
        String line = "";
        int index = 0, counter = 0;
        int startLoc, nextLoc;
        try{
            BufferedReader reader = new BufferedReader(new FileReader(f));
            while (index < start && start != -1 && (line = reader.readLine()) != null){
                index++; counter++;
            }
            if (line == "")
                line = reader.readLine();

            startLoc = parseLine(line);

            while (lines != 1){
                if ((line = reader.readLine()) == null)
                    break;

                nextLoc = parseLine(line);
                addJump(startLoc - nextLoc);
                addLocation(startLoc);
                startLoc = nextLoc;
                lines--; counter++;
            }
            addLocation(startLoc); //add last location visited

        } catch (Exception e){
            System.out.println("Error reading trace");
            System.out.println("@ line: "+counter+" line says:"+line);
            System.out.println(e);
            System.exit(0);
        }
        //System.out.println("Jump Distance and Location Histograms Created Successfully.");
        return lines < 0;
    }

    private int parseLine(String line){
        line = line.trim().replaceAll("\\s+", " ");
        try{
            return Integer.parseInt(line.split(" ")[1]);
        } catch (Exception e){
            System.out.println("File not in correct format!");
            System.out.println(line);
            System.out.println(e);
        }
        return -1;
    }

    private void addJump(int jump){
        if (jd_map.containsKey(jump)){
            jd_map.put(jump, jd_map.get(jump)+1);
        } else
            jd_map.put(jump, 1);
    }

    private void addLocation(int location){
        if (loc_map.containsKey(location)){
            loc_map.put(location, loc_map.get(location)+1);
        } else
            loc_map.put(location, 1);
    }

    private void createGraph(EdgeRule eRule, UndirectedGraph<Node, DefaultEdge> graph){
        ArrayList<Node> nodes = new ArrayList<Node>();
        for (Integer sL: loc_map.keySet()){
            for (Integer eL: loc_map.keySet()){
                if (jd_map.containsKey(sL - eL)){
                    for (int i = 0; i < loc_map.get(sL)*loc_map.get(eL)*jd_map.get(sL - eL); i++){
                        graph.addVertex(new Node(sL, sL - eL, eL, i));
                        nodes.add(new Node(sL, sL - eL, eL, i));
                    }
                }
            }
        }
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

        //System.out.println(graph.toString());
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
                fullFilePath =  File.separator+fileName;
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
                                //jumpSet.add(new Node(indexed_loc_map.get(sl).index + i, indexed_jd_map.get(jd).index + j, indexed_loc_map.get(el).index + k));
                                //graph.addVertex(new Node(indexed_loc_map.get(sl).index + i, indexed_jd_map.get(jd).index + j, indexed_loc_map.get(el).index + k));
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
                                //graph.addVertex(new Node(sl, jd, el));
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



 **/
