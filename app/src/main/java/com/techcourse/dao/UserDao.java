package com.techcourse.dao;

import javax.sql.DataSource;
import java.util.List;
import com.interface21.jdbc.core.JdbcTemplate;
import com.interface21.jdbc.core.RowMapper;
import com.techcourse.domain.User;

public class UserDao {

    public static final RowMapper<User> USER_ROW_MAPPER = rs -> new User(
            rs.getLong("id"),
            rs.getString("account"),
            rs.getString("password"),
            rs.getString("email")
    );

    private final JdbcTemplate jdbcTemplate;

    public UserDao(DataSource dataSource) {
        this(new JdbcTemplate(dataSource));
    }

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(User user) {
        String sql = "insert into users (account, password, email) values (?, ?, ?)";

        jdbcTemplate.update(sql, user.getAccount(), user.getPassword(), user.getEmail());
    }

    public void update(User user) {
        String sql = "update users set account = ?, password = ?, email = ? where id = ?";

        jdbcTemplate.update(sql, user.getAccount(), user.getPassword(), user.getEmail(), user.getId());
    }

    public List<User> findAll() {
        String sql = "select id, account, password, email from users";

        return jdbcTemplate.query(sql, USER_ROW_MAPPER);
    }

    public User findById(Long id) {
        String sql = "select id, account, password, email from users where id = ?";

        return jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, id);
    }

    public User findByAccount(String account) {
        String sql = "select id, account, password, email from users where account = ?";

        return jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, account);
    }
}
