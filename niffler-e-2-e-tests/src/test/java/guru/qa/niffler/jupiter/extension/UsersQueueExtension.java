package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.UsersApiClient;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UsersQueueExtension implements
    BeforeTestExecutionCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);
  public static final String USER_PW = "12345";
  private final UsersClient usersClient = new UsersApiClient();

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface UserType {
    Type value() default Type.EMPTY;

    enum Type {
      EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public void beforeTestExecution(ExtensionContext context) {
    Arrays.stream(context.getRequiredTestMethod().getParameters())
        .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class) && p.getType().isAssignableFrom(UserJson.class))
        .map(p -> p.getAnnotation(UserType.class))
        .forEach(ut -> {
          String username = randomUsername();
          UserJson targetUser = usersClient.createUser(username, USER_PW);

          UserJson userToStore = switch (ut.value()) {
            case EMPTY -> targetUser;
            case WITH_FRIEND ->  {
                usersClient.addFriend(targetUser, 1);
                yield targetUser;
            }
            case WITH_INCOME_REQUEST -> {
                usersClient.sendInvitation(targetUser, 1);
                yield targetUser;
            }
            case WITH_OUTCOME_REQUEST -> {
                UserJson randomUser = usersClient.createUser(randomUsername(), USER_PW);
                usersClient.sendInvitation(targetUser,randomUser);
                yield targetUser;
            }
          };

          ((Map<UserType, UserJson>) context.getStore(NAMESPACE).getOrComputeIfAbsent(
                context.getUniqueId(),
                key -> new HashMap<>()
          )).put(ut, userToStore);

        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class)
           && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return (UserJson) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class)
        .get(
            AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserType.class).get()
        );
  }
}
