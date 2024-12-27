package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller implements Initializable {
	
		@Override
		public void initialize(URL url, ResourceBundle rb) {
    }
		@FXML
	    private TextField email, username, name, loginName;
	    @FXML
	    private Text lblUsernameTaken, lblIncFields, lblUnregisteredUser, txtsignUp, logIn;
	    @FXML
	    private PasswordField password, logPassword;
	    @FXML
	    private Button signUp, btnBack, btnLogin;	    
	    @FXML
	    private Pane login;    
	    @FXML
	    private VBox vbox;

	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private UserDataBase userDataBase = new UserDataBase(); 
	
	//signup button
	public void signupHandles(ActionEvent event) throws IOException {    
	    String nameText = name.getText();
	    String emailText = email.getText();
	    String usernameText = username.getText();  
	    String passwordText = password.getText();  
	    
	    // Check if any field is empty
	    if (nameText.isEmpty() || emailText.isEmpty() || usernameText.isEmpty() || passwordText.isEmpty()) {
	    	lblIncFields.setText("Please fill in all fields.");
	        return; 
	    }
	    
	    if (!isValidEmail(emailText)) {
	        lblIncFields.setText("Please enter a valid email address.");
	        return;
	    }
	    
	    UserDataBase userDataBase = new UserDataBase(); 
	    boolean isRegistered = userDataBase.registerUser(emailText, passwordText, usernameText);

	  
	    if (isRegistered) {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("/displayPage.fxml"));    
	        root = loader.load();    

	        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
	        scene = new Scene(root);
	        stage.setScene(scene);
	        stage.show();
	        
	    } else {
	        lblUsernameTaken.setText("Username already exists. Please use a different username.");

	        name.clear();
	        email.clear();
	        username.clear();
	        password.clear();

	        username.requestFocus();
	    }
	}
	
	private boolean isValidEmail(String email) {
	    String emailRegex = "^[a-zA-Z0-9._%+-]+@(gmail|yahoo|yandex|protonmail)\\.com$";
	    return email.matches(emailRegex);
	}
	
	//log in clickable text
	@FXML
	void logInHandles(MouseEvent event) throws IOException {		
		 Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
	     Scene scene = logIn.getScene();
	     root.translateXProperty().set(scene.getHeight());
	    
        vbox.getChildren().add(root);
	     }

	//back button
	@FXML
	    void previous(ActionEvent event) {
	        try {
	        	
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml")); 
	            root = loader.load();

	            Scene scene = new Scene(root);
	            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
	            currentStage.setScene(scene);
	            currentStage.show();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }	
		
	//sign up clickable text
		@FXML
		public void signup(MouseEvent event) throws IOException {	
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));	
			root = loader.load();	
				
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();		
		}
		
		@FXML
	    void Login(ActionEvent event) throws IOException {
			UserDataBase database = new UserDataBase();
	
		     String name = loginName.getText();
		     String password = logPassword.getText();	     
		     String registeredName = database.loginUser(name, password);
		     
		     if (registeredName == null) {
		         lblUnregisteredUser.setText("You don't have an account. Sign up to continue.");
		         
		     } else {
		    	 FXMLLoader loader = new FXMLLoader(getClass().getResource("/displayPage.fxml"));
		         loader.setControllerFactory(controller -> new displayPageController(database, registeredName));

		            root = loader.load();
		            
					stage = (Stage)((Node)event.getSource()).getScene().getWindow();
					scene = new Scene(root);
					stage.setScene(scene);
					stage.show();	   
		    	  
		     }
			
		  
	    }
		
	
}
