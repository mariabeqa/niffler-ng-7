package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UDUserDAO;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

public class UDUserDAOSpringJdbc implements UDUserDAO {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity createUser(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?)",
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getCurrency().name());
                    ps.setString(3, user.getFirstname());
                    ps.setString(4, user.getSurname());
                    ps.setBytes(5, user.getPhoto());
                    ps.setBytes(6, user.getPhotoSmall());
                    ps.setString(7, user.getFullname());
                    return ps;
                },
                kh
        );

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);

        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        UdUserEntityRowMapper.INSTANCE,
                        id
                )
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE username = ?",
                        UdUserEntityRowMapper.INSTANCE,
                        username
                )
        );
    }

    @Override
    public void delete(UserEntity user) {

    }
}
