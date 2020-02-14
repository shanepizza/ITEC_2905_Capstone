# ITEC_2905_Capstone
## Synopsis
#### This is my Capstone project.   
The goal of this program is to reduce the time wasted by guardianship attorneys when they have to update files on their PC. This program will find and download those files for them. 

## How it Works
This project will get Emails based on chosen Characters set by the user and found in the subject line of the email. It will then open emails selected by the program and download any attachments it finds to the selected 
location.  

When the program finds a file that it needs to replace on the computer it will delete the file and then download the new file with the same name. If no file to replace is found the program will download the file from the email anyways. 

## Code Snipet
```
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
												
		//Get client name.
			getClient(message);
		//Add client name to search directory so we only search for the file under the correct name.
			checkFileIsReal(f, lastName);
			File file = new File(saveToDirectory);
		//Check Attachment exists in folder. Delete it if it does.
			checkFileIsReal(file, fileName);
		//This is where we save the file to the folder.	
			part.saveFile(saveToDirectory + File.separator + part.getFileName() /*+ date*/);
						
		} else {
			messageContent = part.getContent().toString();
		}//End IF/ELSE ATTACHMENT.Equals 
```

## Bugs
The program has a Login screen that curently must be set manually for the email address and password the user needs. The login screen mainly stops the program from crashing when the wrong username and password are given. 

## To Use
You will need the Java Libraries:
  * jakarta.activation.jar
  * jakarta.mail.jar
  * jsoup-1.12.1.jar  
To use, download the zip file called 'Capstone Project' and open it in a compiler.  
The current file path looks for a folder called 'GUARDIANSHIP' in the documents folder.   
This can be changed by changing the 'saveToDirectory' variable in the main method to the correct file path. 

