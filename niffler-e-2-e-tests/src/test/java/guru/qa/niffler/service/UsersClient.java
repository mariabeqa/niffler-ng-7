package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

public interface UsersClient {
  UserJson createUser(String username, String password);

  void sendInvitation(UserJson targetUser, int count);

  void sendInvitation(UserJson user, UserJson targetUser);

  void addFriend(UserJson targetUser, int count);
}
