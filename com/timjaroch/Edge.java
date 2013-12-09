package com.timjaroch;

/**
 * User: Jaroch
 * Date: 12/8/13 @ Time: 9:05 PM
 */
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
