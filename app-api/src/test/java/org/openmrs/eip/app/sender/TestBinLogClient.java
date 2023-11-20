package org.openmrs.eip.app.sender;

import com.github.shyiko.mysql.binlog.event.Event;

import io.debezium.connector.mysql.MySqlStreamingChangeEventSource.BinlogPosition;

public class TestBinLogClient extends BaseBinlogClient {
	
	public TestBinLogClient() {
		this(null);
	}
	
	public TestBinLogClient(BinlogPosition binlogPosition) {
		super(binlogPosition);
	}
	
	@Override
	public void onEvent(Event event) {
	}
	
}
