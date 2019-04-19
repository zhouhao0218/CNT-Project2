import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;

public class cserver {
	static ServerSocket server;
	static Map<Integer, Set<String>> nameMap = Collections.synchronizedMap(new HashMap<Integer, Set<String>>());						//contest number to nickname set
	static Map<Integer, List<String[]>> questionMap = Collections.synchronizedMap(new HashMap<Integer, List<String[]>>());				//contest number to question list
	static Map<Integer, String> contestStatusMap = Collections.synchronizedMap(new HashMap<Integer, String>()) ;						//contest number to status 
	static Map<String, Statistic> statMap = Collections.synchronizedMap(new HashMap<String, Statistic>());								//nickname to statistics
	static Map<Integer, List<Statistic>> questionStatMap = Collections.synchronizedMap(new HashMap<Integer, List<Statistic>>()); 		//question number to statistic list
	static HashSet<String> nameList = new HashSet<>();
	static Object lock = new Object();
	static int playerNum = 0;
	static int finishedPlayer = 0;
		
    public static void main(String[] args) {
    	try{
			ServerSocket sock = new ServerSocket(0);
		    int port = sock.getLocalPort();
		    sock.close();
		    System.out.println(port);
			server = new ServerSocket(port);
			
			new MeisterHandler(server.accept()).start();;
			System.out.println("Client accepted");
		}
		catch (IOException IOex) {
//            System.out.println("Server error");
			IOex.printStackTrace();
        }
    }
    
    public static void CreateContestant(int port, int contestNum) {
    	try {
    	    ServerSocket server1 = new ServerSocket(port);
    	    server1.setSoTimeout(30000);
    	    while(true) {
    	    	new ContestantHandler(server1.accept(), contestNum, lock).start();
    	    	playerNum++;
    	    }
    	}catch (SocketTimeoutException e1) {
    		System.out.println("Contest started, no more contestant accept");
    	}catch (IOException e) {
    		e.printStackTrace();
    	}
    	synchronized(lock) {
    		lock.notifyAll();
    	}
    }
    
    public static boolean notifyThreads(int contestNum) {
//    	System.out.println("all player: " + playerNum);
    	finishedPlayer++;
//    	System.out.println("finished player: " + finishedPlayer);
    	if(finishedPlayer == playerNum) {
    		synchronized(lock) {
    			lock.notifyAll();
    			finishedPlayer = 0;
    			return true;
    		}
    	}else {
    		return false;
    	}
    }
    
    private static class MeisterHandler extends Thread{
    	static Socket client;
    	static HashMap<Integer, String[]> map = new HashMap<Integer, String[]>();
    	
    	public MeisterHandler(Socket socket) {
    		this.client = socket;
    	}
    	
