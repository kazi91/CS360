package databasePart1;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import application.User;
import application.Answer;

/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, role management, and handling invitation codes.
 */

public class DatabaseHelper {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	private Connection connection = null;
	private Statement statement = null;

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();

			//You can use this command to clear the database and restart from fresh.
			statement.execute("DROP ALL OBJECTS");
//
//			statement.execute("DROP TABLE IF EXISTS UserRoles;");
//			statement.execute("DROP TABLE IF EXISTS cse360users;");
//			statement.execute("DROP TABLE IF EXISTS InvitationCodes;");
//			statement.execute("DROP TABLE IF EXISTS Feedback;");
//			statement.execute("DROP TABLE IF EXISTS Answers;");
//			statement.execute("DROP TABLE IF EXISTS Questions;");

			createTables(); // Create tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/**
	 * Expose database connection
	 */
	public Connection getConnection() {
		return connection;
	}


	//Creates necessary tables if they don't exist.
	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "email VARCHAR(255) UNIQUE, "
				+ "isUsingOTP BOOLEAN DEFAULT FALSE)";
		statement.execute(userTable);

		String userRolesTable = "CREATE TABLE IF NOT EXISTS UserRoles ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userId INT, "
				+ "role VARCHAR(20), "
				+ "FOREIGN KEY (userId) REFERENCES cse360users(id) ON DELETE CASCADE)";
		statement.execute(userRolesTable);

		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
				+ "code VARCHAR(10) PRIMARY KEY, "
				+ "isUsed BOOLEAN DEFAULT FALSE, "
				+ "roles VARCHAR(255), "
				+ "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
		statement.execute(invitationCodesTable);

		String questionsTable = "CREATE TABLE IF NOT EXISTS Questions ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userId INT, "
				+ "questionText TEXT NOT NULL, "
				+ "timestamp TIMESTAMP DEFAULT FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm'), "
				+ "FOREIGN KEY (userId) REFERENCES cse360users(id) ON DELETE CASCADE)";
		statement.execute(questionsTable);

		String answersTable = "CREATE TABLE IF NOT EXISTS Answers ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "questionId INT, "
				+ "userId INT, "
				+ "answerText TEXT NOT NULL, "
				+ "timestamp TIMESTAMP DEFAULT FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm'), "
				+ "isAccepted BOOLEAN DEFAULT FALSE, "
				+ "FOREIGN KEY (questionId) REFERENCES Questions(id) ON DELETE CASCADE, "
				+ "FOREIGN KEY (userId) REFERENCES cse360users(id) ON DELETE CASCADE)";
		statement.execute(answersTable);

