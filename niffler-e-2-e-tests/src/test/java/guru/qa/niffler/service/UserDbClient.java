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
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Optional;

import static guru.qa.niffler.data.tpl.DataSources.testDataSource;

public class UserDbClient {

    private static final PasswordEncoder ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private static final Config CFG = Config.getInstance();

    private final AuthUserDAO authUserDAO = new AuthUserDAOJdbc();
    private final AuthAuthorityDAO authAuthorityDAO = new AuthAuthorityDAOJdbc();
    private final UDUserDAO userDAO = new UDUserDAOJdbc();

    private final AuthUserDAO authUserSpringDAO = new AuthUserDAOSpringJdbc();
    private final AuthAuthorityDAO authAuthoritySpringDAO = new AuthAuthorityDAOSpringJdbc();
    private final UDUserDAO userSpringDAO = new UDUserDAOSpringJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    TransactionTemplate txTemplateWithChainedTxManager = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            testDataSource(CFG.authJdbcUrl())
                    ),
                    new JdbcTransactionManager(
                            testDataSource(CFG.userdataJdbcUrl())
                    )
            )
    );

    public UserJson createUserJdbcWithTx(UserJson user) {
        return xaTxTemplate.execute(() -> {
            AuthUserEntity aue = new AuthUserEntity();
            aue.setUsername(user.username());
            aue.setPassword(ENCODER.encode("12345"));
            aue.setEnabled(true);
            aue.setAccountNonLocked(true);
            aue.setAccountNonExpired(true);
            aue.setCredentialsNonExpired(true);

            AuthUserEntity createdAuthUser = authUserDAO.createUser(aue);

            AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUser);

            authAuthorityDAO.createAuthorities(authorityEntities);
            return UserJson.fromEntity(userDAO.createUser(UserEntity.fromJson(user)), null);
        });
    }

    public UserJson createUserJdbcWithoutTx(UserJson user) {
        AuthUserEntity aue = new AuthUserEntity();
        aue.setUsername(user.username());
        aue.setPassword(ENCODER.encode("12345"));
        aue.setEnabled(true);
        aue.setAccountNonLocked(true);
        aue.setAccountNonExpired(true);
        aue.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserDAO.createUser(aue);

        AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUser);

        authAuthorityDAO.createAuthorities(authorityEntities);
        return UserJson.fromEntity(userDAO.createUser(UserEntity.fromJson(user)), null);
    }

    public UserJson createUserSpringJdbcWithoutTx(UserJson user) {
        AuthUserEntity aue = new AuthUserEntity();
        aue.setUsername(user.username());
        aue.setPassword(ENCODER.encode("12345"));
        aue.setEnabled(true);
        aue.setAccountNonLocked(true);
        aue.setAccountNonExpired(true);
        aue.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserSpringDAO.createUser(aue);

        AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUser);

        authAuthoritySpringDAO.createAuthorities(authorityEntities);
        return UserJson.fromEntity(userSpringDAO.createUser(UserEntity.fromJson(user)), null);

    }

    public UserJson createUser(UserJson userJson) {
        return txTemplateWithChainedTxManager.execute(status -> {
                    AuthUserEntity aue = new AuthUserEntity();
                    aue.setUsername(userJson.username());
                    aue.setPassword(ENCODER.encode("12345"));
                    aue.setEnabled(true);
                    aue.setAccountNonLocked(true);
                    aue.setAccountNonExpired(true);
                    aue.setCredentialsNonExpired(true);
                    //1 - создаем запись в табл user, niffler-auth
                    AuthUserEntity createdAuthUser = authUserSpringDAO.createUser(aue);

                    //2 - создаем 2 записи read и write в табл authorities, niffler-auth
                    AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUser);
                    authAuthoritySpringDAO.createAuthorities(authorityEntities);

                    //3- создаем запись в табл user, niffler-userdata
                    UserEntity ue = userSpringDAO.createUser(UserEntity.fromJson(userJson));
                    return UserJson.fromEntity(ue, null);
                }
        );
    }

    private static AuthorityEntity[] getAuthorityEntities(AuthUserEntity createdAuthUser) {
        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUserId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);
        return authorityEntities;
    }

    public Optional<UserEntity> findUserByUsername(String username) {
        return userSpringDAO.findByUsername(username);
    }

}