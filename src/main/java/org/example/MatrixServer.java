package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class MatrixServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        int SERVER_PORT = Integer.parseInt(args[0]);
        Server server = setupServer(SERVER_PORT);

        startServer(server);
        System.out.println("Server started at port " + SERVER_PORT);

        server.awaitTermination();
    }

    private static Server setupServer(int serverPort) {
        return ServerBuilder.forPort(serverPort)
                .addService(new MatrixImpl())
                .build();
    }

    private static void startServer(Server server) throws IOException {
        server.start();
    }
}