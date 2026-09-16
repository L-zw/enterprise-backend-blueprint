package com.lzw.blueprint.workflow.config;

import org.flowable.engine.ProcessEngine;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlowableEngineTest {

    private ProcessEngine engine;

    @AfterEach
    void tearDown() {
        if (engine != null) {
            engine.close();
        }
    }

    @Test
    void processEngineBootsAndCreatesActTables() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:flowable;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");

        engine = new FlowableConfig().processEngine(dataSource, new DataSourceTransactionManager(dataSource));

        assertFalse(engine.getName().isEmpty());
        assertNotNull(engine.getRepositoryService());
        assertNotNull(engine.getRuntimeService());
        assertNotNull(engine.getHistoryService());

        List<String> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME LIKE 'ACT_%'")) {
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }

        assertTrue(tables.size() >= 25, "ACT_* tables created: " + tables.size());
        assertTrue(tables.contains("ACT_GE_PROPERTY"));
        assertTrue(tables.contains("ACT_RE_PROCDEF"));
        assertTrue(tables.contains("ACT_RU_EXECUTION"));
        assertTrue(tables.contains("ACT_HI_PROCINST"));
        assertTrue(tables.contains("ACT_HI_TASKINST"));
    }
}
