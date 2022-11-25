package org.openmrs.eip.app.sender;

import com.github.shyiko.mysql.binlog.event.Event;

import io.debezium.connector.mysql.BinlogReader;

public class TestBinLogClient extends BaseBinlogClient {
	
	public TestBinLogClient() {
		this(null);
	}
	
	public TestBinLogClient(BinlogReader.BinlogPosition binlogPosition) {
		super(binlogPosition);
	}
	
	@Override
	public void onEvent(Event event) {
	}
	
}
