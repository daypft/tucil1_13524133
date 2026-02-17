package com.tucil1.core;

import java.util.Arrays;

public class Solver {
    private Board board;
    private boolean found;
    private long totalCase;

    public Solver(Board board) {
        this.board = board;
        this.found = false;
        this.totalCase = 0;
    }

    public void solve(){
        found = false;
        totalCase = 0;

        int n = board.getSize();
        boolean[][] q = board.getQueens();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (q[i][j]) {
                    board.removeQueen(i, j);
                }
            }
        }

        int[] p = new int[n];
        Arrays.fill(p, -1);

        int i = 0;
        while (i >= 0) {
            boolean adv = false;
            int s = p[i] + 1;

            for (int j = s; j < n; j++){
                totalCase++;

                board.placeQueen(i, j);
                p[i] = j;

                if (i == n - 1) {
                    if (board.isValid()){
                        found = true;
                        return;
                    }
                    board.removeQueen(i, j);
                    p[i] = -1;
                    continue;
                }

                i++;
                p[i] = -1;
                adv = true;
                break;
            }

            if (!adv){
                p[i] = -1;
                i--;
                if (i >= 0){
                    int k = p[i];
                    if (k >= 0) {
                        board.removeQueen(i, k);
                    }
                }
            }
        }
    }

    public long getTotalCase(){
        return totalCase;
    }

    public boolean isFound() {
        return found;
    }
}
