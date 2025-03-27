package junitTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import databasePart1.DatabaseHelper;
import java.sql.*;
import java.util.List;
import application.Questions;
import application.Answers;


class CaseFour {

	
	
	@Test
	void testQuestionAnswer() throws SQLException {

		dbHelper = new DatabaseHelper();
		dbHelper.connectToDatabase();
		Questions questions = new Questions(dbHelper);
		Answers answers = new Answers(dbHelper);
		
		dbHelper.createQuestion(1, "Where am I?"); // create and store test questions in DB
		
		  List<String[]> questionsList = questions.getAllQuestions();
		
	}
		fail("Not yet implemented");
	}

}
