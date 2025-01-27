package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UDUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UDUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class UsersDbClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
  private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();
  private final UDUserRepository udUserRepository = new UDUserRepositoryJdbc();

  private final TransactionTemplate txTemplate = new TransactionTemplate(
      new JdbcTransactionManager(
          DataSources.dataSource(CFG.authJdbcUrl())
      )
  );

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );

  public UserJson createUser(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
          AuthUserEntity authUser = new AuthUserEntity();
          authUser.setUsername(user.username());
          authUser.setPassword(pe.encode("12345"));
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
      authUserRepository.create(authUser);
          return UserJson.fromEntity(
              udUserDao.create(UserEntity.fromJson(user)),
              null
          );
        }
    );
  }

  public Optional<UserEntity> findUserByID(UUID id) {
      return udUserRepository.findById(id);
  }

  public void addIncomeInvitation(UUID requesterUUID, UUID addresseeUUID) {
      UserEntity requester = new UserEntity();
      requester.setId(requesterUUID);
      UserEntity addressee = new UserEntity();
      addressee.setId(addresseeUUID);

      udUserRepository.addIncomeInvitation(requester, addressee);
  }

    public void addFriend(UUID requesterUUID, UUID addresseeUUID) {
        UserEntity requester = new UserEntity();
        requester.setId(requesterUUID);
        UserEntity addressee = new UserEntity();
        addressee.setId(addresseeUUID);

        udUserRepository.addFriend(requester, addressee);
    }

    //метод для проверки запросов на дружбу
    public List<FriendshipEntity> getFriendshipRequestsByUserID(UUID requesterUUID, UUID addresseeUUID) {
      UserEntity requester = new UserEntity();
      requester.setId(requesterUUID);
      UserEntity addressee = new UserEntity();
      addressee.setId(addresseeUUID);

      return udUserRepository.getFriendshipRequestsByUsersID(requester, addressee);
    }

}
