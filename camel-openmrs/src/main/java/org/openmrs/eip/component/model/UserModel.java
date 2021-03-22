package org.openmrs.eip.component.model;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class UserModel extends BaseChangeableMetadataModel {
	
	private String systemId;
	private String username;
	private String password;
	private String salt;
	private String secretQuestion;
	private boolean retired;
	private String retireReason;
	private LocalDateTime dateRetired;
	private Long retiredBy;
	private String personUuid;
    private String activationKey;
    private String email;
    
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getSecretQuestion() {
		return secretQuestion;
	}
	public void setSecretQuestion(String secretQuestion) {
		this.secretQuestion = secretQuestion;
	}
	public boolean isRetired() {
		return retired;
	}
	public void setRetired(boolean retired) {
		this.retired = retired;
	}
	public String getRetireReason() {
		return retireReason;
	}
	public void setRetireReason(String retireReason) {
		this.retireReason = retireReason;
	}
	public LocalDateTime getDateRetired() {
		return dateRetired;
	}
	public void setDateRetired(LocalDateTime dateRetired) {
		this.dateRetired = dateRetired;
	}
	public Long getRetiredBy() {
		return retiredBy;
	}
	public void setRetiredBy(Long retiredBy) {
		this.retiredBy = retiredBy;
	}
	public String getPersonUuid() {
		return personUuid;
	}
	public void setPersonUuid(String personUuid) {
		this.personUuid = personUuid;
	}
	public String getActivationKey() {
		return activationKey;
	}
	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
