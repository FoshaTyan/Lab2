package com.example;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class BenchmarkTest {

    @Param({"100"})
    int size;

    int[][] A, B;

    @Setup(Level.Invocation)
    public void setup() {
        A = Matrix.create(size, size);
        B = Matrix.create(size, size);
    }

    @Benchmark
    public void testFixedThreadPool() throws InterruptedException {
        MatrixMultiplier.multiplyFixed(A, B, 6);
    }

    @Benchmark
    public void testVirtualThreadsNoLimit() throws InterruptedException {
        MatrixMultiplier.multiplyVirtualNoLimit(A, B);
    }

    @Benchmark
    public void testVirtualThreadsWithLimit() throws InterruptedException {
        MatrixMultiplier.multiplyVirtualWithLimit(A, B, 6);
    }
}