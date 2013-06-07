import java.io.*;
import java.util.*;

public class GraphGenerator {
    private List<Pair> jd_hist;
    //private LinkedList<Pair> JD_histogram;
    private List<Pair> loc_Hist;
    private HashMap<Integer, Integer> loc_Map;
    private static GraphGenerator gen;
    //private Pair start_object;

    public static void main(String[] args) {
        gen = new GraphGenerator();
        gen.run(args);
    }

    public GraphGenerator(){}

    public void run(String[] args){
        if (args.length == 0){
            System.out.println("No arguments specified exiting..");
        } else {
            System.out.println("Arguments found, attempting to open files...");
            this.loc_Hist = gen.readFile(args[0], " Location: ");
            this.jd_hist = gen.readFile(args[1], " Distance: ");
        }
        this.loc_Map = createMap(loc_Hist);
        writeFile(correlatedGraph(loc_Hist, jd_hist), "output.data");
        System.out.println("DONE!!!!!");
    }

    private boolean writeFile(List<Triple> outputList, String fileName){
        try{
            File outputFile = new File(fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            Iterator<Triple> iter = outputList.iterator();
            Triple tri;
            while (iter.hasNext()){
                tri = iter.next();
                writer.write(tri.count + ": " + tri.start+" "+tri.mid+" "+ tri.end);
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

    private List<Triple> correlatedGraph(List<Pair> locHist, List<Pair> jdHist){
        List<Triple> graph = new LinkedList<Triple>();
        Iterator<Pair> locIter = locHist.iterator();
        Iterator<Pair> jdIter;
        int startLoc, jd, finalLoc;
	int startCount, jdCount, finalCount;
	int total_vertices = 0;
	int total_lines = 0;
	// YYY Here you can create a counter for start location, end location, and jump distance.

        while (locIter.hasNext()){
	    Pair start = locIter.next();
            startLoc = start.second;
	    startCount = start.first;
            jdIter = jdHist.iterator();
	    // YYY reset the jump distance counter
            while (jdIter.hasNext()){
		Pair jdPair = jdIter.next();
                jd = jdPair.second;
		jdCount = jdPair.first;
                finalLoc = startLoc+jd;
                if (loc_Map.containsKey(finalLoc)){
                    //System.out.println("["+startLoc+","+jd+","+finalLoc+"]");
		    finalCount = loc_Map.get(finalLoc);
		    int copies = (startCount*jdCount*finalCount);


		    // YYY At this point, instead of creating a single Triple with a count, 
		    // Use a loop to create "copies" Triples, each with its own combination of unique values.
		    // The think you'll have to think most carefully about is when exactly to increment and reset the counters.
                    graph.add(new Triple(startLoc,jd,finalLoc,copies ));
		    total_vertices += copies;
		    total_lines++;
                }
            }
        }
	System.out.printf("Total vertices: %d\tTotal lines:  %d\n", total_vertices, total_lines);
        return graph;
    }


    private HashMap<Integer, Integer> createMap(List<Pair> locHist){
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(locHist.size());
        Iterator<Pair> locIter = locHist.iterator();
        Pair tPair;
        while(locIter.hasNext()){
            tPair = locIter.next();
            map.put(tPair.second, tPair.first);
        }
        System.out.println("Map succesfully created and populated with valid locations");
        return map;
    }
}
class Triple implements Comparable<Triple> {
    public int start, mid, end, count;

    public Triple(){}

    public Triple(int start, int mid, int end){
        this.start = start;
        this.mid = mid;
        this.end = end;
    }

    public Triple(int start, int mid, int end, int count) {
	this(start, mid, end);
	this.count = count;
	   
    }

    @Override
    public int compareTo(Triple o) {
        return this.start - o.start;
    }
}
class Pair implements Comparable<Pair>{
    public int first, second;

    public Pair(){}

    public Pair(int first, int second){
        this.first = first;
        this.second = second;
    }

    @Override
    public int compareTo(Pair o) {
        return this.second - o.second;
    }
}
