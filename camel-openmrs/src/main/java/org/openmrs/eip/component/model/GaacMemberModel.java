package org.openmrs.eip.component.model;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class GaacMemberModel extends BaseChangeableDataModel {
	
	private String gaacUuid;
	
	private String memberUuid;
	
	private Boolean leaving;
	
	private Boolean restart;
	
	protected LocalDateTime restartDate;
	
	private String reasonLeavingTypeUuid;
	
	private String description;
	
	protected LocalDateTime startDate;
	
	protected LocalDateTime endDate;
	
	public String getGaacUuid() {
		return gaacUuid;
	}
	
	public void setGaacUuid(String gaacUuid) {
		this.gaacUuid = gaacUuid;
	}
	
	public String getMemberUuid() {
		return memberUuid;
	}
	
	public void setMemberUuid(String memberUuid) {
		this.memberUuid = memberUuid;
	}
	
	public Boolean getLeaving() {
		return leaving;
	}
	
	public void setLeaving(Boolean leaving) {
		this.leaving = leaving;
	}
	
	public Boolean getRestart() {
		return restart;
	}
	
	public void setRestart(Boolean restart) {
		this.restart = restart;
	}
	
	public LocalDateTime getRestartDate() {
		return restartDate;
	}
	
	public void setRestartDate(LocalDateTime restartDate) {
		this.restartDate = restartDate;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public LocalDateTime getStartDate() {
		return startDate;
	}
	
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}
	
	public LocalDateTime getEndDate() {
		return endDate;
	}
	
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}
	
	public String getReasonLeavingTypeUuid() {
		return reasonLeavingTypeUuid;
	}
	
	public void setReasonLeavingTypeUuid(String reasonLeavingTypeUuid) {
		this.reasonLeavingTypeUuid = reasonLeavingTypeUuid;
	}
}
