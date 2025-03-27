package junitTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.List;
import application.Questions;

class CaseTwo {

	private static DatabaseHelper dbHelper; 
	
	
	// method used to create test questions for the class
	 private void addTestQuestion(DatabaseHelper dbHelper, int userId, String questionText) throws SQLException {
	        if (questionText != null && !questionText.trim().isEmpty()) {
	            dbHelper.createQuestion(userId, questionText);
	        }
	    }
	
	
	// test if questions are created and stored 
	
	@Test
	void testCaseTwo() throws SQLException {
	
		dbHelper = new DatabaseHelper();
		dbHelper.connectToDatabase();
		Questions questions = new Questions(dbHelper);
		
		addTestQuestion(dbHelper, 1, "Where am I?"); // create and store test questions in DB
		addTestQuestion(dbHelper, 2, "Where should I go?");
		addTestQuestion(dbHelper, 3, " "); // incorrect submission, should not be stored
		
		
		List<String[]> result = questions.getAllQuestions(); // get questions form DB
		
		//print the correct submissions and check if the incorrect submission was printed
		 for (String[] questionData : result) {
		        System.out.println("ID: " + questionData[0] + ", User: " + questionData[1] + ", Question: " + questionData[2] + ", Timestamp: " + questionData[3]); 
		    }
	}

	// test if edited questions are stored 
	@Test
	void testCaseTwoEdit() throws SQLException {
	
		dbHelper = new DatabaseHelper();
		dbHelper.connectToDatabase();
		Questions questions = new Questions(dbHelper);
		
		addTestQuestion(dbHelper, 1, "Where am I?"); // store valid question 
		
		questions.updateQuestion(1, "What am i doing?"); // update stored question 		
		 
		List<String[]> result = questions.getAllQuestions(); // get questions form DB
		
		//print the correct submissions and check if the incorrect submission was printed
		 for (String[] questionData : result) {
		        System.out.println("ID: " + questionData[0] + ", User: " + questionData[1] + ", Question: " + questionData[2] + ", Timestamp: " + questionData[3]); 
		    }

	}

}
