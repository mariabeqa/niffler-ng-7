package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDAO;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;


public class AuthUserDAOJdbc implements AuthUserDAO {

    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final Connection connection;

    public AuthUserDAOJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthUserEntity createUser(AuthUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, ENCODER.encode(user.getPassword()));
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find user id in ResultSet");
                }
            }

            user.setId(generatedKey);
            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity uae = new AuthUserEntity();
                    uae.setId(rs.getObject("id", UUID.class));
                    uae.setUsername(rs.getString("username"));
                    uae.setPassword(ENCODER.encode(rs.getString("password")));
                    uae.setEnabled(rs.getBoolean("enabled"));
                    uae.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    uae.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    uae.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));

                    return Optional.of(uae);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
