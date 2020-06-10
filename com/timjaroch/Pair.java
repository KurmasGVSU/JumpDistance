package com.timjaroch;

/**
 * User: Jaroch
 * Date: 12/8/13 @ Time: 9:05 PM
 */
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
