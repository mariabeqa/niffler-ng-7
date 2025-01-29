package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UDUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UDUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class UsersDbClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate() ;
  private final UDUserRepository udUserRepository = new UDUserRepositoryHibernate();

  private final TransactionTemplate txTemplate = new TransactionTemplate(
      new JdbcTransactionManager(
          DataSources.dataSource(CFG.authJdbcUrl())
      )
  );

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );

  public UserJson createUser(String username, String password) {
    return xaTransactionTemplate.execute(() -> {
        AuthUserEntity authUser = authUserEntity(username, password);
        authUserRepository.create(authUser);
          return UserJson.fromEntity(
                  udUserRepository .create(userEntity(username)),
              null
          );
        }
    );
  }

  private UserEntity userEntity(String username) {
      UserEntity ue = new UserEntity();
      ue.setUsername(username);
      ue.setCurrency(CurrencyValues.RUB) ;
       return ue;
  }

  public void addIncomeInvitation(UserJson targetUser, int count) {
      if (count > 0) {
          UserEntity targetEntity = udUserRepository.findById(
                  targetUser.id()
          ).orElseThrow();

          for (int i = 0; i < count; i++) {
              xaTransactionTemplate.execute(() -> {
                  String username = RandomDataUtils.randomUsername();
                  AuthUserEntity authUser = authUserEntity(username,  "12345");
                  authUserRepository.create(authUser);
                  UserEntity addressee = udUserRepository.create(userEntity(username));

                  udUserRepository.addIncomeInvitation(targetEntity, addressee);
                  return null;
              });
          }
      }
  }

  public void addOutcomeInvitation(UserJson targetUser, int count) {
      if (count > 0) {
          UserEntity targetEntity = udUserRepository.findById(
                  targetUser.id()
          ).orElseThrow();

          for (int i = 0; i < count; i++) {
              xaTransactionTemplate.execute(() -> {
                  String username = RandomDataUtils.randomUsername();
                  AuthUserEntity authUser = authUserEntity(username,  "12345");
                  authUserRepository.create(authUser);
                  UserEntity addressee = udUserRepository.create(userEntity(username));

                  udUserRepository.addOutcomeInvitation(targetEntity, addressee);
                  return null;
              });
          }
      }
  }

  public void addFriend(UserJson targetUser, int count) {

  }

  private AuthUserEntity authUserEntity(String username, String password) {
      AuthUserEntity authUser = new AuthUserEntity();
      authUser.setUsername(username);
      authUser.setPassword(pe.encode(password));
      authUser.setEnabled(true);
      authUser.setAccountNonExpired(true);
      authUser.setAccountNonLocked(true);
      authUser.setCredentialsNonExpired(true);
      authUser.setAuthorities(
              Arrays.stream(Authority.values()).map(
                e -> {
                  AuthorityEntity ae = new AuthorityEntity();
                  ae.setUser(authUser);
                  ae.setAuthority(e);
                  return ae;
                }
            ).toList()
      );
      return authUser;
  }
}
