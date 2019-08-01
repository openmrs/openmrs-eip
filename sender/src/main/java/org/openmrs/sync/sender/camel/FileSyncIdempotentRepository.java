package org.openmrs.sync.sender.camel;

import org.apache.camel.processor.idempotent.FileIdempotentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;

@Component("fileSyncIdempotentRepository")
public class FileSyncIdempotentRepository extends FileIdempotentRepository {

    private static final String FILE_NAME = "/store";

    public FileSyncIdempotentRepository(@Value("${camel.output.endpoint.file.location}") final String path) {
        super(new File(path + FILE_NAME), new HashMap<>());
    }
}
