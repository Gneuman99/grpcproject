package com.example.grpc.server.grpcserver;


import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
@GrpcService
public class MatrixServiceImpl extends MatrixServiceGrpc.MatrixServiceImplBase
{
	@Override
	public void addBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
	{
		System.out.println("Request received from client:\n" + request);
		int C00=request.getA00()+request.getB00();
    		int C01=request.getA01()+request.getB01();
		int C10=request.getA10()+request.getB10();
		int C11=request.getA11()+request.getB11();
		MatrixReply response = MatrixReply.newBuilder().setC00(C00).setC01(C01).setC10(C10).setC11(C11).build();
		reply.onNext(response);
		reply.onCompleted();
	}
	@Override
    	public void multiplyBlock(MatrixRequest request, StreamObserver<MatrixReply> reply)
    	{
        	System.out.println("Request received from client:\n" + request);
        	int C00=request.getA00()*request.getB00()+request.getA01()*request.getB10();
		int C01=request.getA00()*request.getB01()+request.getA01()*request.getB11();
		int C10=request.getA10()*request.getB00()+request.getA11()*request.getB10();
		int C11=request.getA10()*request.getB01()+request.getA11()*request.getB11();
        MatrixReply response = MatrixReply.newBuilder().setC00(C00).setC01(C01).setC10(C10).setC11(C11).build();
        reply.onNext(response);
        reply.onCompleted();
    }

	@Override
		public void multiplyArbitrary(MatrixArbitraryRequest request, StreamObserver<MatrixArbitraryReply> reply)
		{
			System.out.println("Multiply Arbitrary request received from client:");
			 
			int size = (int) Math.sqrt( (double) request.getMatrix1Count() );

			//Unpack request into 2D array
			int[][][] matrices = unpackMatrixArbitraryRequest(request);
			int[][] matrix1 = matrices[0];
			int[][] matrix2 = matrices[1];
			
			System.out.println("Matrix 1:");
			printMatrix(matrix1);
			System.out.println("\nMatrix 2:");
			printMatrix(matrix2);
			System.out.println();

			//Multiply matrices
			//TODO: Implement multiplication
			int[][] result = new int[size][size];
			//multiply two matrices
			for(int i = 0; i < size; i++)
			{
				for(int j = 0; j < size; j++)
				{
					for(int k = 0; k < size; k++)
					{
						result[i][j] += matrix1[i][k] * matrix2[k][j];
					}
				}
			}


			//pack result into response
			MatrixArbitraryReply response = packMatrixArbitraryReply(result);

			reply.onNext(response);
			reply.onCompleted();
		}
		@Override
		public void addArbitrary(MatrixArbitraryRequest request, StreamObserver<MatrixArbitraryReply> reply)
		{
			System.out.println("Add request received from client:");
			 
			int size = (int) Math.sqrt( (double) request.getMatrix1Count() );

			//Unpack request into 2D array
			int[][][] matrices = unpackMatrixArbitraryRequest(request);
			int[][] matrix1 = matrices[0];
			int[][] matrix2 = matrices[1];
			
			System.out.println("Matrix 1:");
			printMatrix(matrix1);
			System.out.println("\nMatrix 2:");
			printMatrix(matrix2);
			System.out.println();

			//add matrices
			//TODO: Implement multiplication
			int[][] result = new int[size][size];
			//add two matrices
			for(int i = 0; i < size; i++)
			{
				for(int j = 0; j < size; j++)
				{
					result[i][j] = matrix1[i][j] + matrix2[i][j];
				}
			}



			//pack result into response
			MatrixArbitraryReply response = packMatrixArbitraryReply(result);

			reply.onNext(response);
			reply.onCompleted();
		}



		public static int[][][] unpackMatrixArbitraryRequest(MatrixArbitraryRequest request)
		{
			int size = (int) Math.sqrt( (double) request.getMatrix1Count() );

			//Unpack request into 2D array
			int[][][] matrices = new int[2][size][size];
			for(int i=0;i<size;i++)
			{
				for(int j=0;j<size;j++)
				{
					matrices[0][i][j]=request.getMatrix1(i*size+j);
					matrices[1][i][j]=request.getMatrix2(i*size+j);
				}
			}

			return matrices;
		}


		public static MatrixArbitraryReply packMatrixArbitraryReply(int[][] matrix)
		{
			int size = matrix.length;

			//pack result into response
			MatrixArbitraryReply.Builder builder = MatrixArbitraryReply.newBuilder();

			for(int i=0;i<size;i++)
			{
				for(int j=0;j<size;j++)
				{
					builder.addMatrix(matrix[i][j]);
				}
			}

			MatrixArbitraryReply response = builder.build();

			return response;
		}


		public static void printMatrix(int[][] matrix) {
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					System.out.print(matrix[i][j] + " ");
				}
				System.out.println();
			}
		}
}
