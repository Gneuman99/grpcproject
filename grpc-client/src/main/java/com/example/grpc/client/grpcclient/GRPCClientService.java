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



}
