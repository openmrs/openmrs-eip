/*
 * Add Copyright
 */
package org.openmrs.eip;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Custom TestExecutionListener that resets all tables in the database by deleting all rows in them.
 * Typically, this listener should be configured to run after every test method has executed
 */
public class DeleteDataTestExecutionListener extends AbstractTestExecutionListener {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String DELETE = "DELETE FROM ";
	
	/**
	 * @see AbstractTestExecutionListener#afterTestMethod(TestContext)
	 */
	@Override
	public void afterTestMethod(TestContext testContext) throws Exception {
		ApplicationContext ctx = testContext.getApplicationContext();
		DataSource dataSource = ctx.getBean(Constants.MGT_DATASOURCE_NAME, DataSource.class);
		
		log.debug("Deleting all data from management DB tables...");
		try (Connection connection = dataSource.getConnection()) {
			deleteAllData(connection, "TEST", "h2");
		}
		
		dataSource = ctx.getBean(Constants.OPENMRS_DATASOURCE_NAME, DataSource.class);
		
		log.debug("Deleting all data from OpenMRS DB tables...");
		
		try (Connection connection = dataSource.getConnection()) {
			deleteAllData(connection, "openmrs", "mysql");
		}
	}
	
	/**
	 * Resets all tables in the test database by deleting all the rows in them
	 *
	 * @param connection JDBC Connection object
	 * @param dbName the name of the database containing the tables from which to delete the rows
	 */
	private void deleteAllData(Connection connection, String dbName, String dbms) throws SQLException {
		var tables = getTableNames(connection, dbName);
		var statement = connection.createStatement();
		var ENABLE_FOREIGN_KEY_CHECKS = dbms.equalsIgnoreCase("h2") ? "SET @FOREIGN_KEY_CHECKS=1"
		        : "SET FOREIGN_KEY_CHECKS=1";
		var DISABLE_FOREIGN_KEY_CHECKS = dbms.equalsIgnoreCase("h2") ? "SET @FOREIGN_KEY_CHECKS=0"
		        : "SET FOREIGN_KEY_CHECKS=0";
		
		try {
			statement.execute(DISABLE_FOREIGN_KEY_CHECKS);
			for (String tableName : tables) {
				if (doesTableExist(connection, tableName)) {
					log.debug("Deleting all data from table -> " + tableName);
					statement.executeUpdate(DELETE + tableName);
				}
			}
		}
		finally {
			if (statement != null) {
				statement.execute(ENABLE_FOREIGN_KEY_CHECKS);
				statement.close();
			}
		}
	}
	
	/**
	 * Gets the names of all the tables in the database
	 * 
	 * @param connection JDBC Connection object
	 * @param dbName the name of the database containing the tables
	 * @return the names of all the tables in the database
	 * @throws SQLException if an error occurs while querying the database
	 */
	private List<String> getTableNames(Connection connection, String dbName) throws SQLException {
		var databaseMetaData = connection.getMetaData();
		var tables = databaseMetaData.getTables(dbName, null, null, new String[] { "TABLE" });
		
		List<String> tableNames = new ArrayList<>();
		while (tables.next()) {
			tableNames.add(tables.getString("TABLE_NAME"));
		}
		return tableNames;
	}
	
	/**
	 * Checks whether a table exists in the database. This is done by attempting to query the table
	 *
	 * @param connection JDBC Connection object
	 * @param tableName the name of the table to check
	 * @return true if the table exists, false otherwise
	 * @throws SQLException if an error occurs while querying the table
	 */
	private static boolean doesTableExist(Connection connection, String tableName) throws SQLException {
		try {
			// Attempt to query the table
			connection.createStatement().executeQuery("SELECT * FROM " + tableName);
			return true; // The table exists
		}
		catch (SQLException e) {
			// The table does not exist
			return false;
		}
	}
}
