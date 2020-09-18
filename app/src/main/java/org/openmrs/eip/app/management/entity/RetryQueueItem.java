package org.openmrs.eip.app.management.entity;

import org.openmrs.eip.component.entity.Event;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "retry_queue")
public class RetryQueueItem extends AbstractEntity {

    @Embedded
    @AttributeOverride(name = "identifier", column = @Column(updatable = false))
    @AttributeOverride(name = "primaryKeyId", column = @Column(name = "primary_key_id", nullable = false, updatable = false))
    @AttributeOverride(name = "tableName", column = @Column(name = "table_name", nullable = false, updatable = false, length = 100))
    @AttributeOverride(name = "operation", column = @Column(nullable = false, updatable = false, length = 1))
    @AttributeOverride(name = "snapshot", column = @Column(nullable = false, updatable = false))
    private Event event;

    //the camel route where this event  couldn't be processed, null is interpreted as all routes
    @Column(updatable = false, length = 50)
    private String route;

    @Column(updatable = false, length = 1024)
    private String message;

    @Column(name = "cause_message", updatable = false, length = 1024)
    private String causeMessage;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 1;

    @Column(name = "date_Changed")
    private Date dateChanged;

    /**
     * Gets the event
     *
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * Sets the event
     *
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
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

    /**
     * Gets the attemptCount
     *
     * @return the attemptCount
     */
    public Integer getAttemptCount() {
        return attemptCount;
    }

    /**
     * Sets the attemptCount
     *
     * @param attemptCount the attemptCount to set
     */
    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    /**
     * Gets the dateChanged
     *
     * @return the dateChanged
     */
    public Date getDateChanged() {
        return dateChanged;
    }

    /**
     * Sets the dateChanged
     *
     * @param dateChanged the dateChanged to set
     */
    public void setDateChanged(Date dateChanged) {
        this.dateChanged = dateChanged;
    }

    @Override
    public String toString() {
        return "RetryQueueItem {route=" + getRoute() + ", event=" + getEvent() + "}";
    }

}
