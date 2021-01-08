package org.openmrs.eip.component.model;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class GaacFamilyMemberModel extends BaseChangeableDataModel {
    private String familyUuid;
    private String memberUuid;
    private Boolean leaving;
    private Boolean restart;
    protected LocalDateTime restartDate;
    private Integer reasonLeavingType;
    private String description;
    protected LocalDateTime startDate;
    protected LocalDateTime endDate;
    private String familyMemberRelacaoUuid;
	
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
	public Integer getReasonLeavingType() {
		return reasonLeavingType;
	}
	
	public void setReasonLeavingType(Integer reasonLeavingType) {
		this.reasonLeavingType = reasonLeavingType;
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
	public String getFamilyMemberRelacaoUuid() {
		return familyMemberRelacaoUuid;
	}
	public void setFamilyMemberRelacaoUuid(String familyMemberRelacaoUuid) {
		this.familyMemberRelacaoUuid = familyMemberRelacaoUuid;
	}
	public String getFamilyUuid() {
		return familyUuid;
	}
	public void setFamilyUuid(String familyUuid) {
		this.familyUuid = familyUuid;
	} 
}