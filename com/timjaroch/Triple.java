package com.timjaroch;

/**
 * User: Jaroch
 * Date: 12/8/13 @ Time: 9:05 PM
 */
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
