package com.tucil1.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser {
    public static Board readBoard (String filepath){
        try {
            File file = new File (filepath);
            Scanner reader = new Scanner (file);

            ArrayList <String> lines = new ArrayList<>();
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                if (!data.trim().isEmpty()) {
                    lines.add(data);
                }
            }
            reader.close();

            int n = lines.size();
            if (n <= 0) {
                return null;
            }

            for (String line : lines) {
                if (line.length() != n) {
                    return null;
                }
            }

            Board board = new Board(n);
            
            for (int i = 0; i < n; i++){
                String line = lines.get(i);
                for (int j = 0; j < n; j++){
                    char color = line.charAt(j);
                    board.addCellToRegion(i, j, color);
                }
            }
            return board;

        } catch (FileNotFoundException e){
            System.out.println("File not found: " + e.getMessage());
            return null;
        }
    }
}
