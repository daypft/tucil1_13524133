package com.tucil1.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.tucil1.util.Cell;

public class Board {
    private int size;
    private char[][] colorMap;
    private boolean[][] queens;
    private Map<Character, List<Cell>> regionMap;

    //Cosntructor
    public Board(int size){
        this.size = size;
        this.colorMap = new char[size][size];
        this.queens = new boolean[size][size];
        this.regionMap = new HashMap<>();
    }


    //per wilayah
    public void addCellToRegion(int r, int c, char color) {
        this.colorMap[r][c] = color;
        this.regionMap.putIfAbsent(color, new ArrayList<>());
        this.regionMap.get(color).add(new Cell(r, c, color));
    }
    public List<Character> getAllRegions() {
        return new ArrayList<>(regionMap.keySet());
    }
    public List<Cell> getCellsInRegion(char color) {
        return regionMap.get(color);
    }

    //Testing
    public int getSize(){
        return this.size;
    }

    public void setColor(int row, int col, char color){
        this.colorMap[row][col] = color;
    }

    public void placeQueen(int row, int col){
        this.queens[row][col] = true;
    }

    public void removeQueen(int row, int col){
        this.queens[row][col] = false;
    }

    public boolean isValidTotal(){
        for (int r1 = 0; r1 < size; r1++){
            for(int c1 = 0; c1 < size; c1++){
                if(queens[r1][c1]) {
                    for(int r2 = 0; r2 < size; r2++){
                        for(int c2 = 0; c2 < size; c2++){
                            if (r1 == r2 && c1 == c2) continue;
                            if (queens[r2][c2]) {
                                if (r1 == r2) return false;
                                if (c1 == c2) return false;
                                if (colorMap[r1][c1] == colorMap[r2][c2]) return false;

                                int selisihRow = Math.abs(r1 - r2);
                                int selisihCol = Math.abs(c1 - c2);
                                if (selisihRow <= 1 && selisihCol <=1) return false; 

                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (queens[i][j]) {
                    sb.append("# ");
                } else {
                    sb.append(colorMap[i][j]).append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void printBoard(){
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                if (queens[i][j] == true) {
                    System.out.print("# ");
                } else {
                System.out.print(colorMap[i][j] + " ");
                }
            }
            System.out.println();
        }
    }
    
}
