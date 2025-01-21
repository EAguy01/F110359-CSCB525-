// BaseDAO.java
package com.homemanager.dao;

import com.homemanager.util.DatabaseConnection;
import java.sql.Connection;
// dava spodelen conenction za rabota s databaza
public abstract class BaseDAO {
    protected Connection connection;
    // inicializira database i vruzkata kato vika databvaseconneciton.get..

    public BaseDAO() {
        this.connection = DatabaseConnection.getConnection();
    }
}