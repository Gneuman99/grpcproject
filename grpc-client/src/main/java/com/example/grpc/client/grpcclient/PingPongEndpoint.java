package com.example.grpc.client.grpcclient;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; 


import java.nio.charset.StandardCharsets;


import javax.servlet.http.HttpServletRequest;

//import com.google.common.io.Files;

@RestController
public class PingPongEndpoint {    

	GRPCClientService grpcClientService;    
	@Autowired
    	public PingPongEndpoint(GRPCClientService grpcClientService) {
        	this.grpcClientService = grpcClientService;
    	}    
	@GetMapping("/ping")
    	public String ping() {
        	return grpcClientService.ping();
    	}
        @GetMapping("/add")
	public String add() {
		return grpcClientService.add();
	}
	@GetMapping("/mult")
		public String mult(){
			return grpcClientService.mult();
		}
	@GetMapping("/upload")//serves a page with an HTML form to upload a file REPLACE WITH STACKOVERFLOW HTML CODE
		public String content() throws IOException{

		Path filePath = Paths.get("/home/gneuman_uk_gmail_com/grpcproject/grpc-client/src/main/java/com/example/grpc/client/grpcclient/upload");
	    //String contents;

		String content = Files.readString(filePath);
		return content;
		}

		@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)//where the form actualy SENDS the file TO using POST
		@ResponseBody //the same
		public String uploadFile(@RequestParam("matrix1") MultipartFile file1, @RequestParam("matrix2") MultipartFile file2, HttpServletRequest request) {
	   //
		try {
	   
		String realPath = "/home/gneuman_uk_gmail_com/grpcproject";
	   
		File transferFile1 = new File(realPath + "/matrix1.txt"); 
		file1.transferTo(transferFile1);
		File transferFile2 = new File(realPath + "/matrix2.txt"); 
		file2.transferTo(transferFile2);
		
		System.out.println(realPath);
	   
		} catch (Exception e) {
	   
		e.printStackTrace();
	   
		return "Failure";
		}
	    
		return "<script>window.location.replace('/matrixMultiply');</script>";
		}
		

		@GetMapping(value = "/matrixMultiply")//reads matrices from files and multiplies them
		@ResponseBody //the same
		public static String matrixMultiply(){
			int[][] matrix1 = readFile("matrix1.txt");
			int[][] matrix2 = readFile("matrix2.txt");

			//Check if matrices are the same dimensions
			if(matrix1.length != matrix2.length || matrix1[0].length != matrix2[0].length){
				return "Matrices are not the same dimensions";
			}

			int[][] result = GRPCClientService.matrixMultiply(matrix1, matrix2);

			return matrixToString(result);
		}
	
	
		//Convert matrix to string
		public static String matrixToString(int[][] matrix){
			String matrixString = "";
			for(int i = 0; i < matrix.length; i++){
				for(int j = 0; j < matrix[i].length; j++){
					matrixString += matrix[i][j] + " ";
				}
				matrixString += "<br>";
			}
			return matrixString;
		}


		public static int[][] readFile(String fileName){
			File file = new File("/home/gneuman_uk_gmail_com/grpcproject/" + fileName);
			int[][] value = null;
			try {
				Scanner sizeScanner = new Scanner(file);
				String[] temp = sizeScanner.nextLine().split(" ");
				sizeScanner.close();
				int nMatrix = temp.length;
			
				Scanner scanner = new Scanner(file);
				value = new int[nMatrix][nMatrix];
				for (int i = 0; i < nMatrix; i++) {
					String[] numbers = scanner.nextLine().split(" ");
					for (int j = 0; j < nMatrix; j++) {
						value[i][j] = Integer.parseInt(numbers[j]);
					}
				}
				scanner.close();
			
			} catch (FileNotFoundException e) {
				e.printStackTrace();}
			return value;
		}
}
