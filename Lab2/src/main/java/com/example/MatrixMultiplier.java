package com.example;

import java.util.concurrent.*;

public class MatrixMultiplier {

    public static int[][] multiplyFixed(int[][] A, int[][] B, int threads) throws InterruptedException {
        int m = A.length, n = A[0].length, k = B[0].length;
        int[][] C = new int[m][k];
        Object lock = new Object();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(m * k);

        int[] nextI = {0}, nextJ = {0};

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                while (true) {
                    int row, col;
                    synchronized (lock) {
                        if (nextI[0] >= m) break;
                        row = nextI[0];
                        col = nextJ[0];

                        if (++nextJ[0] >= k) {
                            nextJ[0] = 0;
                            nextI[0]++;
                        }
//                        System.out.printf("Потік %s обчислює C[%d][%d]%n", Thread.currentThread().getName(), row, col);
                    }
                    for (int x = 0; x < n; x++) {
                        C[row][col] += A[row][x] * B[x][col];
                    }
                    latch.countDown();
                }
//                System.out.printf("Потік %s завершив роботу%n", Thread.currentThread().getName());
            });
        }
        latch.await();
        pool.shutdown();
        return C;
    }

    public static int[][] multiplyVirtualNoLimit(int[][] A, int[][] B) throws InterruptedException {
        int m = A.length, n = A[0].length, k = B[0].length;
        int[][] C = new int[m][k];
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch latch = new CountDownLatch(m * k);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++) {
                int row = i, col = j;
                executor.submit(() -> {
                    for (int x = 0; x < n; x++) {
                        C[row][col] += A[row][x] * B[x][col];
                    }
                    latch.countDown();
                });
            }
        }
        latch.await();
        executor.shutdown();
        return C;
    }

    public static int[][] multiplyVirtualWithLimit(int[][] A, int[][] B, int permits) throws InterruptedException {
        int m = A.length, n = A[0].length, k = B[0].length;
        int[][] C = new int[m][k];
        Semaphore semaphore = new Semaphore(permits);
        CountDownLatch latch = new CountDownLatch(m * k);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < k; j++) {
                int row = i, col = j;
                Thread.startVirtualThread(() -> {
                    try {
                        semaphore.acquire();
                        for (int x = 0; x < n; x++) {
                            C[row][col] += A[row][x] * B[x][col];
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        semaphore.release();
                        latch.countDown();
                    }
                });
            }
        }
        latch.await();
        return C;
    }
}