    	public void run() {
    		File qBank = new File("questionBank.ser");
        	boolean isExists = qBank.exists();
        	
        	if(isExists) {
        		try {
        			InputStream file = new FileInputStream("questionBank.ser");
    			    InputStream buffer = new BufferedInputStream(file);
    			    ObjectInput input = new ObjectInputStream (buffer);
    			    try {
    			    	map = (HashMap<Integer, String[]>)input.readObject();
    			    }finally {
    			    	input.close();
    			    }
        		}catch(ClassNotFoundException e) {
        			System.out.println("Class not found!");
        		}catch(IOException e){
        			System.out.println("Error of I/O");
        		}
        	}
        	
        	int choice = 0;
        	boolean quit = false;
        	while(!quit) {
        		try {
    				DataInputStream reader = new DataInputStream(client.getInputStream());
    		        DataOutputStream writer = new DataOutputStream(client.getOutputStream());
    		        List<String[]> questions= new ArrayList<String[]>();
    		        while(choice != (int)'k') {
    		        	choice = reader.readInt();
    		        	
    		        	if(choice == (int)'p') {
    		        		int questionID = reader.readInt();
    		        		String[] arr = new String[6];
			        		String tag = reader.readUTF();
			        		String question = reader.readUTF();
			        		String withPeriods = reader.readUTF();
			        		
//			        		System.out.println("tag: " + tag);
//				        	System.out.println("Question: " + question);
//				        	System.out.println("Choices with periods: " + withPeriods);
			        		
				        	String choiceString = reader.readUTF();
				        	String correctAns = reader.readUTF();
			        		arr[0] = tag;
			        		arr[1] = question;
			        		arr[2] = choiceString;
			        		arr[3] = correctAns;
			        		arr[4] = withPeriods;
			        		arr[5] = Integer.toString(questionID);
			       
			        		String output = "";
			        		if(map.containsKey(questionID)) {
			        			output = "Error: question number " + questionID + " already used \n";
			        		}else {
			        			output = "Question " + questionID + " added \n"; 
			        			map.put(questionID, arr);
			        		}
//			        		System.out.println(output);
			        		writer.writeUTF(output);
			        		
			        		try{
				      		      OutputStream file = new FileOutputStream("questionBank.ser");
				      		      OutputStream buffer = new BufferedOutputStream(file);
				      		      ObjectOutput out = new ObjectOutputStream(buffer);
				      		      try{
				      		        out.writeObject(map);
				      		      }finally{
				      		        out.close();
				      		      }
				        	}catch(IOException ex){
				      		    System.out.println("I/O Error");
				        	}
    		        	}else if(choice == (int)'d') {
    		        		int questionNum = reader.readInt();
    		        		String output = "";
    		        		if(map.containsKey(questionNum)) {
    		        			output = "Deleted question " + questionNum;
    		        			map.remove(questionNum);
    		        		}else {
    		        			output = "Error: question " + questionNum + " not found";
    		        		}
    		        		writer.writeUTF(output);
    		        		
    		        		try{
				      		      OutputStream file = new FileOutputStream("questionBank.ser");
				      		      OutputStream buffer = new BufferedOutputStream(file);
				      		      ObjectOutput out = new ObjectOutputStream(buffer);
				      		      try{
				      		        out.writeObject(map);
				      		      }finally{
				      		        out.close();
				      		      }
				        	}catch(IOException ex){
				      		    System.out.println("I/O Error");
				        	}
    		        	}else if(choice == (int)'g') {
    		        		int questionNum = reader.readInt();
    		        		String output = "";
    		        		
    		        		if(map.containsKey(questionNum)) {
    		        			String[] question = map.get(questionNum);
    		        			String tag = question[0];
    		        			String questionBody = question[1];
    		        			String choices = question[4];
    		        			String ans = question[3];
    		        			output = tag + "\n" + questionBody + "\n" + choices + ans;
    		        		}else {
    		        			output = "Error: question " + questionNum + " not found \n";
    		        		}
    		        		writer.writeUTF(output);
    		        	}else if(choice == (int)'s') {
    		        		int contestNum = reader.readInt();
    		        		String output = "";
    		        		Set<String> nameList = new HashSet<String>();
    		        		questions= new ArrayList<String[]>();
    		        		if(nameMap.containsKey(contestNum)) {
    		        			output = "Error, contest " + contestNum + " already exist";
    		        		}else {
    		        			nameMap.put(contestNum, nameList);
    		        			contestStatusMap.put(contestNum, "not run");
    		        			questionMap.put(contestNum, questions);
    		        			output = "Contest " + contestNum + " is set"; 
    		        		}
    		        		writer.writeUTF(output);
    		        	}else if(choice == (int)'a') {
    		        		int contestNum = reader.readInt();
    		        		int questionNum = reader.readInt();
    		        		String output = "";
    		        		if(nameMap.containsKey(contestNum)) {
    		        			if(map.containsKey(questionNum)) {
    		        				String[] question = map.get(questionNum);
    		        				questions.add(question);
    		        				questionMap.put(contestNum, questions);
    		        				List<Statistic> s = new ArrayList<Statistic>();
    		        				questionStatMap.put(questionNum, s);
    		        				output = "Added question " + questionNum + " to contest " + contestNum;
    		        			}else {
    		        				output = "Question " + questionNum + " does not exist ";
    		        			}
    		        		}else {
    		        			output = "Contest " + contestNum + " does not exist";
    		        		}
    		        		writer.writeUTF(output);
    		        	}else if(choice == (int)'b') {
    		        		int contestNum = reader.readInt();
    		        		String output = "";
    		        		String status = "";
    		        		if(!nameMap.containsKey(contestNum)) {
    		        			status = "not run";
    		        			contestStatusMap.put(contestNum, status);
    		        			output = "Error, contest " + contestNum + " does not exist";
    		        			writer.writeUTF(output);
    		        		}else {
    		        			ServerSocket sock = new ServerSocket(0);
    		            	    int port = sock.getLocalPort();
    		            	    sock.close();
    		            	    System.out.println("Contest " + contestNum + " started on port " + port);
        		    		    output = Integer.toString(port);
        		    		    writer.writeUTF(output);
        		    		    status = "run";
        		    		    contestStatusMap.put(contestNum, status);
        		    		    CreateContestant(port, contestNum);
    		        		}
    		        	}else if(choice == (int)'l') {
    		        		String output = "";
    		        		if(nameMap.size() == 0) {
    		        			output = "No contest set";
    		        		}else {
    		        			int correctNum = 0;
    							int total = 0;
    							for(Entry<Integer, List<Statistic>> set : questionStatMap.entrySet()) {
    								List<Statistic> qStat = set.getValue();
    								for(int j=0; j<qStat.size(); j++) {
        								Statistic st = qStat.get(j);
        								correctNum += st.correct;
        								total += st.total;
        							}
    							}
    							String average  = ", average correct: " + ((double)correctNum) / total;
    							
    		        			int topScore = 0;
    							for(Entry<String, Statistic> s : statMap.entrySet()) {
    								topScore = Math.max(topScore, s.getValue().correct);
    							}
    							String maxScore = "; maximum correct: " + topScore;
    		        			for(int i : nameMap.keySet()) {
    		        				int questionNum = questionMap.get(i).size();
    		        				output += i + "\t" + questionNum + " questions, " + contestStatusMap.get(i) + average + maxScore + "\n";
    		        			}
    		        		}
    		        		writer.writeUTF(output);
    		        	}else if(choice == (int)'r') {
    		        		int contestNum = reader.readInt();
    		        		String output = "";
    		        		if(!nameMap.containsKey(contestNum)) {
    		        			output = "Error: Contest " + contestNum + " does not exist";
    		        		}else {
    		        			int correctNum = 0;
    							int total = 0;
    							for(Entry<Integer, List<Statistic>> set : questionStatMap.entrySet()) {
    								List<Statistic> qStat = set.getValue();
    								for(int j=0; j<qStat.size(); j++) {
        								Statistic st = qStat.get(j);
        								correctNum += st.correct;
        								total += st.total;
        							}
    							}
    							String average  = ", average correct: " + ((double)correctNum) / total;
    							
    		        			int topScore = 0;
    							for(Entry<String, Statistic> s : statMap.entrySet()) {
    								topScore = Math.max(topScore, s.getValue().correct);
    							}
    							String maxScore = "; maximum correct: " + topScore;
    							
    		        			int questionNum = questionMap.get(contestNum).size();
    		        			output += contestNum + "\t" + questionNum + " questions, " + contestStatusMap.get(contestNum) + average + maxScore + "\n";
    		        			
    		        			List<String[]> question = questionMap.get(contestNum);
    		        			for(int i=0; i<question.size(); i++) {
    		        				String[] q = question.get(i);
    		        				int c = 0;
        							int t = 0;
        							List<Statistic> qStat = questionStatMap.get(Integer.parseInt(q[5]));
//        							System.out.println(qStat.size());
        							if(qStat.size() == 0) {
        								output += "\t" + q[5] + "\n";
        							}else {
        								for(int j=0; j<qStat.size(); j++) {
            								Statistic st = qStat.get(j);
            								c += st.correct;
            								t += st.total;
            							}
        		        				output += "\t" + q[5] + "\t" + (c*100) / t +"%\n";
        							}
    		        			}
    		        		}
    		        		writer.writeUTF(output);
    		        	}else if(choice == (int)'k') {
    		        		System.out.println("Server terminated");
			        		server.close();
			        		quit = true;
    		        	}
    		        }
        		}catch(IOException IOex) {
        			System.out.println("Error of I/O");
        		}
        	}
        }
    }
    
