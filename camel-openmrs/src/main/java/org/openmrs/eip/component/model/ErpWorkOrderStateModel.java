package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ErpWorkOrderStateModel extends BaseDataModel {
	
	private String action;
	
	private String erpWorkOrderUuid;
	
	/**
	 * Gets the action
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	
	/**
	 * Sets the action
	 *
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Gets the erpWorkOrderUuid
	 *
	 * @return the erpWorkOrderUuid
	 */
	public String getErpWorkOrderUuid() {
		return erpWorkOrderUuid;
	}
	
	/**
	 * Sets the erpWorkOrderUuid
	 *
	 * @param erpWorkOrderUuid the erpWorkOrderUuid to set
	 */
	public void setErpWorkOrderUuid(String erpWorkOrderUuid) {
		this.erpWorkOrderUuid = erpWorkOrderUuid;
	}
}
