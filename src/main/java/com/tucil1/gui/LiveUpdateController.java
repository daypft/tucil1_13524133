package com.tucil1.gui;

import com.tucil1.core.Board;
import com.tucil1.core.Solver;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

//Untuk ngambil snapshot ratu tiap 10000 kasus
public class LiveUpdateController {
    private static final long SNAPSHOT_INTERVAL = 1000;

    private final Board board;
    private final Solver solver;
    private final Thread solveThread;
    private final long startTime;
    private final long refreshMs;
    private final BiConsumer<Long, Long> progressUpdater;
    private final Consumer<boolean[][]> boardUpdater;
    private final AtomicBoolean running;
    private final AtomicBoolean uiPending;

    public LiveUpdateController(
        Board board,
        Solver solver,
        Thread solveThread,
        long startTime,
        long refreshMs,
        BiConsumer<Long, Long> progressUpdater,
        Consumer<boolean[][]> boardUpdater
    ) {
        this.board = board;
        this.solver = solver;
        this.solveThread = solveThread;
        this.startTime = startTime;
        this.refreshMs = refreshMs;
        this.progressUpdater = progressUpdater;
        this.boardUpdater = boardUpdater;
        this.running = new AtomicBoolean(false);
        this.uiPending = new AtomicBoolean(false);
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        Thread updaterThread = new Thread(() -> {
            long lastSnapshotCase = 0;

            while (running.get() && solveThread.isAlive()) {
                long elapsed = System.currentTimeMillis() - startTime;
                long totalCase = solver.getTotalCase();
                boolean[][] snapshot = null;
                if (totalCase - lastSnapshotCase >= SNAPSHOT_INTERVAL) {
                    snapshot = board.copyQueens();
                    lastSnapshotCase = totalCase;
                }
                final boolean[][] snapshotData = snapshot;
                final long elapsedTime = elapsed;
                final long caseCount = totalCase;

                if (uiPending.compareAndSet(false, true)) {
                    Platform.runLater(() -> {
                        progressUpdater.accept(elapsedTime, caseCount);
                        if (snapshotData != null) {
                            boardUpdater.accept(snapshotData);
                        }
                        uiPending.set(false);
                    });
                }

                try {
                    Thread.sleep(refreshMs);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        updaterThread.setDaemon(true);
        updaterThread.start();
    }

    public void stop() {
        running.set(false);
    }
}
