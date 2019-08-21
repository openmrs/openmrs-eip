package org.openmrs.sync.odoo.config;

/**
 * Class holding Odoo connection properties
 */
public class OdooProperties {

    private String url;

    private String dbName;

    private String username;

    private String password;

    /**
     * Path to Odoo url
     * @return url
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Path to Odoo database name
     * @return db name
     */
    public String getDbName() {
        return dbName;
    }

    public void setDbName(final String dbName) {
        this.dbName = dbName;
    }

    /**
     * Path to Odoo User name
     * @return user name
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Path to Odoo password
     * @return password
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
