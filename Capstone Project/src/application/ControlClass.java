package application;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class ControlClass extends Application{
//Global declaration.
	String firstName;
	String lastName;
	static String username;
	static String password;

//This is the main method.
	public static void main(String[] args) {
	
	// Declaring Things for the program
		ArrayList<Integer> emailList = new ArrayList<Integer>();
		String host = "pop.gmail.com";
		String mailStoreType = "pop3";
		String port = "995";
		String saveDirectory = "/Users/student/Documents/GUARDIANSHIP";

	//Run the login screen
		launch(args);
	
	//Run check method
		emailList = check(host, mailStoreType, username, password);
	//Set saveDirectory
		ControlClass reciever = new ControlClass();
		reciever.setSaveToDirectory(saveDirectory);
	//Run Save attachment method
		reciever.saveAttachments(host, port, username, password, emailList);
		
	}//End Main

//This provides the list of email numbers to look at.
	@SuppressWarnings("finally")
	public static  ArrayList<Integer> check(String host, String storeType, String user, String password) {
		ArrayList<Integer> emailList = new ArrayList<Integer>();


		try {
		//create Properties field
			Properties properties = new Properties();
			properties.put("mail.pop3.host", host);
			properties.put("mail.pop3.port", "995");
			properties.put("mail.pop3.starttls.enable", "true");
			Session emailSession = Session.getDefaultInstance(properties);
			
		//Create the pop3 store object and connect with the pop server
			Store store = emailSession.getStore("pop3s");
			store.connect(host, user, password);			

		//Create the folder object and open it
			Folder emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);
			System.out.println(emailFolder.getMessageCount());
			
		//retrieve the messages from the folder in an array and print it
			Message[] messages = emailFolder.getMessages();
			System.out.println("messages.length---" + messages.length);
			
			for(int i = 0, n = (messages.length ); i < n ; i++) {
				Message message = messages[i];
				
				System.out.println("---------------------------------");
				System.out.println("Email Number " + (i + 1));
				if(message.getSubject()!= null) {
					
//-----------------------------------------------------v-----------------Change this to find new Emails.	
					if(message.getSubject().contains("FU(B)")) {
						emailList.add(i);
						System.out.println("Subject: " + message.getSubject());
						System.out.println("Number Stored...");
					}//end if Statement
				}//End null if
			}//End for loop
			
		//Close the store and folder objects
			emailFolder.close(false);
			store.close();
			
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return emailList;
		}// End Try Catch Block
	}//End Check Method

//Big Method to save attachments
	public void saveAttachments(String host, String port, String username, String password, 
		ArrayList<Integer> emailList) {
	//Server settings
		Properties properties = new Properties();
		properties.put("mail.pop3.host", host);
		properties.put("mail.pop3.port", port);	
	//SSL Setting
		properties.setProperty("mail.pop3.socketFactory.class", "javax.net.SSLSocketFactory");
		properties.setProperty("mail.pop3.socketFactory.fallback", "false");
		properties.setProperty("mail.pop3.socketFactory.port", String.valueOf(port));
	//Session
		Session session = Session.getDefaultInstance(properties);
		
	//Main Try Block
		try {
		//Connects to the message store	
			Store store = session.getStore("pop3s");
			store.connect( host, username, password);
			
		//Opens the inbox folder
			Folder folderInbox = store.getFolder("INBOX");
			folderInbox.open(Folder.READ_ONLY);
		
		//Fetches new messages from the server
			Message[] arrayMessages = folderInbox.getMessages();
		
		//This is a for loop that gets each email we have chosen and run the below code on it.
			for (int email: emailList) {
			//Assigns one of the messages from the array to variable "message" 
				Message message = arrayMessages[email];
	
			//Get info about the email.
				Address[] fromAddress = message.getFrom();
				String from = fromAddress[0].toString();
				String subject = message.getSubject();
				String sentDate = message.getSentDate().toString();
				String contentType = message.getContentType();
				String messageContent = "";
				
			//Store attachment file name, separated by comma.
				String attachFiles = "";
				
			//This tells me if the Email may have attachments.		
				if (contentType.contains("multipart")) {
					Multipart multipart = (Multipart) message.getContent();
					int numberOfParts = multipart.getCount();
						
				//For loop that will check each part of the message.
					for (int partCount = 0; partCount < numberOfParts; partCount++) {
					//Not exactly sure what this does.
						MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(partCount);
						if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
						//This part of the Email is an attachment
							String fileName = part.getFileName();
						
						//Add to printout list
							attachFiles += fileName + ", ";
							File f = new File(saveToDirectory);
							
						//This grabs the HTML code and gives me a string for commands.
						//Sadly this is no longer in use as I need to implement RegEx to use the message 
						//content to its full potential. 
							Document doc = Jsoup.parse(messageContent);
							String text = doc.body().text();
													
						//Get client name.
							getClient(message);
						//Add client name to search directory so we only search for the file under the correct name.
							checkFileIsReal(f, lastName);
							File file = new File(saveToDirectory);
						//Check Attachment exists in folder. Delete it if it does.
							checkFileIsReal(file, fileName);
						//This is where we save the file to the file.	
							part.saveFile(saveToDirectory + File.separator + part.getFileName() /*+ date*/);
						
						} else {
							messageContent = part.getContent().toString();
						}//End IF/ELSE ATTACHMENT.Equals 
					}//End part count loop
				
				//No Idea what this does.
					if (attachFiles.length() > 1) {
						attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
					}//End LENGTH IF
						
				} else if (contentType.contains("text/plain") || contentType.contains("text/html")) {
				//Create object to hold message text.
					Object content = message.getContent();
				//get message content.
					if (content != null) {
						messageContent = content.toString();
					}//null catcher IF
				}//End IF ELSE contentType.contains "Multipart"
					
			//This right here is just formating for the user.
                System.out.println("\nMessage #" + (email + 1) + ":");
				System.out.println("\t From: " + from);
				System.out.println("\t Subject: " + subject);
				System.out.println("\t Sent Date: " + sentDate);
				System.out.println("\t Attachments: " + attachFiles);
			}//End For loop for fetching Emails.
		
		//Disconnect
			folderInbox.close(false);
			store.close();

		} catch (NoSuchProviderException ex) {
			System.out.println("No provider for pop3.");
		} catch (IllegalStateException ex) {
			System.out.println("Folder is not in a closed state.");
		} catch ( MessagingException ex) {
			 System.out.println(ex.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
		}//End try catch block.
	}//End SaveAttachments method. This is the big method.

