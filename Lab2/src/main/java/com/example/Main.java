package com.example;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        int m = 4, n = 4, k = 4;
        int p = 6;

        int[][] A = Matrix.create(m, n);
        int[][] B = Matrix.create(n, k);

        System.out.println("Матриця A:");
        Matrix.printMatrix(A);

        System.out.println("\nМатриця B:");
        Matrix.printMatrix(B);

        int[][] result = MatrixMultiplier.multiplyFixed(A, B, p);

        System.out.println("\nРезультат множення:");
        //Matrix.printMatrix(result);
    }
}
