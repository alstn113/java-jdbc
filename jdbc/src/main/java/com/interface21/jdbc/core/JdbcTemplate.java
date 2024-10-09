package com.interface21.jdbc.core;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.interface21.dao.DataAccessException;

public class JdbcTemplate {

    private final DataSource dataSource;

    public JdbcTemplate(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> query(String sql, @Nullable PreparedStatementSetter pss, RowMapper<T> rowMapper) {
        return execute(sql, pstmt -> {
            List<T> results = new ArrayList<>();
            if (pss != null) {
                pss.setValues(pstmt);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs));
                }
            }
            return results;
        });
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) {
        return query(sql, new SimplePreparedStatementSetter(args), rowMapper);
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper) {
        return query(sql, null, rowMapper);
    }

    public <T> T queryForObject(String sql, @Nullable PreparedStatementSetter pss, RowMapper<T> rowMapper) {
        List<T> results = query(sql, pss, rowMapper);
        if (results.isEmpty()) {
            throw new DataAccessException("Empty Result");
        }
        if (results.size() > 1) {
            throw new DataAccessException("Incorrect Result Size");
        }
        return results.getFirst();
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        return queryForObject(sql, new SimplePreparedStatementSetter(args), rowMapper);
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) {
        return queryForObject(sql, null, rowMapper);
    }

    public int update(String sql, PreparedStatementSetter pss) {
        return execute(sql, pstmt -> {
            if (pss != null) {
                pss.setValues(pstmt);
            }
            return pstmt.executeUpdate();
        });
    }

    public int update(String sql, Object... args) {
        return update(sql, new SimplePreparedStatementSetter(args));
    }

    private <T> T execute(String sql, PreparedStatementCallback<T> callback) throws DataAccessException {
        try (Connection con = dataSource.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            return callback.doInStatement(pstmt);
        } catch (SQLException e) {
            throw new DataAccessException("execute error", e);
        }
    }
}
