package guru.qa.niffler.test.web;

import guru.qa.niffler.model.*;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;


public class JdbcTest {

    @Test
    void jdbcTxTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUserJdbcWithTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        Assertions.assertTrue(userDbClient.findUserByUsername(user.username()).isPresent());
    }

    @Test
    void jdbcWithoutTxTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUserJdbcWithoutTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        Assertions.assertTrue(userDbClient.findUserByUsername(user.username()).isPresent());
    }

    @Test
    void springJdbcWithoutTxTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUserSpringJdbcWithoutTx(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        Assertions.assertTrue(userDbClient.findUserByUsername(user.username()).isPresent());
    }


    @Test
    void springJdbcTxTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        String categoryName = RandomDataUtils.randomCategoryName();

        spendDbClient.createSpendSpringJdbc(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                categoryName,
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1800.0,
                        "Fast Food description",
                        "duck"
                )
        );
    }

    @Test
    void springChainedManagerWithCorrectDataTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUser(
                new UserJson(
                        null,
                        username,
                        null,
                        null,
                        "Chained Manager Positive Test",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null

                ));

        Assertions.assertTrue(userDbClient.findUserByUsername(user.username()).isPresent());
    }

    @Test
    void springChainedManagerWithIncorrectDataTest() {
        UserDbClient userDbClient = new UserDbClient();
        String username = RandomDataUtils.randomUsername();

        UserJson user = userDbClient.createUser(
                new UserJson(
                        //Если передать null в качестве username в UDUserDAOSpringJdbc
                        //ps.setString(1, null);
                        //то при создании пользователя произойдет ошибка, но при этом
                        //будут созданы записи в табл user-auth и authorities-auth
                        //т.е. транзакция не откатилась после ошибки в userdata,
                        //что доказывает невозможность отката внутренней транзакции при сбое во внешней
                        null,
                        username,
                        null,
                        null,
                        "Chained Manager Negative Test",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null

                ));

        System.out.println(user);
    }
}

