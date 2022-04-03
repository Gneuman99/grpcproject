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
		public String uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
	   //
		try {
	   
		String uploadDir = "/uploads/";
		String realPath = "/home/gneuman_uk_gmail_com/grpcproject";
	   
		File transferFile = new File(realPath + "/" + file.getOriginalFilename()); 
		file.transferTo(transferFile);
		System.out.println(realPath);
	   
		} catch (Exception e) {
	   
		e.printStackTrace();
	   
		return "Failure";
		}
	   
		return "Success";
		}
		
	//@GetMapping("/") html links 
	
}
