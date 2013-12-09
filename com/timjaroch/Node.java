package com.timjaroch;

/**
 * User: Jaroch
 * Date: 12/8/13 @ Time: 9:05 PM
 */
class Node implements Comparable<Node>{
    public int startLoc, jumpDistance, endLocation, id;

    public Node(){}

    public Node(int start, int jump, int end, int id){
        this.startLoc = start;
        this.jumpDistance = jump;
        this.endLocation = end;
        this.id = id;
    }

    public boolean equals(Node o){
        if (this.startLoc == o.startLoc && this.jumpDistance == o.jumpDistance && this.endLocation == o.endLocation && this.id == o.id)
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
        return "("+startLoc+":"+endLocation+"/"+jumpDistance+" ID:"+id+")";
        //return "("+startLoc+","+jumpDistance+","+endLocation+")";
    }
}