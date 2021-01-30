package org.openmrs.eip.app.publisher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataLoader {
    final static String insert = "INSERT INTO visit (patient_id, visit_type_id, date_started, voided, creator, date_created, uuid) " +
            "VALUES(7, 1, now(), 0, 1, now(), uuid()";

    public static void main(String[] args) throws Exception {
        final String url = "jdbc:mysql://localhost:3307/openmrs";

        ExecutorService executor = Executors.newFixedThreadPool(8);
        final int count = 8000000;
        List<CompletableFuture> futures = new ArrayList(count);
        int counter = 0;
        Connection c = DriverManager.getConnection(url, "openmrs", "openmrs");
        try {
            while (counter < count) {
                counter++;
                futures.add(CompletableFuture.runAsync(new MyLoader(c), executor));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();

            System.out.println("Done...");
        } finally {
            c.close();
        }
    }

    private static class MyLoader implements Runnable {
        private final Connection c;

        MyLoader(Connection c) {
            this.c = c;
        }

        @Override
        public void run() {
            try (Statement s = c.createStatement()) {
                s.execute(insert);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
