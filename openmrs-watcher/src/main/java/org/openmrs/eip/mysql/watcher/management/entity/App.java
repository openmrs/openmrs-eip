package org.openmrs.eip.mysql.watcher.management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

import org.openmrs.eip.app.management.entity.AbstractEntity;

//@Entity
@Table(name = "sender_app")
public class App extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	@Column(nullable = false, unique = true, length = 50)
	private String name;
	
	/**
	 * Gets the name
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name
	 *
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
