package com.tucil1;

import com.tucil1.core.Board;
import com.tucil1.core.Parser;
import com.tucil1.core.Solver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Masukkan nama file: ");
        String inputPath = scanner.nextLine();

        Board board = Parser.readBoard(inputPath);

        if (board != null) {
            Solver solver = new Solver(board);
            long startTime = System.currentTimeMillis(); 
            
            solver.solve(); 

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            String status;
            int regionCount = board.getAllRegions().size();
            int n = board.getSize();

            if (solver.isFound()) {
                status = "berhasil";
            } else if (regionCount > n) {
                status = "wilayah lebih dari col&row";
            } else {
                status = "tidak mendapat solusi";
            }

            System.out.println("Hasil: " + status);
            System.out.println("Waktu pencarian: " + duration + " ms");
            System.out.println("Banyak kasus yang ditinjau: " + solver.getTotalCase() + " kasus");
            
            System.out.println("---------------------------------------------");

            if (solver.isFound()) {
                board.printBoard(); 
                System.out.println("---------------------------------------------");

                System.out.print("Apakah Anda ingin menyimpan solusi? (Ya/Tidak): ");
                String answer = scanner.next();
                if (answer.equalsIgnoreCase("Ya") || answer.equalsIgnoreCase("Y")) {
                    saveSolution(inputPath, board);
                }
            }

        } else {
            System.out.println("Error: File tidak ditemukan atau kosong.");
        }

        scanner.close();
    }

    private static void saveSolution(String originalPath, Board board) {
        try {
            File saveDir = new File("save");
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }

            File originalFile = new File(originalPath);
            String fileName = originalFile.getName();
            if (fileName.contains(".")) {
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            }
            
            String outputPath = "save/" + fileName + "_solusi.txt";

            FileWriter writer = new FileWriter(outputPath);
            writer.write(board.toString());
            writer.close();

            System.out.println("Solusi berhasil disimpan di: " + outputPath);

        } catch (IOException e) {
            System.out.println("Gagal menyimpan file: " + e.getMessage());
        }
    }
}