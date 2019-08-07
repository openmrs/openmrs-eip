package org.openmrs.sync.app.camel;

import org.apache.camel.processor.idempotent.FileIdempotentRepository;
import org.openmrs.sync.app.SyncProfiles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;

@Profile(SyncProfiles.SENDER)
@Component("fileSyncIdempotentRepository")
public class FileSyncIdempotentRepository extends FileIdempotentRepository {

    private static final String FILE_NAME = "/store";

    public FileSyncIdempotentRepository(@Value("${camel.output.endpoint.file.location}") final String path) {
        super(new File(path + FILE_NAME), new HashMap<>());
    }
}
