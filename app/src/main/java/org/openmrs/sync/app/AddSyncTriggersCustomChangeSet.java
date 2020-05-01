package org.openmrs.sync.app;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Table;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Custom liquibase changeset that add missing after insert, update and delete triggers to all table(s) that are synced.
 */
public class AddSyncTriggersCustomChangeSet implements CustomTaskChange {

    private static final Logger log = LoggerFactory.getLogger(AddSyncTriggersCustomChangeSet.class);

    private static final String CREATE_SYNC_TRIGGER_TEMPLATE = "create_sync_trigger_template.sql";

    private static final String TRIGGER_NAME_PREFIX = "after_";

    private static final String[] OPERATIONS = new String[]{"INSERT", "UPDATE", "DELETE"};

    @Override
    public void execute(Database database) throws CustomChangeException {
        JdbcConnection jdbcConn = (JdbcConnection) database.getConnection();
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream(CREATE_SYNC_TRIGGER_TEMPLATE);
            final String sqlTemplate = IOUtils.toString(in, "UTF-8");
            for (TableToSyncEnum tableToSync : TableToSyncEnum.values()) {
                String tableName = tableToSync.getEntityClass().getAnnotation(Table.class).name();
                if ("patient".equals(tableName)) {
                    //TODO skip concepts
                    //Use SELECT uuid INTO @old_uuid from person where person_id = OLD.patient_id for patient table
                    log.info("Skipping adding triggers to " + tableName + " table");
                    continue;
                }

                for (String operation : OPERATIONS) {
                    if (triggerExists(tableName, operation, jdbcConn)) {
                        if (log.isDebugEnabled()) {
                            log.debug("After " + operation + " trigger already exists for table " + tableName);
                        }
                        continue;
                    }

                    log.info("Adding after " + operation + " trigger to " + tableName + " table");

                    Statement stmt = null;
                    try {
                        String sql = StringUtils.replace(sqlTemplate, "${tableName}", tableName);
                        sql = StringUtils.replace(sql, "${operationLower}", operation.toLowerCase());
                        sql = StringUtils.replace(sql, "${operationUpper}", operation);
                        sql = StringUtils.replace(sql, "${refRow}", "INSERT".equals(operation) ? "NEW" : "OLD");
                        stmt = jdbcConn.createStatement();
                        stmt.execute(sql);
                    } finally {
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (Exception e) {
                                throw new CustomChangeException(e);
                            }
                        }
                    }
                }
            }

        } catch (DatabaseException | SQLException | IOException e) {
            throw new CustomChangeException(e);
        }
    }

    @Override
    public String getConfirmationMessage() {
        return "Successfully run " + getClass().getSimpleName() + "!";
    }

    /**
     * Checks if the trigger with the specified name exists for the specified table.
     * MySQL triggers uniqueness is based on table, action timing and event.
     *
     * @param tableName the name of the table to match
     * @param event     the name of the operation to match
     * @param jdbcConn  the {@link JdbcConnection} object
     * @return true of the trigger exists otherwise false
     */
    public boolean triggerExists(String tableName, String event, JdbcConnection jdbcConn) throws SQLException, DatabaseException {
        Statement stmt = null;
        try {
            stmt = jdbcConn.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TRIGGERS");
            while (rs.next()) {
                if (tableName.equalsIgnoreCase(rs.getString("Table")) && event.equalsIgnoreCase(rs.getString("Event"))
                        && "after".equalsIgnoreCase(rs.getString("Timing"))) {

                    return true;
                }
            }
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }

        return false;
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }

}
