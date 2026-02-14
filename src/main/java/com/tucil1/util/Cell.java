package com.tucil1.util;

public class Cell {
    private int row;
    private int col;
    private char region;

    // Constructor
    public Cell (int row, int col, char region) {
        this.row = row;
        this.col = col;
        this.region = region;
    }

    public int getRow(){
        return this.row;
    }

    public int getCol(){
        return this.col;
    }

    public char getRegion(){
        return this.region;
    }
}
