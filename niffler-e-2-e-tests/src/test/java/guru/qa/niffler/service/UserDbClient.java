package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDAO;
import guru.qa.niffler.data.dao.AuthUserDAO;
import guru.qa.niffler.data.dao.UDUserDAO;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Optional;

import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;

public class UserDbClient {

    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private static final Config CFG = Config.getInstance();

    private final AuthUserDAO authUserDAO = new AuthUserDAOJdbc();
    private final AuthAuthorityDAO authAuthorityDAO = new AuthAuthorityDAOJdbc();
    private final UDUserDAO userDAO = new UDUserDAOSpringJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson createUserSpringJdbc(UserJson user) {
        return  xaTxTemplate.execute(() -> {
            AuthUserEntity aue = new AuthUserEntity();
            aue.setUsername(user.username());
            aue.setPassword(ENCODER.encode("12345"));
            aue.setEnabled(true);
            aue.setAccountNonLocked(true);
            aue.setAccountNonExpired(true);
            aue.setCredentialsNonExpired(true);

            AuthUserEntity createdAuthUser = authUserDAO.createUser(aue);

            AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                    e -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setId(createdAuthUser.getId());
                        ae.setAuthority(e);
                        return ae;
                    }
            ).toArray(AuthorityEntity[]::new);

            authAuthorityDAO.createAuthorities(authorityEntities);
            return UserJson.fromEntity(userDAO.createUser(UserEntity.fromJson(user)), null);
        });
    }

//    public UserJson createUser(UserJson userJson) {
//        AuthUserEntity aue = new AuthUserEntity();
//        aue.setUsername(userJson.username());
//        aue.setPassword(ENCODER.encode("12345"));
//        aue.setEnabled(true);
//        aue.setAccountNonLocked(true);
//        aue.setAccountNonExpired(true);
//        aue.setCredentialsNonExpired(true);
//
//        XAFunction<UserJson> xaAuthF = new XAFunction<>(
//                connection -> {
//                    //1 - создаем запись в табл user, niffler-auth
//                    AuthUserEntity createdAuthUser = new AuthUserDAOJdbc(connection).createUser(aue);
//
//                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
//                            e -> {
//                                AuthorityEntity ae = new AuthorityEntity();
//                                ae.setUserId(createdAuthUser.getId());
//                                ae.setAuthority(e);
//                                return ae;
//                            }
//                    ).toArray(AuthorityEntity[]::new);
//
//                    //2 - создаем 2 записи read и write в табл authorities, niffler-auth
//                    new AuthAuthorityDAOJdbc(connection).createAuthorities(authorityEntities);
//                    return UserJson.fromAuthEntity(createdAuthUser);
//                },
//                CFG.authJdbcUrl());
//
//        XAFunction<UserJson> xaUserDataF = new XAFunction<>(connection -> {
//            //3- создаем запись в табл user, niffler-userdata
//            UserEntity ue = new UDUserDAOJdbc(connection).createUser(UserEntity.fromJson(userJson));
//            return UserJson.fromEntity(ue, null);
//        },
//                CFG.userdataJdbcUrl());
//
//        return xaTransaction(TRANSACTION_READ_UNCOMMITTED, xaAuthF, xaUserDataF);
//    }
//
//    public Optional<UserEntity> findUserByUsername(String username) {
//
//        return transaction(TRANSACTION_READ_UNCOMMITTED, connection -> {
//                    Optional<UserEntity> user = new UDUserDAOJdbc(connection)
//                            .findByUsername(username);
//                    return user;
//                },
//                CFG.userdataJdbcUrl());
//
//    }

}