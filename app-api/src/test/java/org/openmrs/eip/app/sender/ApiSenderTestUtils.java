package org.openmrs.eip.app.sender;

import static java.util.Collections.singletonList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.debezium.DebeziumConstants;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.support.DefaultMessage;
import org.apache.kafka.connect.data.ConnectSchema;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;

public class ApiSenderTestUtils {
	
	public static Exchange createExchange(int index, String snapshot, String table) {
		Exchange exchange = new DefaultExchange(new DefaultCamelContext());
		Message message = new DefaultMessage(exchange);
		exchange.setMessage(message);
		final Field id = new Field(table + "_id", 0, new ConnectSchema(Schema.Type.INT32));
		final List<Field> ids = singletonList(id);
		final Struct primaryKey = new Struct(
		        new ConnectSchema(Schema.Type.STRUCT, false, null, "key", null, null, null, ids, null, null));
		primaryKey.put(table + "_id", index);
		message.setHeader(DebeziumConstants.HEADER_KEY, primaryKey);
		Map<String, Object> sourceMetadata = new HashMap();
		sourceMetadata.put("table", table);
		sourceMetadata.put("snapshot", snapshot);
		message.setHeader(DebeziumConstants.HEADER_SOURCE_METADATA, sourceMetadata);
		return exchange;
	}
	
}
