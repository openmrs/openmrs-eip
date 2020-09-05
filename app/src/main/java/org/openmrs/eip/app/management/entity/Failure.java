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

    //the camel route where this event  couldn't be processed
    @Column(nullable = false, updatable = false, length = 50)
    private String route;

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

    @Override
    public String toString() {
        return "route: " + getRoute() + ", " + getDbEvent();
    }

}
