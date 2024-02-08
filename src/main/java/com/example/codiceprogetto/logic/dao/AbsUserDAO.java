package com.example.codiceprogetto.logic.dao;

import com.example.codiceprogetto.logic.utils.DBConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbsUserDAO {
    public int registerUser(String email, String password, String userType, String name, String surname, String sql) throws SQLException {
        int result;
        PreparedStatement stmt;
        Connection conn = DBConnectionFactory.getConn();

        stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        stmt.setString(1, email);
        stmt.setString(2, password);
        stmt.setString(3, userType);
        stmt.setString(4, name);
        stmt.setString(5, surname);

        result = stmt.executeUpdate();

        stmt.close();

        return result;
    }
}
