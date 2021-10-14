package org.openmrs.eip.app.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "site_info")
public class SiteInfo extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	@Column(nullable = false, unique = true)
	private String name;
	
	@Column(nullable = false, unique = true)
	private String identifier;
	
	private String siteDistrict;
	
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
	
	/**
	 * Gets the identifier
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Sets the identifier
	 *
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getSiteDistrict() {
		return siteDistrict;
	}

	public void setSiteDistrict(String siteDistrict) {
		this.siteDistrict = siteDistrict;
	}

	@Override
	public String toString() {
		return getName();
	}
	
}
