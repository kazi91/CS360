package application;

import databasePart1.DatabaseHelper;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
	private String id;
    private String userName;
    private String password;
    private String role;
    private String email;
    private boolean isUsingOTP;
    private DatabaseHelper databaseHelper;

    // Constructor to initialize a new User object with userName, password, email, and role
    public User(String userName, String password, String email, String role, DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.id = databaseHelper.getUserIdByUsername(userName);
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.email = email;
        this.isUsingOTP = false;
    }

    // Constructor used just for returning id in adminHomePage
    public User( String id, String userName, String password, String email, String role) {
    	this.id = id;
    	this.userName = userName;
        this.password = password;
        this.role = role;
        this.email = email;
        this.isUsingOTP = false;
    }

    // Sets the role of the user.
    public void setRole(String role) {
    	this.role=role;
    }

    public String getUserId() {	return id; }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public boolean isUsingOTP() { return isUsingOTP; }
    
    // sets the otp boolean flag of the user.
    public void setUsingOTP(boolean isUsingOTP) {
    	this.isUsingOTP = isUsingOTP;
    }

    public void setId(String id) {this.id = id;}

}
