# Release Notes - DB Sync Version 1.6.0

<h2>        New Feature
</h2>
<ul>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-620'>EC-620</a>] -         Remote sites should periodically reconcile with central
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-621'>EC-621</a>] -         Receiver should process reconciliation data from remote sites
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-631'>EC-631</a>] -         Generate reconciliation report in receiver
</li>
</ul>

<h2>        Task
</h2>
<ul>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-602'>EC-602</a>] -         Verify that DB sync works with latest version of ArtemisMQ
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-603'>EC-603</a>] -         Verify that DB sync works with MySQL8
</li>
</ul>

<h2>        Bug
</h2>
<ul>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-611'>EC-611</a>] -         Add not null constraint to receiver_sync_msg.date_created column
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-612'>EC-612</a>] -         Add not null constraint to debezium_event_queue.date_created column
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-613'>EC-613</a>] -         Add not null constraint to sender_retry_queue.date_created column
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-619'>EC-619</a>] -         Delete debezium event and retry if the entity is not found in the database
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-627'>EC-627</a>] -         Block the generation of a new certificate in DBSYNC while there is a valid one installed
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-632'>EC-632</a>] -         Fix occassional error during initial loading
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-635'>EC-635</a>] -         Errors are randomly thrown when clearing db cache
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-636'>EC-636</a>] -         Errors are randomly thrown when updating search index
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-637'>EC-637</a>] -         Errors are randomly thrown when processing a sync message
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-638'>EC-638</a>] -         Errors are randomly thrown when updating  last sync date
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-640'>EC-640</a>] -         Errors are randomly thrown when updating column value
</li>
</ul>

<h2>        Improvement
</h2>
<ul>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-518'>EC-518</a>] -         Add not null constraint to receiver tables with site_id column
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-527'>EC-527</a>] -         Make sender move operations between queue atomic
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-528'>EC-528</a>] -         Make retry to conflict queue move operation atomic
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-529'>EC-529</a>] -         Make sync to conflict queue move operation atomic
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-530'>EC-530</a>] -         Make sync to error queue move operation atomic
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-549'>EC-549</a>] -         Display archives with latest first
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-597'>EC-597</a>] -         Upgrade dependency versions
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-599'>EC-599</a>] -         Upgrade to Java 17
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-601'>EC-601</a>] -         Support read operation for debezium events
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-606'>EC-606</a>] -         Split sender and receiver liquibase log files into schema and update
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-609'>EC-609</a>] -         Make DB sync logs less verbose
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-610'>EC-610</a>] -         Reduce the default pool size on remote dbsync
</li>
<li>[<a href='https://jira.fgh.org.mz/browse/EC-614'>EC-614</a>] -         Add not null constraint to receiver tables with message_uuid column
</li>
</ul>
