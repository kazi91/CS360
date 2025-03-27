package junitTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.List;
import application.Questions;

class CaseThree {

	private static DatabaseHelper dbHelper; 
	
	
	// method used to create test questions for the class
	 private void addTestQuestion(DatabaseHelper dbHelper, int userId, String questionText) throws SQLException {
	        if (questionText != null && !questionText.trim().isEmpty()) {
	            dbHelper.createQuestion(userId, questionText);
	        }
	    }
	
	
	// test if questions are deleted correctly 
	
	@Test
	void testCaseThree() throws SQLException {
	
		dbHelper = new DatabaseHelper();
		dbHelper.connectToDatabase();
		Questions questions = new Questions(dbHelper);
		
		addTestQuestion(dbHelper, 1, "Where am I?"); // create and store test questions in DB
		addTestQuestion(dbHelper, 2, "Where should I go?");
		
		List<String[]> result = questions.getAllQuestions(); // get questions form DB
		
		//print the correct submissions 
		 for (String[] questionData : result) {
		        System.out.println("ID: " + questionData[0] + ", User: " + questionData[1] + ", Question: " + questionData[2] + ", Timestamp: " + questionData[3]); 
		    }
		 
		 questions.deleteQuestion(1); // delete question 1
		 
		 
		//print the correct submissions and check if the incorrect submission was deleted
		 for (String[] questionData : result) {
		        System.out.println("ID: " + questionData[0] + ", User: " + questionData[1] + ", Question: " + questionData[2] + ", Timestamp: " + questionData[3]); 
		    }
		  		 
	}
}
