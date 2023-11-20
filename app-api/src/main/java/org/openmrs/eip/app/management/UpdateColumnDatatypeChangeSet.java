package org.openmrs.eip.app.management;

import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Table;

import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

public class UpdateColumnDatatypeChangeSet implements CustomTaskChange {
	
	private static final Logger log = LoggerFactory.getLogger(UpdateColumnDatatypeChangeSet.class);
	
	@Override
	public void execute(Database database) throws CustomChangeException {
		log.info("Running " + getClass().getSimpleName());
		
		try {
			JdbcConnection conn = (JdbcConnection) database.getConnection();
			for (String tableName : getHashTableNames()) {
				updateColumnDataTypes(tableName, conn);
			}
		}
		catch (Exception e) {
			throw new CustomChangeException(
			        "Failed to update data types for the date_created and date_changed columns in the tables used to "
			                + "store hashes",
			        e);
		}
	}
	
	private void updateColumnDataTypes(String table, JdbcConnection conn) throws Exception {
		try (Statement s = conn.createStatement()) {
			log.info("Changing " + table + ".date_created column datatype to DATETIME(3)");
			
			s.executeUpdate("ALTER TABLE " + table + " CHANGE COLUMN date_created date_created DATETIME(3) NOT NULL");
			
			log.info("Changing " + table + ".date_changed column datatype to DATETIME(3)");
			
			s.executeUpdate(
			    "ALTER TABLE " + table + " CHANGE COLUMN date_changed date_changed DATETIME(3) NULL DEFAULT NULL");
		}
	}
	
	protected Set<String> getHashTableNames() throws Exception {
		PathMatchingResourcePatternResolver scanner = new PathMatchingResourcePatternResolver();
		String locationPattern = "classpath:" + BaseHashEntity.class.getPackage().getName().replace(".", "/")
		        + "/*Hash.class";
		
		log.info("Loading all subclasses of " + BaseHashEntity.class.getName() + " matching the location pattern "
		        + locationPattern);
		
		Resource[] resources = scanner.getResources(locationPattern);
		TypeFilter filter = new AssignableTypeFilter(BaseHashEntity.class);
		final MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory();
		Set<String> tableNames = new HashSet();
		for (Resource resource : resources) {
			final MetadataReader reader = readerFactory.getMetadataReader(resource);
			if (filter.match(reader, readerFactory)) {
				tableNames.add(
				    reader.getAnnotationMetadata().getAnnotationAttributes(Table.class.getName()).get("name").toString());
			}
		}
		
		return tableNames;
	}
	
	@Override
	public String getConfirmationMessage() {
		return getClass().getSimpleName() + " completed successfully";
	}
	
	@Override
	public void setUp() throws SetupException {
		
	}
	
	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
	}
	
	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}
	
}
