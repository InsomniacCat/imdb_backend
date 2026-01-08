package com.imdb.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

// @Component
public class SchemaDebugger implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("====== DATABASE SCHEMA DEBUG ======");
        try {
            // Check title_basics columns
            printTableColumns("title_basics");
            // Check name_basics columns
            printTableColumns("name_basics");
            // Check title_ratings
            printTableColumns("title_ratings");
            // Check title_principals
            printTableColumns("title_principals");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("==================================");
    }

    private void printTableColumns(String tableName) {
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT column_name FROM information_schema.columns WHERE table_name = '" + tableName + "'");

        System.out.println("Columns in " + tableName + ":");
        for (Map<String, Object> col : columns) {
            System.out.println(" - " + col.get("column_name"));
        }
    }
}
