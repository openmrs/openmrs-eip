package org.openmrs.eip.app.sender;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

public class ReconcileSnapshot {
	
	@Getter
	private List<TableSnapshot> tableSnapshots;
	
	public ReconcileSnapshot(List<TableSnapshot> tableSnapshots) {
		this.tableSnapshots = tableSnapshots;
	}
	
	public static class TableSnapshot {
		
		@Getter
		private String tableName;
		
		@Getter
		private long rowCount;
		
		@Getter
		private long startId;
		
		@Getter
		private long endId;
		
		@Getter
		private LocalDateTime dateTaken;
		
		public TableSnapshot(String tableName, long rowCount, long startId, long endId, LocalDateTime dateTaken) {
			this.tableName = tableName;
			this.rowCount = rowCount;
			this.startId = startId;
			this.endId = endId;
			this.dateTaken = dateTaken;
		}
		
	}
	
}
