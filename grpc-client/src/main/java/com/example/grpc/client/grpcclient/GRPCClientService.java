package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc.MatrixServiceBlockingStub;
import com.example.grpc.server.grpcserver.PingPongServiceGrpc;
import com.example.grpc.server.grpcserver.MatrixRequest;
import com.example.grpc.server.grpcserver.MatrixArbitraryReply;
import com.example.grpc.server.grpcserver.MatrixArbitraryRequest;
import com.example.grpc.server.grpcserver.MatrixReply;
import com.example.grpc.server.grpcserver.MatrixServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@Service
public class GRPCClientService {
        public String ping() {
                ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                                .usePlaintext()
                                .build();
                PingPongServiceGrpc.PingPongServiceBlockingStub stub = PingPongServiceGrpc.newBlockingStub(channel);
                PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                                .setPing("")
                                .build());
                channel.shutdown();
                return helloResponse.getPong();
        }

        public String add() {
                ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                                .usePlaintext()
                                .build();
                MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);
                MatrixReply A = stub.addBlock(MatrixRequest.newBuilder()
                                .setA00(1)
                                .setA01(2)
                                .setA10(5)
                                .setA11(6)
                                .setB00(1)
                                .setB01(2)
                                .setB10(5)
                                .setB11(6)
                                .build());
                String resp = A.getC00() + " " + A.getC01() + "<br>" + A.getC10() + " " + A.getC11() + "\n";
                return resp;
        }

        public String mult() {
                ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                                .usePlaintext()
                                .build();
                MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);
                MatrixReply A = stub.multiplyBlock(MatrixRequest.newBuilder()
                                .setA00(1)
                                .setA01(2)
                                .setA10(5)
                                .setA11(6)
                                .setB00(2)
                                .setB01(3)
                                .setB10(6)
                                .setB11(7)
                                .build());
                String resp = A.getC00() + A.getC01() + A.getC10() + A.getC11() + "";
                return resp;
        }

        public static int[][] matrixMultiply(int[][] matrix1, int[][] matrix2) {

                int[][][] submatrices1 = matrixToSubmatrices(matrix1);
                int[][][] submatrices2 = matrixToSubmatrices(matrix2);
                // int[][] matrixMerge = submatricesToMatrix(submatrices);
                // //printMatrix(matrixMerge);

                // printMatrix(submatrices[1]);
                // printMatrix(submatrices[2]);
                // printMatrix(submatrices[3]);
                int submatrixWidth = submatrices1[0].length;
                MatrixServiceBlockingStub[] stubs = createBlockingStubs(createChannels(8));
                int[][][] multiplicationResults = new int[8][submatrices1[0].length][submatrices1[0].length];// 8
                                                                                                             // because
                                                                                                             // storing
                                                                                                             // 8
                                                                                                             // matrices,
                long startTime = System.nanoTime();
                multiplicationResults[0] = sendMultiplicationRequest(stubs[getNextStub(numServer)], submatrices1[0],
                                submatrices2[0]);
                long endTime = System.nanoTime();
                footprints = endTime - startTime;
                System.out.println("Time taken: " + footprints);
                multiplicationResults[1] = sendMultiplicationRequest(stubs[getNextStub(numServer)], submatrices1[1],
                                submatrices2[2]);
                multiplicationResults[2] = sendMultiplicationRequest(stubs[getNextStub(numServer)], submatrices1[0],
                                submatrices2[1]);
                multiplicationResults[3] = sendMultiplicationRequest(stubs[getNextStub(numServer)], submatrices1[1],
                                submatrices2[3]);
                multiplicationResults[4] = sendMultiplicationRequest(stubs[getNextStub(numServer)], submatrices1[2],
                                submatrices2[0]);
                multiplicationResults[5] = sendMultiplicationRequest(stubs[getNextStub(numServer)], submatrices1[3],
                                submatrices2[2]);
                multiplicationResults[6] = sendMultiplicationRequest(stubs[getNextStub(numServer)], submatrices1[2],
                                submatrices2[1]);
                multiplicationResults[7] = sendMultiplicationRequest(stubs[getNextStub(numServer)], submatrices1[3],
                                submatrices2[3]);

                // send addition of every other matrix in the multiplication results
                // make addition results matrix array of 4 matrices
                int[][][] additionResults = new int[4][submatrixWidth][submatrixWidth];// 4 because 4 matrices,
                // send addition request for each matrix in the multiplication results
                additionResults[0] = sendAdditionRequest(stubs[getNextStub(numServer)], multiplicationResults[0],
                                multiplicationResults[1]);
                additionResults[1] = sendAdditionRequest(stubs[getNextStub(numServer)], multiplicationResults[2], multiplicationResults[3]);
                additionResults[2] = sendAdditionRequest(stubs[getNextStub(numServer)], multiplicationResults[4], multiplicationResults[5]);
                additionResults[3] = sendAdditionRequest(stubs[getNextStub(numServer)], multiplicationResults[6], multiplicationResults[7]);

                // merge the addition results into one matrix
                int[][] matrixMerge = submatricesToMatrix(additionResults);
                // printMatrix(matrixMerge);
                int numBlockCalls = 8;
                // int deadline is user input int
                // int deadline = Integer.parseInt(deadlineInput);

                // int numberServer=(footprint*numBlockCalls)/deadline;

                return matrixMerge;
        }
        // get next stub

        private static void printMatrix(int[][] matrix) {
                for (int i = 0; i < matrix.length; i++) {
                        for (int j = 0; j < matrix.length; j++) {
                                System.out.print(matrix[i][j] + " ");
                        }
                        System.out.println();
                }
        }

        public static int[][] unpackMatrixArbitraryReply(MatrixArbitraryReply reply) {
                int size = (int) Math.sqrt((double) reply.getMatrixCount());

                // Unpack request into 2D array
                int[][] matrix = new int[size][size];
                for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                                matrix[i][j] = reply.getMatrix(i * size + j);
                        }
                }

                return matrix;
        }

        // turns matrix into 4 submatrices
        public static int[][][] matrixToSubmatrices(int[][] matrix) {
                int size = matrix.length / 2;
                int[][][] submatrices = new int[4][size][size];
                // System.out.println("size" + size);
                for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                                submatrices[0][i][j] = matrix[i][j];
                                submatrices[1][i][j] = matrix[i][j + size];
                                submatrices[2][i][j] = matrix[i + size][j];
                                submatrices[3][i][j] = matrix[i + size][j + size];
                        }
                }

                return submatrices;
        }

        // merge 4 submatrices into 1
        public static int[][] submatricesToMatrix(int[][][] submatrices) {
                int size = submatrices[0].length;
                int[][] matrix = new int[size * 2][size * 2];
                for (int i = 0; i < size; i++) {
                        for (int j = 0; j < size; j++) {
                                matrix[i][j] = submatrices[0][i][j];
                                matrix[i][j + size] = submatrices[1][i][j];
                                matrix[i + size][j] = submatrices[2][i][j];
                                matrix[i + size][j + size] = submatrices[3][i][j];
                        }
                }

                return matrix;
        }

        // create array of async channels
        public static ManagedChannel[] createChannels(int numChannels) {
                String[] addreses = { "10.128.0.4", "10.128.0.6", "10.128.0.8", "10.128.0.9", "10.128.0.10",
                                "10.128.0.11", "10.128.0.12", "10.128.0.13" };// copy the rest of the internal ip
                                                                              // addresses here
                ManagedChannel[] channels = new ManagedChannel[numChannels];
                for (int i = 0; i < numChannels; i++) {
                        channels[i] = ManagedChannelBuilder.forAddress(addreses[i], 9090)
                                        .usePlaintext()
                                        .build();
                        System.out.println(i);
                }
                return channels;
        }

        // //create array of async stubs
        // public static MatrixServiceGrpc.MatrixServiceStub[]
        // createStubs(ManagedChannel[] channels) {
        // MatrixServiceGrpc.MatrixServiceStub[] stubs = new
        // MatrixServiceGrpc.MatrixServiceStub[channels.length];
        // for (int i = 0; i < channels.length; i++) {
        // stubs[i] = MatrixServiceGrpc.newStub(channels[i]);
        // }
        // return stubs;
        // }
        // create array of blocking stubs
        public static MatrixServiceGrpc.MatrixServiceBlockingStub[] createBlockingStubs(ManagedChannel[] channels) {
                MatrixServiceGrpc.MatrixServiceBlockingStub[] stubs = new MatrixServiceGrpc.MatrixServiceBlockingStub[channels.length];
                for (int i = 0; i < channels.length; i++) {
                        stubs[i] = MatrixServiceGrpc.newBlockingStub(channels[i]);
                }
                return stubs;
        }

        // send multiplication request to a stub
        public static int[][] sendMultiplicationRequest(MatrixServiceGrpc.MatrixServiceBlockingStub stub,
                        int[][] matrix1, int[][] matrix2) {
                MatrixArbitraryRequest.Builder builder = MatrixArbitraryRequest.newBuilder();

                for (int i = 0; i < matrix1.length; i++) {
                        for (int j = 0; j < matrix1.length; j++) {
                                builder.addMatrix1(matrix1[i][j]);
                                builder.addMatrix2(matrix2[i][j]);
                        }
                }

                MatrixArbitraryRequest request = builder.build();

                MatrixArbitraryReply response = stub.multiplyArbitrary(request);
                System.out.println(response);

                int[][] result = unpackMatrixArbitraryReply(response);

                return result;

        }

        public static int[][] sendAdditionRequest(MatrixServiceGrpc.MatrixServiceBlockingStub stub, int[][] matrix1,
                        int[][] matrix2) {
                MatrixArbitraryRequest.Builder builder = MatrixArbitraryRequest.newBuilder();

                for (int i = 0; i < matrix1.length; i++) {
                        for (int j = 0; j < matrix1.length; j++) {
                                builder.addMatrix1(matrix1[i][j]);
                                builder.addMatrix2(matrix2[i][j]);
                        }
                }

                MatrixArbitraryRequest request = builder.build();

                MatrixArbitraryReply response = stub.addArbitrary(request);

                int[][] result = unpackMatrixArbitraryReply(response);

                return result;

        }

        public static int stubNumber = 0;
        public static long footprints;
        public static int footprint = (int) footprints;
        public static int deadline = 40;
        public static int numServer = (footprint * 8) / deadline;

        // getNextStub loops through all the numbers until it reaches the max
        public static int getNextStub(int max) {
                stubNumber = stubNumber + 1;
                if (stubNumber == max) {
                        stubNumber = 0;
                }
                return stubNumber;

        }

}
