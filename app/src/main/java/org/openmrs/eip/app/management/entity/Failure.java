package org.openmrs.eip.app.management.entity;

import org.openmrs.eip.component.entity.DbEvent;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "failure")
public class Failure extends AbstractEntity {

    @Embedded
    @AttributeOverride(name = "entityId", column = @Column(name = "entity_id", nullable = false, updatable = false))
    @AttributeOverride(name = "entityTableName", column = @Column(name = "entity_table_name", nullable = false, updatable = false, length = 100))
    @AttributeOverride(name = "operation", column = @Column(name = "operation", nullable = false, updatable = false, length = 1))
    private DbEvent dbEvent;

    //the camel route where this event  couldn't be processed, null is interpreted as all routes
    @Column(updatable = false, length = 50)
    private String route;

    @Column(updatable = false, length = 1024)
    private String message;

    @Column(name = "cause_message", updatable = false, length = 1024)
    private String causeMessage;

    /**
     * Gets the dbEvent
     *
     * @return the dbEvent
     */
    public DbEvent getDbEvent() {
        return dbEvent;
    }

    /**
     * Sets the dbEvent
     *
     * @param dbEvent the dbEvent to set
     */
    public void setDbEvent(DbEvent dbEvent) {
        this.dbEvent = dbEvent;
    }

    /**
     * Gets the route
     *
     * @return the route
     */
    public String getRoute() {
        return route;
    }

    /**
     * Sets the route
     *
     * @param route the route to set
     */
    public void setRoute(String route) {
        this.route = route;
    }

    /**
     * Gets the message
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message
     *
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the causeMessage
     *
     * @return the causeMessage
     */
    public String getCauseMessage() {
        return causeMessage;
    }

    /**
     * Sets the causeMessage
     *
     * @param causeMessage the causeMessage to set
     */
    public void setCauseMessage(String causeMessage) {
        this.causeMessage = causeMessage;
    }

    @Override
    public String toString() {
        return "route: " + getRoute() + ", " + getDbEvent();
    }

}
