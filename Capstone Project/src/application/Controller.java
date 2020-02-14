package application;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller {
//Declare labels for FXML file
	@FXML
	public Label label;
	
	@FXML
	private TextField txtUsername;
	
	@FXML
	private TextField txtPassword;
//Strings for use.
	public String username;
	public String password;
	
//Methods that do something. Not super sure what.
	public Controller() {
		
	}
	public void initialize() {
        // TODO
	}
	
	//@FXML
	public void Login(ActionEvent event) {
	//Null Catcher
		if (txtUsername.getText() != null) {
		//check if the username and password match what they are supposed to.
		    if (txtUsername.getText().equals("testingcapstoneproject@gmail.com") 
		    		&& txtPassword.getText().equals("videogamehigh")) {
		 
		    //Get what was typed and assign it to a string.
		    	username = txtUsername.getText();
		    	password = txtPassword.getText();
		    	
		    //Use these right here to auto-set the username and password.
		    	//username = "ExampleEmail@gmail.com";
		    	//password = "************";
		    	username = "testingcapstoneproject@gmail.com";
		    	password = "videogamehigh";
		    	
		    //Set the Username and Password back in the conrtoller class.
		    	ControlClass.setPassword(password);
		    	ControlClass.setUsername(username);
	
		    //This will close the Login window. 
		    	final Node source = (Node) event.getSource();
		    	final Stage stage = (Stage) source.getScene().getWindow();
		        stage.close();
		    }  else {
		    //This is if they fail to type in what they are supposed to.
		    	label.setText("Login Failed");
		    }//end IF ELSE
		}//Null catcher
		// TODO
	}//End Login method
}//
