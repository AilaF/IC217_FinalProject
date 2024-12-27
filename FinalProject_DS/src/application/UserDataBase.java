package application;

import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class UserDataBase {
    private static final String DATABASE_FILE = "user_database.txt";
    private static final String NOTES_FILE = "user_notes.txt";
    private Map<String, String[]> users = new HashMap<>();
    private Map<String, List<String>> userNotes = new HashMap<>();

    public UserDataBase() {
        loadUsers();
        loadNotes();
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.put(parts[0], parts); //
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No user database found. Starting fresh.");
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private void loadNotes() {
        try (BufferedReader reader = new BufferedReader(new FileReader(NOTES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    String email = parts[0];
                    String note = parts[1];
                    userNotes.computeIfAbsent(email, k -> new ArrayList<>()).add(note);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No notes database found. Starting fresh.");
        } catch (IOException e) {
            System.err.println("Error loading notes: " + e.getMessage());
        }
    }
    
     private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            for (String[] userDetails : users.values()) {
                writer.write(String.join(",", userDetails));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }


    private void saveNotes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOTES_FILE))) {
            for (Map.Entry<String, List<String>> entry : userNotes.entrySet()) {
                for (String note : entry.getValue()) {
                    writer.write(entry.getKey() + ":" + note);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving notes: " + e.getMessage());
        }
    }

    public boolean registerUser(String username, String email, String password) {
        if (users.containsKey(username)) {
            return false; // Email already exists
        }
        users.put(username, new String[]{username, email, password});
        saveUsers();
        return true;
    }
    
    public boolean isUsernameTaken(String username) {
        return users.containsKey(username); // Check if the username exists in the database
    }


    public String loginUser(String username, String password) {
        if (users.containsKey(username)) {
            String[] userDetails = users.get(username);
            if (userDetails[2].equals(password)) {
                return userDetails[0]; // Return name if login is successful
            }
        }
        return null; 
    }

    public String[] getUserDetails(String username) {
        return users.get(username); 
    }

    public void addNoteForUser(String username, String note) {
        userNotes.computeIfAbsent(username, k -> new ArrayList<>()).add(note);
        saveNotes();
    }

    public List<String> getNotesForUser(String email) {
        return userNotes.getOrDefault(email, new ArrayList<>());
    }
    
    
    public boolean updateUserProfile(String username, String newEmail, String newPassword) {
        if (users.containsKey(username)) {
            String[] userDetails = users.get(username);

            userDetails[1] = newEmail;  // Update email
            userDetails[2] = newPassword;  // Update password

            saveUsers();
            return true;  
        }
        return false;  
    }

  
    public boolean deleteNoteForUser(String username, String note) {
        Path filePath = Paths.get("C:\\Users\\TGBK\\Documents\\FinalProject_DS\\user_notes.txt");
        List<String> linesToKeep = new ArrayList<>();
        boolean noteFound = false;

        try {
            List<String> lines = Files.readAllLines(filePath);

            for (String line : lines) {
                if (line.startsWith(username + ":") && line.substring(username.length() + 1).equals(note)) {
                    noteFound = true;  // Mark as found
                    continue;  
                }
                linesToKeep.add(line);  // Keep other notes
            }

            if (noteFound) {
                Files.write(filePath, linesToKeep, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("Note deleted: " + note);
                return true;
            } else {
                System.out.println("Note not found.");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



    
}