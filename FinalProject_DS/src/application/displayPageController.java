package application;
	
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
	
public class displayPageController {
		
	private UserDataBase database;
	private String loggedInUsername;
	ObservableList<String> notes = FXCollections.observableArrayList();    
	    
	@FXML
	private ListView<String> noteListView, searchListView;
	@FXML
	private TextArea noteContent;
	@FXML
	private TextField nameField, noteTitle, UpdateEmail, UpdateName; 
	@FXML
	private Pane categoryPane, trashPane, analyticsPane, homePane, paneView, addPane, notesPane, profilePane, contentArea;	
	@FXML
	private PasswordField passwordField, RePass, UpdatePass;	 
	@FXML
	private Text lblIncorrectPassword;
		 
	private Stage stage;
	private Scene scene;
	private Parent root;
		 	
	    public displayPageController(UserDataBase database, String loggedInUsername) {
	        this.database = database;
	        this.loggedInUsername = loggedInUsername;

	        System.out.println("Logged in username passed to displayPageController: " + loggedInUsername);
	    }
	    
	    public displayPageController(String loggedInUsername) {
	        this.loggedInUsername = loggedInUsername;
	        System.out.println("Logged in username passed to displayPageController: " + loggedInUsername);
	    }
	        
	    public displayPageController() {
	        this.database = new UserDataBase();  
	        
	        System.out.println("Logged in username passed to displayPageController: " + loggedInUsername);
	    }
	    
	    public void setLoggedInUsername(String loggedInUsername) {
	        this.loggedInUsername = loggedInUsername;
	    }
		
	    private void showAlert(Alert.AlertType alertType, String title, String message) {
	        Alert alert = new Alert(alertType);
	        alert.setTitle(title);
	        alert.setHeaderText(null);
	        alert.setContentText(message);
	        alert.showAndWait();
	    }
	    
