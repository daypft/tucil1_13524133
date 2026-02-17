package com.tucil1.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tucil1.util.Cell;

public class Board {
    private int size;
    private char[][] color;
    private boolean[][] queens;
    private Map<Character, List<Cell>> cells;

    public Board(int size){
        this.size = size;
        this.color = new char[size][size];
        this.queens = new boolean[size][size];
        this.cells = new HashMap<>();
    }

    public void addCellToRegion(int r, int c, char color) {
        this.color[r][c] = color;
        this.cells.putIfAbsent(color, new ArrayList<>());
        this.cells.get(color).add(new Cell(r, c, color));
    }
    public List<Character> getAllRegions() {
        return new ArrayList<>(cells.keySet());
    }

    public char getRegion(int row, int col) {
        return color[row][col];
    }

    public int getSize(){
        return this.size;
    }

    public void placeQueen(int row, int col){
        this.queens[row][col] = true;
    }

    public void removeQueen(int row, int col){
        this.queens[row][col] = false;
    }
    public boolean[][] getQueens() {
        return queens;
    }

    public synchronized boolean[][] copyQueens() {
        int n = queens.length;
        boolean[][] copy = new boolean[n][n];
        for (int row = 0; row < n; row++) {
            System.arraycopy(queens[row], 0, copy[row], 0, n);
        }
        return copy;
    }

    public boolean isValid(){
        int count = 0;
        for (int r = 0; r < size; r++) {
        for (int c = 0; c < size; c++) {
                if (queens[r][c]) count++;
            }
        }
        if (count != size) return false;
        for (int r1 = 0; r1 < size; r1++){
            for(int c1 = 0; c1 < size; c1++){
                if(queens[r1][c1]) {
                    for(int r2 = 0; r2 < size; r2++){
                        for(int c2 = 0; c2 < size; c2++){
                            if (r1 == r2 && c1 == c2) continue;
                            if (queens[r2][c2]) {
                                if (r1 == r2) return false;
                                if (c1 == c2) return false;
                                if (color[r1][c1] == color[r2][c2]) return false;

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


    //Untuk CLI
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (queens[i][j]) {
                    sb.append("# ");
                } else {
                    sb.append(color[i][j]).append(" ");
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
                System.out.print(color[i][j] + " ");
                }
            }
            System.out.println();
        }
    }
    
}
