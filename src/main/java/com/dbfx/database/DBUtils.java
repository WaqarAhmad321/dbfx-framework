package com.dbfx.database;

import java.sql.*;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class DBUtils {
    public static <T> T executeQuery(String sql,
                                     SQLConsumer<PreparedStatement> preparer,
                                     ResultSetMapper<T> mapper) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fill in the ? marks in the SQL
            if (preparer != null) {
                preparer.accept(stmt);
            }

            // Turn database rows into Java objects
            try (ResultSet rs = stmt.executeQuery()) {
                return mapper.map(rs);
            }
        }
    }

    // For INSERT/UPDATE/DELETE:
    // Same as above but returns true if the query changed any data
    public static boolean executeUpdate(String sql,
                                        SQLConsumer<PreparedStatement> preparer) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            preparer.accept(stmt);
            return stmt.executeUpdate() > 0;
        }
    }

    public static <T> Task<T> executeQueryAsync(
            String sql,
            SQLConsumer<PreparedStatement> preparer,
            ResultSetMapper<T> mapper,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError) {

        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return executeQuery(sql, preparer, mapper);
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> onSuccess.accept(task.getValue())));
        task.setOnFailed(e -> Platform.runLater(() -> onError.accept(task.getException())));

        new Thread(task).start();
        return task;
    }

    public static Task<Boolean> executeUpdateAsync(
            String sql,
            SQLConsumer<PreparedStatement> preparer,
            Consumer<Boolean> onSuccess,
            Consumer<Throwable> onError) {

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return executeUpdate(sql, preparer);
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> onSuccess.accept(task.getValue())));
        task.setOnFailed(e -> Platform.runLater(() -> onError.accept(task.getException())));

        new Thread(task).start();
        return task;
    }

    // Like Consumer but can throw SQLExceptions.
    // Use to fill in query parameters.
    @FunctionalInterface
    public interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }

    // convert database rows into Java objects
    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
