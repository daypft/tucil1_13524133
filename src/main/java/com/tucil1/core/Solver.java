package com.tucil1.core;

import java.util.ArrayList;
import java.util.List;
import com.tucil1.core.Board;
import com.tucil1.util.Cell;

public class Solver {
    private Board board;
    private boolean found;
    private long totalCase;
    private List<Character> regionOrder;

    public Solver(Board board) {
        this.board = board;
        this.found = false;
        this.totalCase = 0;
        this.regionOrder = board.getAllRegions();
    }

    public void solve() {
        List<List<Cell>> allRegionsCells = new ArrayList<>();
        for (char color : regionOrder) {
            allRegionsCells.add(board.getCellsInRegion(color));
        }

        int n = allRegionsCells.size();
        int[] indices = new int[n];

        while (true) {
            for (int i = 0; i < n; i++) {
                Cell c = allRegionsCells.get(i).get(indices[i]);
                board.placeQueen(c.getRow(), c.getCol());
            }

            totalCase++;

            if (board.isValidTotal()) {
                found = true;
                return;
            }

            for (int i = 0; i < n; i++) {
                Cell c = allRegionsCells.get(i).get(indices[i]);
                board.removeQueen(c.getRow(), c.getCol());
            }

            int pos = n - 1;
            while (pos >= 0) {
                indices[pos]++;
                if (indices[pos] < allRegionsCells.get(pos).size()) {
                    break;
                }
                indices[pos] = 0;
                pos--;
            }

            if (pos < 0) {
                break;
            }
        }
    }

    public long getTotalCase() {
        return totalCase;
    }

    public boolean isFound() {
        return found;
    }
}