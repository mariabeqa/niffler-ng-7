package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserDataUserRepository;
import guru.qa.niffler.data.repository.impl.*;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;


public class UsersDbClient implements UsersClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private static final String USER_PW = "12345";
  //HIBERNATE
  private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate() ;
    private final UserDataUserRepository udUserRepository = new UserDataUserRepositoryHibernate();
  //JDBC
//  private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc() ;
//  private final UserDataUserRepository udUserRepository = new UserDataUserRepositoryJdbc();
  //SPRING JDBC
//  private final AuthUserRepository authUserRepository = new AuthUserRepositorySpringJdbc() ;
//  private final UserDataUserRepository udUserRepository = new UserDataUserRepositorySpringJdbc();

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
                  udUserRepository.create(userEntity(username)),
                  null
          );
        }
    );
  }

  public Optional<UserJson> findByUsername(String username) {
      Optional<UserEntity> ue = udUserRepository.findByUsername(username);
      return ue.map(userEntity -> UserJson.fromEntity(userEntity, null));
  }

  public Optional<UserJson> findById(UUID id) {
      Optional<UserEntity> ue = udUserRepository.findById(id);
      return ue.map(userEntity -> UserJson.fromEntity(userEntity, null));
  }

  public void sendInvitation(UserJson user) {
      xaTransactionTemplate.execute(() -> {
          UserEntity requester = udUserRepository.findById(user.id()).orElseThrow();

          String username = randomUsername();
          AuthUserEntity aue = authUserEntity(username,  USER_PW);
          authUserRepository.create(aue);
          UserEntity ue = udUserRepository.create(userEntity(username));

          udUserRepository.sendInvitation(requester, ue);
          return null;
      });
  }

  public UserJson updateUserInfo(UserJson user) {
      return xaTransactionTemplate.execute(() -> {
          UserEntity ueToUpdate = UserEntity.fromJson(user);

          String username = randomUsername();
          AuthUserEntity aue = authUserEntity(username,  USER_PW);
          authUserRepository.create(aue);
          UserEntity ue = udUserRepository.create(userEntity(username));

          switch (user.friendshipStatus()) {
              case INVITE_SENT -> { //ueToUpdate - requester send invite
                  ueToUpdate.addFriends(FriendshipStatus.PENDING, ue);
                  ue.addInvitations(ueToUpdate);
              }
              case INVITE_RECEIVED -> {//ueToUpdate - addressee gets friend request
                  ueToUpdate.addInvitations( ue);
                  ue.addFriends(FriendshipStatus.PENDING, ueToUpdate);
              }
              case FRIEND -> {
                  ueToUpdate.addFriends(FriendshipStatus.ACCEPTED, ue);
                  ue.addFriends(FriendshipStatus.ACCEPTED, ueToUpdate);
              }
          }
          UserEntity updated = udUserRepository.update(ueToUpdate);
          udUserRepository.update(ue);

          return UserJson.fromEntity(updated,
                  user.friendshipStatus());
        }
      );
  }

  public void addFriend(UserJson requester, UserJson addressee) {
      UserEntity sender = udUserRepository.findById(requester.id()).orElseThrow();
      UserEntity receiver = udUserRepository.findById(addressee.id()).orElseThrow();
      udUserRepository.addFriend(sender, receiver);
  }

    public void deleteUser(UserJson user) {
        xaTransactionTemplate.execute( () -> {
            UserEntity ueToDelete = udUserRepository.findById(
                    user.id()
            ).orElseThrow();
            AuthUserEntity aueToDelete = authUserRepository.findByUsername(
                    user.username()
            ).orElseThrow();

            authUserRepository.remove(aueToDelete);
            udUserRepository.remove(ueToDelete);
            return null;
        });
    }

    public List<FriendshipEntity> findInvitationByRequesterId(UUID id) {
        return udUserRepository.findInvitationByRequesterId(id);
    }

  private UserEntity userEntity(String username) {
      UserEntity ue = new UserEntity();
      ue.setUsername(username);
      ue.setCurrency(CurrencyValues.RUB) ;
       return ue;
  }

  public void createIncomeInvitations(UserJson targetUser, int count) {
      if (count > 0) {
          UserEntity targetEntity = udUserRepository.findById(
                  targetUser.id()
          ).orElseThrow();

          for (int i = 0; i < count; i++) {
              xaTransactionTemplate.execute(() -> {
                  UserEntity addressee = createRandomUser();

                  udUserRepository.sendInvitation(targetEntity, addressee);
                  return null;
              });
          }
      }
  }

  private UserEntity createRandomUser() {
        String username = randomUsername();
        AuthUserEntity authUser = authUserEntity(username,  USER_PW);
        authUserRepository.create(authUser);
        UserEntity addressee = udUserRepository.create(userEntity(username));
        return addressee;
  }

    public void createOutcomeInvitations(UserJson targetUser, int count) {
      if (count > 0) {
          UserEntity targetEntity = udUserRepository.findById(
                  targetUser.id()
          ).orElseThrow();

          for (int i = 0; i < count; i++) {
              xaTransactionTemplate.execute(() -> {
                  UserEntity addressee = createRandomUser();

                  udUserRepository.sendInvitation(targetEntity, addressee);
                  return null;
              });
          }
      }
  }

  public void createFriends(UserJson targetUser, int count) {
      if (count > 0) {
          UserEntity targetEntity = udUserRepository.findById(
                  targetUser.id()
          ).orElseThrow();

          for (int i = 0; i < count; i++) {
              xaTransactionTemplate.execute(() -> {
                          UserEntity addressee = createRandomUser();
                          udUserRepository.addFriend(
                                  targetEntity,
                                  addressee
                          );
                          return null;
                      }
              );
          }
      }
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