		String feedbackTable = "CREATE TABLE IF NOT EXISTS Feedback ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "questionId INT, "
				+ "questionOwnerId INT, "
				+ "reviewerId INT, "
				+ "feedbackText TEXT NOT NULL, "
				+ "timestamp TIMESTAMP DEFAULT FORMATDATETIME(CURRENT_TIMESTAMP, 'yyyy-MM-dd HH:mm'), "
				+ "readStatus BOOLEAN DEFAULT FALSE, "
				+ "FOREIGN KEY (questionId) REFERENCES Questions(id) ON DELETE CASCADE, "
				+ "FOREIGN KEY (reviewerId) REFERENCES cse360users(id) ON DELETE CASCADE, "
				+ "FOREIGN KEY (questionOwnerId) REFERENCES cse360users(id) ON DELETE CASCADE)";
		statement.execute(feedbackTable);

	}

	// Helper method to format the timestamp properly
	private String formatTimestamp(Timestamp timestamp) {
		if (timestamp == null) return "N/A"; // Handle null case
		LocalDateTime localDateTime = timestamp.toLocalDateTime();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // Remove seconds
		return localDateTime.format(formatter);
	}

	public void clearQuestions() throws SQLException {
		String query = "DELETE FROM Questions;";
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(query);
		}
	}

	public void clearAnswers() throws SQLException {
		String query = "DELETE FROM Answers;";
		try (Statement stmt = connection.createStatement()) {
			stmt.execute(query);
		}
	}
	//

	//Checks if the database is empty.
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	//Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, email) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getEmail());
			pstmt.executeUpdate();
		}
	}

	public String getCurrentUserID(User user) {
		String query = "SELECT id, userName, password, email, 'student' AS role FROM cse360users LIMIT 1";
		try (Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {
			if (rs.next()) {
				return rs.getString("id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;  // Return null if no user is found
	}

	public User getCurrentUser() {
		String query = "SELECT id, userName, password, email, 'student' AS role FROM cse360users LIMIT 1";
		try (Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {
			if (rs.next()) {
				return new User(
						rs.getString("id"),
						rs.getString("userName"),
						rs.getString("password"),
						rs.getString("email"),
						rs.getString("role")
				);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;  // Return null if no user is found
	}

	//Checks if a user already exists in the database.
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // If an error occurs, assume user doesn't exist
	}

	//Assigns a role to a user.
	public void addUserRole(String userName, String role) {
		String query = "INSERT INTO UserRoles (userId, role) VALUES ((SELECT id FROM cse360users WHERE userName = ?), ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, role);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Validates user login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next(); // Return true if a match is found
			}
		}
	}

	//Removes a role from a user.
	public void removeUserRole(String userName, String role) {
		String query = "DELETE FROM UserRoles WHERE userId = (SELECT id FROM cse360users WHERE userName = ?) AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, role);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Retrieves all roles assigned to a user.
	public List<String> getUserRoles(String userName) {
		List<String> roles = new ArrayList<>();
		String query = "SELECT role FROM UserRoles WHERE userId = (SELECT id FROM cse360users WHERE userName = ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				roles.add(rs.getString("role")); // Return roles if user exists
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return roles;
	}

	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode(List<String> rolesList) {
		String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
		String roles = String.join(", ", rolesList);
		String query = "INSERT INTO InvitationCodes (code, roles) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.setString(2, roles);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return code;
	}


	//Deletes a user from the database.
	public void deleteUser(String userName) {
		String query = "DELETE FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	//Lists all users in the database.
	public List<String[]> listUsers() {
		List<String[]> usersList = new ArrayList<>();

		String query = "SELECT cse360users.id, cse360users.userName, cse360users.password, cse360users.email, " +
				"COALESCE(GROUP_CONCAT(UserRoles.role SEPARATOR ', '), 'No Roles Assigned') AS roles " +
				"FROM cse360users " +
				"LEFT JOIN UserRoles ON cse360users.id = UserRoles.userId " +
				"GROUP BY cse360users.id, cse360users.userName, cse360users.password, cse360users.email";


		try (Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {


			System.out.println("User List:");
			while (rs.next()) {

				String currentId = String.valueOf(rs.getInt("id"));
				String userName = rs.getString("userName");
				String currentUserName = null;
				String currentPassword = rs.getString("password");
				String email = rs.getString("email");
				String currentRole = rs.getString("roles");

				// Check if we're still on the same user
				if (!userName.equals(currentUserName)) {
					System.out.println("ID: " + currentId +
							", Username: " + userName +
							", Role: " + currentRole +
							", Email: " + email);
					String[] userInfo = {currentId, userName, currentPassword, email, currentRole};
					usersList.add(userInfo);
					currentUserName = userName;
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return usersList;
	}

	//Validates an invitation code to ensure it's unused.
		public List<String> validateInvitationCode(String code) {
			String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, code);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					
					Timestamp initialTime = rs.getTimestamp("created_at");
		            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
		            // 1 hour deadline
		            if (currentTime.getTime() - initialTime.getTime() > 3600000) {
		                markInvitationCodeAsUsed(code);
		                return null;
		            }
					
					markInvitationCodeAsUsed(code);
					String roles = rs.getString("roles");
					List<String> rolesList = new ArrayList<>();
					for (String eachRole : roles.split(",")) {
		                rolesList.add(eachRole.trim());
		            }
					return rolesList;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}


	//Marks an invitation code as used.
	private void markInvitationCodeAsUsed(String code) {
		String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// allows the admin to set otp
	public void setOneTimePassword(String userName, String newPassword) throws SQLException {
		String query = "UPDATE cse360users SET password = ?, isUsingOTP = TRUE WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newPassword);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		}
	}


	// checks if that user has an otp
	public boolean isUsingOTP(String userName) throws SQLException {
		String query = "SELECT isUsingOTP FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			ResultSet rs = pstmt.executeQuery();
			return rs.next() && rs.getBoolean("isUsingOTP");
		}
	}


	// updates password after using an otp
	public void updateUserPassword(String userName, String newPassword) throws SQLException {
		String query = "UPDATE cse360users SET password = ?, isUsingOTP = FALSE WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newPassword);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		}
	}

	// ---------------------------------------- QUESTION SECTION ----------------------------------------

	//lists all questions
	public List<String[]> getAllQuestions() throws SQLException {
		List<String[]> questionsList = new ArrayList<>();
		String query = "SELECT q.id, u.userName, q.questionText, q.timestamp FROM Questions q JOIN cse360users u ON q.userId = u.id";

		try (Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				// Retrieve timestamp and format it
				Timestamp timestamp = rs.getTimestamp("timestamp");
				String formattedTime = formatTimestamp(timestamp);

				String[] questionData = {
						String.valueOf(rs.getInt("id")),
						rs.getString("userName"),
						rs.getString("questionText"),
						formattedTime // Use formatted timestamp here
				};
				questionsList.add(questionData);
			}
		}
		return questionsList;
	}

	public List<String[]> searchRelatedQuestions(String keyword) throws SQLException {
		List<String[]> relatedQuestions = new ArrayList<>();

		// Query to find questions that contain the keyword
		String query = "SELECT id, userId, questionText, timestamp FROM Questions WHERE LOWER(questionText) LIKE LOWER(?)";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, "%" + keyword.toLowerCase() + "%"); // Search for partial matches
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String[] questionData = {
						String.valueOf(rs.getInt("id")),
						String.valueOf(rs.getInt("userId")),
						rs.getString("questionText"),
						rs.getTimestamp("timestamp").toString()
				};
				relatedQuestions.add(questionData);
			}
		}
		return relatedQuestions;
	}



	// Create a new question
	public void createQuestion(int userId, String questionText) throws SQLException {
		if (questionText == null || questionText.trim().isEmpty()) {
			throw new IllegalArgumentException("Question cannot be empty.");
		}

		String query = "INSERT INTO Questions (userId, questionText) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userId);
			pstmt.setString(2, questionText);
			pstmt.executeUpdate();
		}
	}

	// Update an existing question
	public void updateQuestion(int questionId, String newText) throws SQLException {
		if (newText == null || newText.trim().isEmpty()) {
			throw new IllegalArgumentException("Updated question cannot be empty.");
		}

		String query = "UPDATE Questions SET questionText = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newText);
			pstmt.setInt(2, questionId);
			pstmt.executeUpdate();
		}
	}

	// Delete a question
	public void deleteQuestion(int questionId) throws SQLException {
		String query = "DELETE FROM Questions WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionId);
			pstmt.executeUpdate();
		}
	}

	// ---------------------------------------- ANSWER SECTION ----------------------------------------

	//lists all answers for selected question
	public List<Answer> getAnswersForQuestion(int questionId) throws SQLException {
		List<Answer> answersList = new ArrayList<>();
		String query = "SELECT a.id, a.questionId, u.userName, a.answerText, a.timestamp, a.isAccepted " +
				"FROM Answers a JOIN cse360users u ON a.userId = u.id " +
				"WHERE a.questionId = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				answersList.add(new Answer(
						rs.getInt("id"),
						rs.getInt("questionId"),
						rs.getString("userName"),
						rs.getString("answerText"),
						rs.getTimestamp("timestamp").toString(),
						rs.getBoolean("isAccepted")
				));
			}
		}
		return answersList;
	}

	// Create a new answer
	public void createAnswer(int questionId, int userId, String answerText) throws SQLException {
		if (answerText == null || answerText.trim().isEmpty()) {
			throw new IllegalArgumentException("Answer cannot be empty.");
		}

		String query = "INSERT INTO Answers (questionId, userId, answerText) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionId);
			pstmt.setInt(2, userId);
			pstmt.setString(3, answerText);
			pstmt.executeUpdate();
		}
	}

	// Update an answer
	public void updateAnswer(int answerId, String newText) throws SQLException {
		if (newText == null || newText.trim().isEmpty()) {
			throw new IllegalArgumentException("Updated answer cannot be empty.");
		}

		String query = "UPDATE Answers SET answerText = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newText);
			pstmt.setInt(2, answerId);
			pstmt.executeUpdate();
		}
	}

	// Delete an answer
	public void deleteAnswer(int answerId) throws SQLException {
		String query = "DELETE FROM Answers WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, answerId);
			pstmt.executeUpdate();
		}
	}

	// Mark an answer as accepted
	public void markAnswerAsAccepted(int answerId) throws SQLException {
		String query = "UPDATE Answers SET isAccepted = TRUE WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, answerId);
			pstmt.executeUpdate();
		}
	}

	// ---------------------------------------- FEEDBACK SECTION ----------------------------------------
	public void provideFeedback(int questionId, int reviewerId, String feedbackText) throws SQLException {
		if (feedbackText == null || feedbackText.trim().isEmpty()) {
			throw new IllegalArgumentException("Feedback cannot be empty.");
		}

		// Get the owner of the question
		String getOwnerQuery = "SELECT userId FROM Questions WHERE id = ?";
		int questionOwnerId = -1;

		try (PreparedStatement ownerStmt = connection.prepareStatement(getOwnerQuery)) {
			ownerStmt.setInt(1, questionId);
			ResultSet rs = ownerStmt.executeQuery();
			if (rs.next()) {
				questionOwnerId = rs.getInt("userId");
			}
		}

		if (questionOwnerId == -1) {
			throw new IllegalArgumentException("Invalid question ID.");
		}

		// Insert the feedback with questionOwnerId
		String query = "INSERT INTO Feedback (questionId, questionOwnerId, reviewerId, feedbackText) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionId);
			pstmt.setInt(2, questionOwnerId);
			pstmt.setInt(3, reviewerId);
			pstmt.setString(4, feedbackText);
			pstmt.executeUpdate();
		}
	}


	public List<String[]> getFeedbackForQuestion(int questionId, int userId) throws SQLException {
		List<String[]> feedbackList = new ArrayList<>();

		// Ensure the requester is the question owner
		String ownerCheckQuery = "SELECT userId FROM Questions WHERE id = ?";
		try (PreparedStatement ownerCheckStmt = connection.prepareStatement(ownerCheckQuery)) {
			ownerCheckStmt.setInt(1, questionId);
			ResultSet rs = ownerCheckStmt.executeQuery();
			if (rs.next()) {
				int questionOwnerId = rs.getInt("userId");
				if (questionOwnerId != userId) {
					throw new SecurityException("Unauthorized access. You cannot view this feedback.");
				}
			} else {
				throw new IllegalArgumentException("Question not found.");
			}
		}

		// If check passes, retrieve feedback
		String query = "SELECT f.id, u.userName, f.feedbackText, f.timestamp FROM Feedback f JOIN cse360users u ON f.reviewerId = u.id WHERE f.questionId = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String[] feedbackData = {
						String.valueOf(rs.getInt("id")),
						rs.getString("userName"),
						rs.getString("feedbackText"),
						rs.getTimestamp("timestamp").toString()
				};
				feedbackList.add(feedbackData);
			}
		}
		return feedbackList;
	}

	public List<String[]> getUnreadFeedbackForUser(int userId) throws SQLException {
		List<String[]> unreadFeedbackList = new ArrayList<>();
		String query = "SELECT f.id, u.userName, f.feedbackText, f.timestamp, f.readStatus FROM Feedback f "
				+ "JOIN cse360users u ON f.reviewerId = u.id "
				+ "WHERE f.questionOwnerId = ? AND f.readStatus = FALSE";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String[] feedbackData = {
						String.valueOf(rs.getInt("id")),
						rs.getString("userName"),
						rs.getString("feedbackText"),
						rs.getTimestamp("timestamp").toString(),
						String.valueOf(rs.getBoolean("readStatus")) // Read status as String
				};
				unreadFeedbackList.add(feedbackData);
			}
		}
		return unreadFeedbackList;
	}


	public void markFeedbackAsRead(int feedbackId, int userId) throws SQLException {
		String checkQuery = "SELECT questionOwnerId FROM Feedback WHERE id = ?";
		try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
			checkStmt.setInt(1, feedbackId);
			ResultSet rs = checkStmt.executeQuery();
			if (rs.next()) {
				int questionOwnerId = rs.getInt("questionOwnerId");
				if (questionOwnerId != userId) {
					throw new SecurityException("Unauthorized access. You cannot mark this feedback as read.");
				}
			} else {
				throw new IllegalArgumentException("Feedback not found.");
			}
		}

		String query = "UPDATE Feedback SET readStatus = TRUE WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, feedbackId);
			pstmt.executeUpdate();
		}
	}

	//Closes the database connection and statement.
	public void closeConnection() {
		try{
			if(statement!=null) statement.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		try {
			if(connection!=null) connection.close();
		} catch(SQLException se){
			se.printStackTrace();
		}
	}

	public String getUserIdByUsername(String username) {
		String query = "SELECT id FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return String.valueOf(rs.getInt("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
}
