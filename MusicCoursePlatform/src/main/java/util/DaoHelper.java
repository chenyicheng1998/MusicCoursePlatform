package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Reusable JDBC helper methods for DAO classes.
 * Eliminates repetitive try-with-resources and exception-handling boilerplate.
 */
public final class DaoHelper {

    private static final Logger logger = LoggerFactory.getLogger(DaoHelper.class);

    private DaoHelper() {
    }

    @FunctionalInterface
    public interface StatementPreparer {
        void prepare(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    /**
     * Execute a SELECT expected to return at most one row.
     * Returns null if no row found or on error.
     */
    public static <T> T querySingle(String sql, StatementPreparer preparer, ResultSetMapper<T> mapper) {
        try (Connection conn = DatabaseConnection.getNewConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            preparer.prepare(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.map(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Query error: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Execute a SELECT that returns multiple rows.
     * Pass {@code stmt -> {}} as preparer for queries with no parameters.
     */
    public static <T> List<T> queryList(String sql, StatementPreparer preparer, ResultSetMapper<T> mapper) {
        List<T> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getNewConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            preparer.prepare(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Query error: {}", e.getMessage(), e);
        }
        return results;
    }

    /**
     * Execute an UPDATE or DELETE statement.
     * Returns true if at least one row was affected.
     */
    public static boolean executeUpdate(String sql, StatementPreparer preparer) {
        try (Connection conn = DatabaseConnection.getNewConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            preparer.prepare(stmt);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Update error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Execute an INSERT and pass the generated key to {@code keyConsumer}.
     * Returns true if the row was inserted.
     */
    public static boolean executeInsert(String sql, StatementPreparer preparer, IntConsumer keyConsumer) {
        try (Connection conn = DatabaseConnection.getNewConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparer.prepare(stmt);
            if (stmt.executeUpdate() > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next() && keyConsumer != null) {
                        keyConsumer.accept(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            logger.error("Insert error: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * Read a nullable DATE column and return its {@link java.time.LocalDate} value,
     * or {@code null} if the column value is SQL NULL.
     *
     * <p>Eliminates the repetitive null-check pattern that would otherwise appear
     * in every DAO mapper method that reads {@code created_at} / {@code updated_at}.</p>
     */
    public static java.time.LocalDate readLocalDate(ResultSet rs, String column) throws SQLException {
        Date d = rs.getDate(column);
        return d != null ? d.toLocalDate() : null;
    }

    /**
     * Normalize an instrument string to a lowercase canonical DB key.
     * E.g. "Piano", "PIANO", "piano, guitar" all become "piano".
     */
    public static String normalizeInstrumentKey(String raw) {
        if (raw == null || raw.isBlank()) {
            return "piano";
        }
        return raw.split(",")[0].trim().toLowerCase();
    }
}
