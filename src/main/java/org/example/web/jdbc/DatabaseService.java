package org.example.web.jdbc;

import org.example.Card;
import org.example.Student;

import java.sql.SQLException;

public interface DatabaseService {

    Card getCardFromDatabase(String cardName);

    Student getStudentFromDatabase(String studentFullName);

    void saveCardToDatabase(Card card);

    void createPokemonOwner(Student owner) throws SQLException;
}