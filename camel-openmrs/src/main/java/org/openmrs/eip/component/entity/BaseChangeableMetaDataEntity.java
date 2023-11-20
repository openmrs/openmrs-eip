package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.utils.DateUtils;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseChangeableMetaDataEntity extends BaseMetaDataEntity {
	
	@ManyToOne
	@JoinColumn(name = "changed_by")
	private UserLight changedBy;
	
	@Column(name = "date_changed")
	private LocalDateTime dateChanged;
	
	@Override
	public boolean wasModifiedAfter(BaseEntity entity) {
		BaseChangeableMetaDataEntity other = (BaseChangeableMetaDataEntity) entity;
		List<LocalDateTime> dates = Arrays.asList(getDateChanged(), getDateRetired());
		List<LocalDateTime> otherDates = Arrays.asList(other.getDateChanged(), other.getDateRetired());
		return DateUtils.containsLatestDate(dates, otherDates);
	}
	
	/**
	 * Gets the changedBy
	 *
	 * @return the changedBy
	 */
	public UserLight getChangedBy() {
		return changedBy;
	}
	
	/**
	 * Sets the changedBy
	 *
	 * @param changedBy the changedBy to set
	 */
	public void setChangedBy(UserLight changedBy) {
		this.changedBy = changedBy;
	}
	
	/**
	 * Gets the dateChanged
	 *
	 * @return the dateChanged
	 */
	public LocalDateTime getDateChanged() {
		return dateChanged;
	}
	
	/**
	 * Sets the dateChanged
	 *
	 * @param dateChanged the dateChanged to set
	 */
	public void setDateChanged(LocalDateTime dateChanged) {
		this.dateChanged = dateChanged;
	}
	
}
