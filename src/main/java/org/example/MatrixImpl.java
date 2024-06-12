package org.example;

import io.grpc.stub.StreamObserver;

public class MatrixImpl extends MatrixMultiplicationGrpc.MatrixMultiplicationImplBase {
    private final int serverPort;

    public MatrixImpl(int serverPort) {
        this.serverPort = serverPort;
    }
    @Override
    public void multiply(
            MatrixOuterClass.MatrixMultiplicationRequest request,
            StreamObserver<MatrixOuterClass.MatrixMultiplicationReply> responseObserver
    ) {
        System.out.println("Received multiplication request on server running on port " + serverPort);
        // Extract matrices from request
        MatrixOuterClass.Matrix matrixA = request.getMatrixA();
        MatrixOuterClass.Matrix matrixB = request.getMatrixB();

        // Check if multiplication is possible
        if(!isMultiplicationPossible(matrixA, matrixB)){
            responseObserver.onError(new IllegalArgumentException("Columns of matrix A must be equal to rows of matrix B"));
            return;
        }

        // Perform multiplication
        MatrixOuterClass.Matrix result = MatrixUtils.multiply(matrixA, matrixB);

        // Build reply
        MatrixOuterClass.MatrixMultiplicationReply reply = buildReply(result);

        // Send reply
        sendReply(responseObserver, reply);
    }

    private boolean isMultiplicationPossible(MatrixOuterClass.Matrix matrixA, MatrixOuterClass.Matrix matrixB) {
        return matrixA.getCols() == matrixB.getRows();
    }

    private MatrixOuterClass.MatrixMultiplicationReply buildReply(MatrixOuterClass.Matrix result) {
        return MatrixOuterClass.MatrixMultiplicationReply.newBuilder()
                .setResult(result).build();
    }

    private void sendReply(StreamObserver<MatrixOuterClass.MatrixMultiplicationReply> responseObserver, MatrixOuterClass.MatrixMultiplicationReply reply) {
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}