package guru.qa.niffler.test.web;

import guru.qa.niffler.model.*;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JdbcTest {

//    @Test
//    void txTest() {
//
//        SpendDbClient spendDbClient = new SpendDbClient();
//
//        SpendJson spendJson = spendDbClient.createSpend(
//                new SpendJson(
//                        null,
//                        new Date(),
//                        new CategoryJson(
//                                null,
//                                "cat-name-ttx",
//                                "maria",
//                                false
//                        ),
//                        CurrencyValues.RUB,
//                        1000.0,
//                        "spend-name-ttx",
//                        "maria"
//                )
//        );
//
//    }

//    @Test
//    void xaTransactionsCorrectDataTest() {
//        UserDbClient userDbClient = new UserDbClient();
//        String username = RandomDataUtils.randomUsername();
//
//        UserJson user = userDbClient.createUser(
//                new UserJson(
//                        null,
//                        username,
//                        "First Name",
//                        "Surname",
//                        "Full Name",
//                        CurrencyValues.RUB,
//                        null,
//                        null,
//                        null
//                )
//        );
//
//        assertEquals(username, user.username());
//    }

//    @Test
//    void xaTransactionsInCorrectDataTest() {
//        UserDbClient userDbClient = new UserDbClient();
//        String username = "incorrectData";
//
//        try {
//            UserJson user = userDbClient.createUser(
//                    new UserJson(
//                            null,
//                            null,
//                            "First Name",
//                            "Surname",
//                            "Full Name",
//                            CurrencyValues.RUB,
//                            null,
//                            null,
//                            null
//                    )
//            );
//        } catch (IllegalArgumentException e) {
//            //NOP
//        } finally {
//            Assertions.assertFalse(userDbClient.findUserByUsername(username).isPresent());
//        }
//
//
//    }


    @Test
    void springJdbcTest() {
        UserDbClient userDbClient = new UserDbClient();

        UserJson user = userDbClient.createUserSpringJdbc(
                new UserJson(
                        null,
                        "valentin-6",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
        System.out.println(user);
    }


//    @Test
//    void springSpendJdbcTest() {
//        SpendDbClient spendDbClient = new SpendDbClient();
//
//        spendDbClient.createSpendSpringJdbc(
//                new SpendJson(
//                        null,
//                        new Date(),
//                        new CategoryJson(
//                                null,
//                                "Fast Food Test2",
//                                "duck",
//                                false
//                        ),
//                        CurrencyValues.RUB,
//                        1800.0,
//                        "Fast Food description",
//                        "duck"
//                )
//        );
//    }

}