//Method to check if attachment name is already in use.
//If it is a file it will delete the file to make way for the new file. 
//If it is a folder we are looking for it will add it to the saveToDirectory variable
	public void checkFileIsReal(File f, String fileName) {
	//Declare boolean and array.
	//The boolean will tell us when we have found the folder.
		ArrayList<String> files = new ArrayList<String>();
		boolean fileFound = false;
		
	//Main Try Catch block
		try {
		//Check if the f is a file or folder	
			if (f.isDirectory()) {
			//If we are searching for a directory use this code.
				if (f.getName().equalsIgnoreCase(fileName) 
						|| f.getName().toLowerCase().startsWith(fileName.toLowerCase()) 
						|| (f.getName().toLowerCase().endsWith(fileName.toLowerCase()))) {
					//Testing
						System.out.println("File found...");
					
					//Now we change file found to true.
						fileFound = true;	
						
					//If saveToDirectory does not contain this folder name add it.
						if (saveToDirectory.contains(lastName) == false) {
							setSaveToDirectory(saveToDirectory + File.separator + f.getName());
						}//End saveToDirectory Checker.
						
					//Testing
						System.out.println("\t\t\tSave to: " + saveToDirectory);
				} else {
				//This runs through more folders if we have not found what we are looking for.
				//Creates an array of all the current files we are looking at.
					File [] fi = f.listFiles();
				//starts moving through all the files and folders in the array.
					for (int i = 0; i < fi.length; i++) {
					//uses recursion to check files/folder on the above array.
						checkFileIsReal(fi[i], fileName);
					}//End For loop
				}//End ELSE IF
			} else {
			//This Else if from the beginning IF statement checking if the selected
			//file is a Directory or a file. Now we know the file we have is not a folder.
			//It is a file. 
			//Now we check if the name matches what we are looking for.
				if (f.getName().equalsIgnoreCase(fileName) || 
						f.getName().toLowerCase().startsWith(fileName.toLowerCase()) || 
						(f.getName().toLowerCase().endsWith(fileName.toLowerCase()))) {
				//This will add the file we found to a print out list. 
					files.add(f.getName().toString() + ", ");
				
				//Change fileFound Status.	
					fileFound = true;
				}//end Long IF ELSE Check
			//Delete the file if we have found the correct file and if it is not a folder.
				if (fileFound == true && f.isDirectory() != true) {
					if (f.delete()) {
						System.out.println("File Deleted...");
					}//End delete IF
				}//End IF fileFound is true
			}//End ELSE/IF directory check
			
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}//End Try Catch
	}//End of checkFileIsReal Method

//This is declaring the string to show us where we will save the file.
	@SuppressWarnings("unused")
	private String saveToDirectory;

//This begins the Login screen.
	 @Override
	 public void start(Stage stage) throws Exception {
	     FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/FXMLTest.fxml"));
	     Parent root = loader.load();
	     Scene scene = new Scene(root);
	     stage.setScene(scene);
	     stage.show();
	 }//End Start

//This method gets the client's First and Last name.
	 public void getClient(Message email) throws MessagingException {
		if(email.getSubject() != null) {
		//Declare variables.
			String subject = email.getSubject();
			 String clientFN = "";
			 String clientLN = "";
		//There are four patterns in use. here are the first three.
		//The fourth uses results from the first search to make the 4th pattern.
			 String pat1 = "- ";
			 String pat2 = "FU(B)";
			 String pat3 = " ";
		     Pattern p = Pattern.compile(Pattern.quote(pat2) + "(.+)" + Pattern.quote(pat1));
		       
		     String temp = "";
		     Matcher m = p.matcher(subject);
		     if(m.find()){
		     //This will delete everything we found so we can get the last name.
		    	 temp = (String)(m.group(1));
		         subject = subject.replaceAll(temp, "");
		         
		     //This will set up a new search to fin the last name.
		         Pattern x = Pattern.compile(Pattern.quote(pat3) + "(.+)" + Pattern.quote(pat3));
		         Matcher n = x.matcher(subject);
		         
		       //See if we can find pattern x
		         if(n.find()) {
		         //If we do save the finding to clientFN
		        	clientFN = (String)(n.group(1));
		        
		        //Get a substring for the first name. The first name is not really
		        //used in this version of the program. later development will implement RegEx 
		        //and first names to make the program more error proof.
		            clientLN = subject.substring(subject.indexOf(clientFN) +clientFN.length() + 1);
		         }//end FIND IF N
		     }//End find IF M
		     firstName = clientFN;
		     lastName = clientLN;
		}//End NULL catcher
	 }//End getClient method

//Setter declaration.
//This method will set the Username.
	 public static void setUsername(String e) {
			username = e;
	}//Set Username 
//This method will set the Password.
	 public static void setPassword(String e) {
			password = e;
	}//set password
//This is a method to set the save directory path.	 
	 public void setSaveToDirectory(String dir) {
		this.saveToDirectory = dir;
	}//End setSaveToDirectory.	
	 
}//ControlClass Class