		private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
		        Alert alert = new Alert(alertType);
		        alert.setTitle(title);
		        alert.setHeaderText(header);
		        alert.setContentText(content);
		        alert.showAndWait();
		 }

	    @FXML
	    public void initialize() {
	        if (noteListView != null) {
	            ObservableList<String> notes = FXCollections.observableArrayList();
	            
	            loadNotes();
	            noteListView.setItems(notes);        
	      
	        } else {
	            System.out.println("noteListView is null");
	        }        
	        }
	
		 public Pane getPage(String fxmlFile) {
		        Pane page = null;  
		        try {
		            URL fileUrl = getClass().getResource(fxmlFile);
		            if (fileUrl == null) {
		                throw new java.io.FileNotFoundException("FXML file not found: " + fxmlFile);
		            }
		            page = new FXMLLoader().load(fileUrl);
	
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        return page;  
		    }
	
	    @FXML
	    void handlesAdd(MouseEvent event) {    
	    	try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add.fxml")); 
	            loader.setControllerFactory(controller -> new displayPageController(database, loggedInUsername));
	            
	            Pane addPane = loader.load(); 
	            paneView.getChildren().clear();
	            paneView.getChildren().add(addPane); 
	            
	        } catch (IOException e) {
	            e.printStackTrace(); 
	        }  	   	
	    }
	    
	    private void loadNotes() {
	        List<String> userNotes = database.getNotesForUser(loggedInUsername);
	        System.out.println("Notes for " + loggedInUsername + ": " + userNotes);  // Debugging output
	        
	        ObservableList<String> notes = FXCollections.observableArrayList(userNotes);
	        noteListView.setItems(notes);  
	        
	        noteListView.setOnMouseClicked(event -> {
	            String selectedNote = noteListView.getSelectionModel().getSelectedItem();
	            if (selectedNote != null) {
	                showNoteDetails(selectedNote);
	            }
	        });

	        Map<String, Integer> moodTotals = new HashMap<>();
	        for (String note : userNotes) {
	            Map<String, Integer> moodCount = MoodAnalyzer.analyzeMood(note);
	            moodCount.forEach((mood, count) -> moodTotals.merge(mood, count, Integer::sum));
	        }
	    }

	    private void showNoteDetails(String note) {
	        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	        alert.setTitle("Delete Note");
	        alert.setHeaderText("Are you sure you want to delete this note?");
	        alert.setContentText(note);

	        ButtonType deleteButton = new ButtonType("Delete", ButtonType.YES.getButtonData());
	        ButtonType cancelButton = new ButtonType("Cancel", ButtonType.NO.getButtonData());
	        alert.getButtonTypes().setAll(deleteButton, cancelButton);

	        alert.showAndWait().ifPresent(response -> {
	            if (response == deleteButton) {
	                deleteNotePermanently(note);
	            }
	        });
	    }

	    private void deleteNotePermanently(String note) {
	        boolean success = database.deleteNoteForUser(loggedInUsername, note); 
	        if (success) {
	            ObservableList<String> currentNotes = noteListView.getItems();
	            currentNotes.remove(note);  // Remove the note from the ListView
	            System.out.println("Note deleted permanently: " + note);
	        } else {
	            System.out.println("Failed to delete note.");
	        }
	    }

	    private Map<String, Double> displayMoodPercentages(Map<String, Integer> moodCount) {
	        int total = moodCount.values().stream().mapToInt(Integer::intValue).sum();

	        if (total == 0) {
	            return Collections.emptyMap(); 
	        }

	        Map<String, Double> moodPercentages = new HashMap<>();

	        for (Map.Entry<String, Integer> entry : moodCount.entrySet()) {
	            double percentage = (entry.getValue() / (double) total) * 100;
	            moodPercentages.put(entry.getKey(), percentage);
	        }
	        return moodPercentages;
	    }
	    
	    @FXML
	    void addNoteContent(ActionEvent event) {
	        String title = noteTitle.getText();
	        String content = noteContent.getText();

	        if (title != null && !title.trim().isEmpty() && content != null && !content.trim().isEmpty()) {
	            database.addNoteForUser(loggedInUsername, title + ": " + content);
	            
	            showAlert(Alert.AlertType.INFORMATION, "Note Added", "Your note has been saved successfully!", "The note is now added to your records.");
	            noteTitle.clear();
	            noteContent.clear();
	        } else {
	            showAlert(Alert.AlertType.WARNING, "Empty Fields", "Please fill in both the title and content of the note.", "Both fields are required.");
	        }
	    }
	    
	    public List<String> getNotesForUser(String username) {
	        return database.getNotesForUser(username);  
	    }
	    
	    public void displayMoodDataInDialog(Map<String, Integer> moodCount, Map<String, Double> moodPercentages) {
	        StringBuilder message = new StringBuilder("Mood Counts and Percentages:\n\n");

	        for (Map.Entry<String, Integer> entry : moodCount.entrySet()) {
	            message.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
	        }
	        message.append("\nMood Percentages:\n");
	        for (Map.Entry<String, Double> entry : moodPercentages.entrySet()) {
	            message.append(entry.getKey()).append(": ").append(String.format("%.2f", entry.getValue())).append("%\n");
	        }
	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle("Mood Analysis");
	        alert.setHeaderText("Mood Data for User");
	        alert.setContentText(message.toString());
	        alert.showAndWait();
	    }
    
		public void loadAnalyticsData() {
		        System.out.println("Logged-in username: " + loggedInUsername);
	
		        List<String> notes = getNotesForUser(loggedInUsername);
		        if (notes == null || notes.isEmpty()) {
		            showAlert(Alert.AlertType.WARNING, "No Notes Found", "There are no notes to analyze.", "Please add some notes first.");
		            return;
		        }
	
		        Map<String, Integer> moodTotals = new HashMap<>();
		        
		        for (String note : notes) {
		            Map<String, Integer> moodCount = MoodAnalyzer.analyzeMood(note);
		            moodCount.forEach((mood, count) -> moodTotals.merge(mood, count, Integer::sum));
		        }
	
		        int sadCount = moodTotals.getOrDefault("SAD", 0);
		        if (sadCount > 3) {
		            ComfortingMessageHandler.showComfortingMessage();
		        }

	
		        int totalMoods = moodTotals.values().stream().mapToInt(Integer::intValue).sum();
		        Map<String, Double> moodPercentages = new HashMap<>();
		        moodTotals.forEach((mood, count) -> moodPercentages.put(mood, (count * 100.0) / totalMoods));
	
		        CategoryAxis xAxis = new CategoryAxis();
		        NumberAxis yAxis = new NumberAxis();
	
		        xAxis.setLabel("Mood");
		        xAxis.setStyle("-fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold;");
	
		        yAxis.setLabel("Percentage");
		        yAxis.setStyle("-fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold;");
		        
		        yAxis.setLowerBound(10);
		        yAxis.setUpperBound(100);
		        yAxis.setTickUnit(10);
	
		        yAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
		            @Override
		            public String toString(Number object) {
		                return String.format("%.0f%%", object.doubleValue()); 
		            }
	
		            @Override
		            public Number fromString(String string) {
		                return Double.valueOf(string.replace("%", "")); 
		            }
		        });
	
		        XYChart.Series<String, Number> series = new XYChart.Series<>();
		        series.setName("Mood Counts");
	
		        Map<String, String> moodColors = new HashMap<>();
	
		        for (Map.Entry<String, Integer> entry : moodTotals.entrySet()) {
		            XYChart.Data<String, Number> data = new XYChart.Data<>(entry.getKey(), entry.getValue());
		            series.getData().add(data);
		        }
	
		        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
		        barChart.getData().add(series);
	
		        barChart.setPrefWidth(1200);
		        barChart.setPrefHeight(700);
		        barChart.setTitle("Mood Analytics");
		        barChart.setStyle("-fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold;");
		        barChart.setLegendVisible(false);
	
		        barChart.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
		            for (XYChart.Data<String, Number> data : series.getData()) {
		                String mood = data.getXValue();
		                String color = moodColors.getOrDefault(mood, "#a348fd");
		                data.getNode().setStyle("-fx-bar-fill: " + color + ";");
		            }
		        });
	
		        analyticsPane.getChildren().clear();
		        analyticsPane.getChildren().add(barChart);
		    }

	    @FXML
	    void moodP(MouseEvent event) throws IOException {
	        List<String> notes = getNotesForUser(loggedInUsername);
	        if (notes == null || notes.isEmpty()) {
	            showAlert(Alert.AlertType.WARNING, "No Notes Found", "There are no notes to analyze.", "Please add some notes first.");
	            return;
	        }

	        Map<String, Integer> moodTotals = new HashMap<>();
	        
	        for (String note : notes) {
	            Map<String, Integer> moodCount = MoodAnalyzer.analyzeMood(note);
	            moodCount.forEach((mood, count) -> moodTotals.merge(mood, count, Integer::sum));
	        }

	        int totalMoods = moodTotals.values().stream().mapToInt(Integer::intValue).sum();
	        Map<String, Double> moodPercentages = new HashMap<>();
	        moodTotals.forEach((mood, count) -> moodPercentages.put(mood, (count * 100.0) / totalMoods));

	        displayMoodDataInDialog(moodTotals, moodPercentages);
	    }    

	    @FXML
	    void handlesAnalytics(MouseEvent event) {
	    	try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/analytics.fxml"));
	            loader.setControllerFactory(controller -> new displayPageController(database, loggedInUsername));
	            
	            Pane analyticsPane = loader.load(); 
	            displayPageController controller = loader.getController();
	            controller.loadAnalyticsData(); 
	            
	            paneView.getChildren().clear();
	            paneView.getChildren().add(analyticsPane); 
	            
	        } catch (IOException e) {
	            e.printStackTrace(); 
	        }
	    }
	
	    @FXML
	    void handlesCategory(MouseEvent event) {
	    	List<String> notes = getNotesForUser(loggedInUsername); 
	    	Map<String, List<String>> categorizedNotes = new HashMap<>();

	        for (String note : notes) {
	            Map<String, Integer> moodCount = MoodAnalyzer.analyzeMood(note);
	            moodCount.keySet().forEach(mood -> categorizedNotes.computeIfAbsent(mood, k -> new ArrayList<>()).add(note));
	        }

	        StringBuilder categories = new StringBuilder("Categories by Mood:\n");
	        categorizedNotes.forEach((mood, noteList) -> {
	            categories.append(mood).append(":\n");
	            noteList.forEach(note -> categories.append(" - ").append(note).append("\n"));
	        });

	        Alert alert = new Alert(Alert.AlertType.INFORMATION);
	        alert.setTitle("Categories");
	        alert.setHeaderText("Notes Categorized by Mood");
	        alert.setContentText(categories.toString());
	        alert.showAndWait();	   	
	    }
	    
	    @FXML
	    void handlesNotes(MouseEvent event) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allnotes.fxml"));
	            loader.setControllerFactory(controller -> new displayPageController(database, loggedInUsername));

	            Pane notesPane = loader.load();
	            
	            displayPageController controller = loader.getController();            
	            controller.loadNotes(); 
	            
	            paneView.getChildren().clear();
	            paneView.getChildren().add(notesPane);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	 
	    @FXML
	    void handlesProfile(MouseEvent event) {
	    	try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile.fxml"));
	            loader.setControllerFactory(controller -> new displayPageController(database, loggedInUsername));

	            Pane profilePane = loader.load(); 
        
	            paneView.getChildren().clear();
	            paneView.getChildren().add(profilePane); 
	            
	        } catch (IOException e) {
	            e.printStackTrace(); 
	        }    	
	    }
	    		    
	    @FXML
	    public void logoutHandles(MouseEvent event) throws IOException {    	
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/logout.fxml"));	
			root = loader.load();	
				
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();	        	
	    }    
	    
	    @FXML
	    void loginHandles(ActionEvent event) throws IOException {
	        UserDataBase database = new UserDataBase();
	        String name = nameField.getText();
	        String password = passwordField.getText();     
	        String registeredName = database.loginUser (name, password);

	        if (registeredName == null) {
	            lblIncorrectPassword.setText("You don't have an account. Sign up to continue.");
	        } else {
	            System.out.println("Registered name: " + registeredName); // Debugging output	            
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/displayPage.fxml"));
	            loader.setControllerFactory(controller -> new displayPageController(database, registeredName));
	            
	            displayPageController controller = loader.getController();
	            controller.setLoggedInUsername(registeredName);
	            
	            root = loader.load();	                        
	            
	            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
	            scene = new Scene(root);
	            stage.setScene(scene);
	            stage.show();
	        }
	    }
	    
	    @FXML
	    void signupTxt(MouseEvent event) throws IOException {
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));	
			root = loader.load();	
				
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
	    }
	    
	    @FXML
	    void displayLogout(MouseEvent event) throws IOException {    	
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/logout.fxml"));	
			root = loader.load();	
				
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();	        	

	    }
    
	    @FXML
	    void update(ActionEvent event) {
	        if (database == null) {
	            System.out.println("Database is null. Cannot update profile.");
	            return;
	        }

	        if (loggedInUsername == null) {
	            System.out.println("Logged in username is null. Cannot update profile.");
	            return;
	        }

	        String[] userDetails = database.getUserDetails(loggedInUsername);
	        if (userDetails != null) {
	            System.out.println("Current user details: " + Arrays.toString(userDetails));

	            String newEmail = UpdateEmail.getText(); 
	            String newPassword = UpdatePass.getText(); 
	            String newRePassword = RePass.getText(); 

	            if (!newPassword.equals(newRePassword)) {
	                showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
	                return;
	            }

	            if (!newEmail.contains("@") || !newEmail.contains(".")) {
	                showAlert(Alert.AlertType.ERROR, "Error", "Invalid email format.");
	                return;
	            }
	            userDetails[1] = newEmail;  
	            userDetails[2] = newPassword;  

	           boolean updateSuccessful = database.updateUserProfile(loggedInUsername, newEmail, newPassword);
	            if (updateSuccessful) {
	                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully.");
	            } else {
	                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update profile.");
	            }
	        }  else {
	            showAlert(Alert.AlertType.ERROR, "Error", "User not found.");
	        }
	    }
	    
	}
