package com.interface21.jdbc.datasource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import com.interface21.jdbc.CannotGetJdbcConnectionException;
import com.interface21.transaction.support.TransactionSynchronizationManager;

public abstract class DataSourceUtils {

    private DataSourceUtils() {
    }

    public static Connection getConnection(DataSource dataSource) throws CannotGetJdbcConnectionException {
        Connection connection = TransactionSynchronizationManager.getResource(dataSource);
        if (connection != null) {
            return connection;
        }

        try {
            connection = dataSource.getConnection();
            TransactionSynchronizationManager.bindResource(dataSource, connection);
            return connection;
        } catch (SQLException ex) {
            throw new CannotGetJdbcConnectionException("Failed to obtain JDBC Connection", ex);
        }
    }

    public static void releaseConnection(Connection connection, DataSource dataSource) {
        try {
            connection.close();
            TransactionSynchronizationManager.unbindResource(dataSource);
        } catch (SQLException ex) {
            throw new CannotGetJdbcConnectionException("Failed to close JDBC Connection");
        }
    }

    public static boolean isConnectionTransactional(Connection connection, DataSource dataSource) {
        if (connection == null) {
            return false;
        }

        Connection con = TransactionSynchronizationManager.getResource(dataSource);

        return connection.equals(con);
    }
}