    private static class ContestantHandler extends Thread{
    	static Socket client;
    	static int contestNum;
//    	Object lock;
    	static List<Statistic> allStats = new ArrayList<Statistic>();
    	
    	public ContestantHandler(Socket socket, int contestNum, Object lock) {
    		this.client = socket;
    		this.contestNum = contestNum;
//    		this.lock = lock;
    	}
    	
    	public void run() {
    		try {
				DataInputStream reader = new DataInputStream(client.getInputStream());
				DataOutputStream writer = new DataOutputStream(client.getOutputStream());
				String nickName = "";
				
				boolean isAccept = false;
				while(!isAccept) {
					nickName = reader.readUTF();
					String output = "";
					if(nameList.contains(nickName)) {
						output = "Error, nickname " + nickName + " is already in use";
					}else {
						nameList.add(nickName);
						nameMap.put(contestNum, nameList);
						output = "Hello " + nickName + ", get ready for contest!";
					}
					writer.writeUTF(output);
					isAccept = reader.readBoolean();
				}
				synchronized(lock) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				String output1 = "";
				String output2 = "";
				boolean hasQuestion = true;
				if(questionMap.containsKey(contestNum)) {
					List<String[]> question = questionMap.get(contestNum);
					int correct = 0;
					int incorrect = 0;
					for(int i=0; i<question.size(); i++) {
						String[] q = question.get(i);
						String text = q[1];
						String choice = q[2];
						String ans = q[3];
						int questionNum = Integer.parseInt(q[5]);
						int qNum = i+1;
						output1 = "Question " + qNum + "\n" + text + "\n" + choice +"\nEnter your choice: ";
						writer.writeUTF(output1);
						
						String reply = reader.readUTF();
//						System.out.println(reply);
//						System.out.println(ans);
//						System.out.println(reply.compareTo(ans));
						
						if(reply.compareTo(ans) == 0) {
							output2 = "Correct. ";
							correct++;
						}else {
							output2 = "Incorrect. ";
							incorrect++;
						}

						Statistic stats = new Statistic(correct, incorrect);
						statMap.put(nickName, stats);
//						System.out.println("statmap size: " + statMap.size());
						allStats.add(stats);
						questionStatMap.put(questionNum, allStats);
						
						boolean isFinish = notifyThreads(contestNum); 
						if(!isFinish) {
							synchronized(lock) {
								try {
									lock.wait();
//									System.out.println("I m back");
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} else {
							synchronized(lock) {
								lock.notifyAll();
//								System.out.println("I m back");
							}
						}
						
						int correctNum = 0;
						int total = 0;
						List<Statistic> qStat = questionStatMap.get(qNum);
						for(int j=0; j<qStat.size(); j++) {
							Statistic st = qStat.get(j);
							correctNum += st.correct;
							total += st.total;
						}
						output2 += (correctNum * 100) / total +"% of contestants answered this question correctly.";
						
						writer.writeUTF(output2);
						int topScore = 0;
						int totalNum = 0;
						for(Entry<String, Statistic> s : statMap.entrySet()) {
							topScore = Math.max(topScore, s.getValue().correct);
							totalNum = s.getValue().total;
						}
						String output3 = stats.getStat();
						output3 += " The top score is currently " + topScore + "/" + totalNum;
//						System.out.println(output3);
						writer.writeUTF(output3);
						
						if(i<question.size()-1) {
							writer.writeBoolean(hasQuestion);
						}else {
							writer.writeBoolean(!hasQuestion);	
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
}

class Statistic{
	int correct;
	int incorrect;
	int total;
	String output;
	
	Statistic(int correct, int incorrect){
		this.correct = correct;
		this.incorrect = incorrect;
		this.total = correct + incorrect;
		this.output = "Your score is " + correct + "/" + total + ".";
	}
	
	public String getStat() {
		return output;
	}
}


