package com.example.grpc.client.grpcclient;

import com.example.grpc.server.grpcserver.PingRequest;
import com.example.grpc.server.grpcserver.PongResponse;
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
		PingPongServiceGrpc.PingPongServiceBlockingStub stub
                = PingPongServiceGrpc.newBlockingStub(channel);        
		PongResponse helloResponse = stub.ping(PingRequest.newBuilder()
                .setPing("")
                .build());        
		channel.shutdown();        
		return helloResponse.getPong();
    }
    public String add(){
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9090)
		.usePlaintext()
		.build();
		MatrixServiceGrpc.MatrixServiceBlockingStub stub
		 = MatrixServiceGrpc.newBlockingStub(channel);
		MatrixReply A=stub.addBlock(MatrixRequest.newBuilder()
			.setA00(1)
			.setA01(2)
			.setA10(5)
			.setA11(6)
			.setB00(1)
			.setB01(2)
			.setB10(5)
			.setB11(6)
			.build());
		String resp= A.getC00()+" "+A.getC01()+"<br>"+A.getC10()+" "+A.getC11()+"\n";
		return resp;
    }
	public String mult(){
                ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9090)
                .usePlaintext()
                .build();
                MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);
                MatrixReply A=stub.multiplyBlock(MatrixRequest.newBuilder()
                        .setA00(1)
                        .setA01(2)
                        .setA10(5)
                        .setA11(6)
                        .setB00(2)
                        .setB01(3)
                        .setB10(6)
                        .setB11(7)
                        .build());
                String resp=A.getC00()+A.getC01()+A.getC10()+A.getC11()+"";
                return resp;
    }

    public static int[][] matrixMultiply(int[][] matrix1, int[][] matrix2) {
        int[][][] submatrices = matrixToSubmatrices(matrix1);
        int[][] matrixMerge = submatricesToMatrix(submatrices);
        printMatrix(matrixMerge);

        printMatrix(submatrices[1]);
        printMatrix(submatrices[2]);
        printMatrix(submatrices[3]);

        
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",9090)
                .usePlaintext()
                .build();
        MatrixServiceGrpc.MatrixServiceBlockingStub stub = MatrixServiceGrpc.newBlockingStub(channel);

        MatrixArbitraryRequest.Builder builder = MatrixArbitraryRequest.newBuilder();

        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1.length; j++) {
                builder.addMatrix1(matrix1[i][j]);
                builder.addMatrix2(matrix2[i][j]);
            }
        }

        MatrixArbitraryRequest request = builder.build();

        MatrixArbitraryReply response = stub.multiplyArbitrary(request);

        int[][] result = unpackMatrixArbitraryReply(response);

        return result;
    }



    private static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

public static int[][] unpackMatrixArbitraryReply(MatrixArbitraryReply reply) {
        int size = (int) Math.sqrt( (double) reply.getMatrixCount() );

        //Unpack request into 2D array
        int[][] matrix = new int[size][size];
        for(int i=0;i<size;i++)
        {
                for(int j=0;j<size;j++)
                {
                        matrix[i][j]=reply.getMatrix(i*size+j);
                }
        }

        return matrix;
    }
    //turns matrix into 4 submatrices   
    public static int[][][] matrixToSubmatrices(int[][] matrix) {
        int size = matrix.length /2;
        int[][][] submatrices = new int[4][size][size];
        //System.out.println("size" + size);
        for(int i=0;i<size;i++)
        {
                for(int j=0;j<size;j++)
                {
                        submatrices[0][i][j]=matrix[i][j];
                        submatrices[1][i][j]=matrix[i][j+size];
                        submatrices[2][i][j]=matrix[i+size][j];
                        submatrices[3][i][j]=matrix[i+size][j+size];
                }
        }

        return submatrices;
    }


    //merge 4 submatrices into 1
        public static int[][] submatricesToMatrix(int[][][] submatrices) {
                int size = submatrices[0].length;
                int[][] matrix = new int[size*2][size*2];
                for(int i=0;i<size;i++)
                {
                        for(int j=0;j<size;j++)
                        {
                                matrix[i][j]=submatrices[0][i][j];
                                matrix[i][j+size]=submatrices[1][i][j];
                                matrix[i+size][j]=submatrices[2][i][j];
                                matrix[i+size][j+size]=submatrices[3][i][j];
                        }
                }
        
                return matrix;
        }


}
