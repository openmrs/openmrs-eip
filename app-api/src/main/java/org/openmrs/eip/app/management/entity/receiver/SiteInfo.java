package org.openmrs.eip.app.management.entity.receiver;

import org.openmrs.eip.app.management.entity.AbstractEntity;

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
	
	@Column(name = "sync_disabled", nullable = false)
	private Boolean disabled;
	
	@Column(name = "site_district", nullable = false, unique = true)
	private String siteDistrict;
	
	@Column(name = "site_instance_name", nullable = false, unique = true)
	private String siteInstanceName;
	
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
	
	/**
	 * Gets the disabled
	 *
	 * @return the disabled
	 */
	public Boolean getDisabled() {
		return disabled;
	}
	
	/**
	 * Sets the disabled
	 *
	 * @param disabled the disabled to set
	 */
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	
	public String getSiteDistrict() {
		return siteDistrict;
	}
	
	public void setSiteDistrict(String siteDistrict) {
		this.siteDistrict = siteDistrict;
	}
	
	public String getSiteInstanceName() {
		return siteInstanceName;
	}
	
	public void setSiteInstanceName(String siteInstanceName) {
		this.siteInstanceName = siteInstanceName;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
