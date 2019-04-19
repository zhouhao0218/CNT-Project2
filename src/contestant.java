import java.io.*;
import java.net.*;
import java.util.*;

public class contestant {
	static Socket client;
	public static void main(String[] args) {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		
		try {
			client = new Socket(host, port);
			DataInputStream reader = new DataInputStream(client.getInputStream());
	        DataOutputStream writer = new DataOutputStream(client.getOutputStream());
	       
	        Scanner s =new Scanner(System.in);
	        boolean isValid = false;
	        while(!isValid) {
	        	System.out.println("Please input a nickname: ");
	 	        String nickName = s.nextLine();
	 	        writer.writeUTF(nickName);
	 	        String input = reader.readUTF();
	 	        System.out.println(input);
	 	        if(input.contains("Hello")){
	 	        	isValid = true;
	 	        }
	 	        writer.writeBoolean(isValid);
	        }
	        
	        boolean hasQuestion = true;
	        String input1 = "";
	        String input2 = "";
	        String input3 = "";
	        while(hasQuestion) {
	        	input1 = reader.readUTF();			//get question
	        	System.out.println(input1);
	        	
	        	String choice = s.next();
	        	writer.writeUTF(choice);
	        	
	        	input2 = reader.readUTF();			//get correct/incorrect
	        	System.out.println(input2);
	        	
	        	input3 = reader.readUTF();
	        	System.out.println(input3);			//get stats
	        	
	        	hasQuestion = reader.readBoolean();
	        }
	        if(hasQuestion == false) {
	        	System.out.println("The contest is over, thanks for playing!");
	        	client.close();
	        }
		}catch(IOException IOex){
            System.out.println("Server Error");
        }
	}
}
