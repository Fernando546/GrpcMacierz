package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.IOException;

public class MatrixClient {
    private static final String SERVER1_ADDRESS = "localhost:2002";
    private static final String SERVER2_ADDRESS = "localhost:2003";
    private static final int MATRIX_SIZE = 4;

    public static void main(String[] args) throws IOException, InterruptedException {
        // Setup servers
        ManagedChannel[] servers = setupServers();

        // Generate matrices
        MatrixOuterClass.Matrix matrixA = MatrixUtils.generate(MATRIX_SIZE);
        MatrixOuterClass.Matrix matrixB = MatrixUtils.generate(MATRIX_SIZE);

        // Print matrices
        MatrixUtils.print(matrixA);
        MatrixUtils.print(matrixB);

        // Split matrix A and multiply with matrix B on each server
        MatrixOuterClass.MatrixMultiplicationReply[] replies = multiplyMatrices(servers, matrixA, matrixB);

        // Combine results and print
        MatrixOuterClass.Matrix result = MatrixUtils.combine(replies, matrixA.getRows(), matrixB.getCols());
        MatrixUtils.print(result);
    }

    private static ManagedChannel[] setupServers() {
        ManagedChannel[] servers = new ManagedChannel[2];
        servers[0] = ManagedChannelBuilder.forTarget(SERVER1_ADDRESS)
                .usePlaintext()
                .build();
        servers[1] = ManagedChannelBuilder.forTarget(SERVER2_ADDRESS)
                .usePlaintext()
                .build();
        return servers;
    }

    private static MatrixOuterClass.MatrixMultiplicationReply[] multiplyMatrices(ManagedChannel[] servers, MatrixOuterClass.Matrix matrixA, MatrixOuterClass.Matrix matrixB) {
        int serversCount = servers.length;

        MatrixOuterClass.Matrix[] subMatricesA = MatrixUtils.split(matrixA, serversCount);
        MatrixOuterClass.MatrixMultiplicationReply[] replies = new MatrixOuterClass.MatrixMultiplicationReply[serversCount];

        for (int i = 0; i < serversCount; i++) {
            MatrixOuterClass.MatrixMultiplicationRequest request = MatrixOuterClass.MatrixMultiplicationRequest.newBuilder()
                    .setMatrixA(subMatricesA[i])
                    .setMatrixB(matrixB)
                    .build();

            MatrixMultiplicationGrpc.MatrixMultiplicationBlockingStub stub = MatrixMultiplicationGrpc.newBlockingStub(servers[i]);
            replies[i] = stub.multiply(request);
        }
        return replies;
    }
}