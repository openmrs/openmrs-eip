package org.openmrs.eip.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Contains utilities to interact with DriverManager
 */
public class DriverManagerUtils {
	
	/**
	 * @see DriverManager#getConnection(String, String, String)
	 */
	public static Connection getConnection(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}
	
}
