import java.io.*;
import java.net.*;
import java.util.*;

public class contestmeister {
	static Socket client;
	public static void main(String[] args) {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		
		try {
			client = new Socket(host, port);
	        DataInputStream reader = new DataInputStream(client.getInputStream());
	        DataOutputStream writer = new DataOutputStream(client.getOutputStream());
	        
	        Scanner s = new Scanner(System.in);
	        String line = "";
	        int choice = 0;
	        while(choice != (int)'q' && choice != (int)'k') {
	        	System.out.print("> ");
	        	line = s.nextLine();
	        	if(line.isEmpty()) {
	        		System.out.println("command not found, try again!");
	        	}else {
	        		choice = (int)line.charAt(0);
	        		
	        		if(choice == (int)'p') {
	        			put(line, choice, writer, reader);
	        		}else if(choice == (int)'d') {
	        			delete(line, choice, writer, reader);
	        		}else if(choice == (int)'g') {
	        			get(line, choice, writer, reader);
	        		}else if(choice == (int)'s') {
	        			set(line, choice, writer, reader);
		        	}else if(choice == (int)'a') {
		        		add(line, choice, writer, reader);
		        	}else if(choice == (int)'b') {
		        		begin(line, choice, writer, reader);
		        	}else if(choice == (int)'l') {
		        		list(line, choice, writer, reader);
		        	}else if(choice == (int)'r') {
		        		review(line, choice, writer, reader);
		        	}else if(choice == (int)'q') {
		        		client.close();
		        	}else if(choice == (int)'k') {
		        		writer.writeInt(choice);
		        		client.close();
		        	}else if(choice == (int)'h') {
		        		System.out.println("Use the following commands: ");
						System.out.println("p: put a question in the bank");
						System.out.println("d: delete question from the bank");
						System.out.println("g: get question from the bank");
						System.out.println("s: set the contest");
						System.out.println("a: add question to contest");
						System.out.println("b: begin a contest");
						System.out.println("l: list contests information");
						System.out.println("r: review specific contest information");
						System.out.println("k: terminate the server");
						System.out.println("q: terminate the client");
						System.out.println("h: help");
		        	}
	        	}
	        }
		}catch (IOException IOex) {
			System.out.println("Server Error");
		}
	}
	
	public static void put(String line, int choice, DataOutputStream writer, DataInputStream reader ){
		try {
	        writer.writeInt(choice); //tells server choice
	        int questionNum = Integer.parseInt(line.substring(2));
	        writer.writeInt(questionNum);
	        
	        Scanner s=new Scanner(System.in);
	        
	        String tag = s.nextLine();
	        writer.writeUTF(tag);
	        
	        String question = s.nextLine();
	        String next1 = "";
	        while (!next1.equals(".")){
	        	next1 = s.nextLine();
	        	if (!next1.equals(".")){
	        		question +=  "\n" + next1;
	        	}
	        	
	        }
	        writer.writeUTF(question);
	        
	        //System.out.println("Question to put in:" + question);
	       // writer.writeUTF(question);
	        String next2 = s.nextLine();
	        int end = 0;
	        String questionString = next2 + "\n";
	        String withPeriods = ".\n" + next2 + "\n";
	        while (end!=1){
	        	String next = s.nextLine();
	        	String nextNext = s.nextLine();
	        	withPeriods += next + "\n"+ nextNext + "\n";
	        	if (next.equals(".")){
	        		if(nextNext.equals(".")){
	        			end = 1;
	        		}
	        		else{
	        			questionString += nextNext + "\n";
	        		}
	        	}
	        }
	        //System.out.print ("Choices with periods: " + withPeriods);
	        //System.out.println("Questions string: " + questionString);
	        String correctAns = s.nextLine();
	        writer.writeUTF(withPeriods);
	        writer.writeUTF(questionString);
	        writer.writeUTF(correctAns);
	        	
	        
	        String result = reader.readUTF();
	        System.out.println(result);
		}
		catch (IOException IOex){
            System.out.println("Server Error");
        }
	}
	
	public static void delete(String line, int choice, DataOutputStream writer, DataInputStream reader) {
		try {
			writer.writeInt(choice);
			String[] subString = line.trim().split("\\s+");
			int questionNum = Integer.parseInt(subString[1]);
			writer.writeInt(questionNum);
			
			String result = reader.readUTF();
			System.out.println(result);
		}catch(IOException IOex) {
			System.out.println("server error");
		}
	}
	
	public static void get(String line, int choice, DataOutputStream writer, DataInputStream reader) {
		try {
			writer.writeInt(choice);
			int questionNum = Integer.parseInt(line.substring(2));
			writer.writeInt(questionNum);
			
			String result = reader.readUTF();
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void set(String line, int choice, DataOutputStream writer, DataInputStream reader) {
		try {
			writer.writeInt(choice);
			int contestNum = Integer.parseInt(line.substring(2));
			writer.writeInt(contestNum);
			
			String result = reader.readUTF();
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void add(String line, int choice, DataOutputStream writer, DataInputStream reader) {
		try {
			writer.writeInt(choice);
			String[] subString = line.trim().split("\\s+");
			int contestNum = Integer.parseInt(subString[1]);
			int questionNum = Integer.parseInt(subString[2]);
			writer.writeInt(contestNum);
			writer.writeInt(questionNum);
			
			String result = reader.readUTF();
			System.out.println(result);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void begin(String line, int choice, DataOutputStream writer, DataInputStream reader) {
		try {
			writer.writeInt(choice);
			int contestNum = Integer.parseInt(line.substring(2));
			writer.writeInt(contestNum);
			
			String result = reader.readUTF();
			System.out.println(result);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void list(String line, int choice, DataOutputStream writer, DataInputStream reader) {
		try {
			writer.writeInt(choice);

			String result = reader.readUTF();
			System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void review(String line, int choice, DataOutputStream writer, DataInputStream reader) {
		try {
			writer.writeInt(choice);
			int contestNum = Integer.parseInt(line.substring(2));
			writer.writeInt(contestNum);

			String result = reader.readUTF();
			System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
