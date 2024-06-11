package org.example;

import java.security.SecureRandom;

public class MatrixUtils {

    private static final int MATRIX_DATA_BOUND = 10;

    static MatrixOuterClass.Matrix multiply(MatrixOuterClass.Matrix matrixA, MatrixOuterClass.Matrix matrixB) {
        int rowsA = matrixA.getRows();
        int colsA = matrixA.getCols();
        int colsB = matrixB.getCols();

        int[][] result = new int[rowsA][colsB];
        for(int i = 0; i < rowsA; i++){
            for(int j = 0; j < colsB; j++){
                for(int k = 0; k < colsA; k++){
                    result[i][j] += matrixA.getData(i*colsA + k) * matrixB.getData(k*colsB + j);
                }
            }
        }

        return buildMatrixFrom2DArray(result, rowsA, colsB);
    }

    static MatrixOuterClass.Matrix generate(int size) {
        MatrixOuterClass.Matrix.Builder matrix = MatrixOuterClass.Matrix.newBuilder().setRows(size).setCols(size);
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < size * size; i++) {
            matrix.addData(secureRandom.nextInt(MATRIX_DATA_BOUND));
        }
        return matrix.build();
    }

    static void print(MatrixOuterClass.Matrix matrix) {
        int rows = matrix.getRows();
        int cols = matrix.getCols();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.printf("%4d",matrix.getData(i*cols + j));
            }
            System.out.println();
        }
        System.out.println();
    }

    static MatrixOuterClass.Matrix[] split(MatrixOuterClass.Matrix matrix, int serversCount) {
        int rows = matrix.getRows();
        int cols = matrix.getCols();

        int rowsPerServer = rows / serversCount;

        MatrixOuterClass.Matrix[] subMatrices = new MatrixOuterClass.Matrix[serversCount];
        for (int i = 0; i < serversCount; i++) {
            subMatrices[i] = buildSubMatrix(matrix, i, rowsPerServer, cols);
        }
        return subMatrices;
    }

    static MatrixOuterClass.Matrix combine(MatrixOuterClass.MatrixMultiplicationReply[] replies, int totalRows, int totalCols) {
        MatrixOuterClass.Matrix.Builder builder = MatrixOuterClass.Matrix.newBuilder();
        builder.setRows(totalRows);
        builder.setCols(totalCols);

        for (MatrixOuterClass.MatrixMultiplicationReply reply : replies) {
            MatrixOuterClass.Matrix result = reply.getResult();
            for (int i = 0; i < result.getDataCount(); i++) {
                builder.addData(result.getData(i));
            }
        }

        return builder.build();
    }

    private static MatrixOuterClass.Matrix buildMatrixFrom2DArray(int[][] array, int rows, int cols) {
        MatrixOuterClass.Matrix.Builder builder = MatrixOuterClass.Matrix.newBuilder()
                .setRows(rows)
                .setCols(cols);
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                builder.addData(array[i][j]);
            }
        }
        return builder.build();
    }

    private static MatrixOuterClass.Matrix buildSubMatrix(MatrixOuterClass.Matrix matrix, int index, int rows, int cols) {
        MatrixOuterClass.Matrix.Builder builder = MatrixOuterClass.Matrix.newBuilder();
        builder.setRows(rows);
        builder.setCols(cols);
        for (int j = 0; j < rows * cols; j++) {
            builder.addData(matrix.getData(index * rows * cols + j));
        }
        return builder.build();
    }
}