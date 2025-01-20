package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDAO;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDAOSpringJdbc implements AuthUserDAO {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getPassword());
                    ps.setBoolean(3, user.getEnabled());
                    ps.setBoolean(4, user.getAccountNonExpired());
                    ps.setBoolean(5, user.getAccountNonLocked());
                    ps.setBoolean(6, user.getCredentialsNonExpired());
                    return ps;
                },
                kh
        );

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);

        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        AuthUserEntityRowMapper.INSTANSE,
                        id
                )
        );
    }
}
