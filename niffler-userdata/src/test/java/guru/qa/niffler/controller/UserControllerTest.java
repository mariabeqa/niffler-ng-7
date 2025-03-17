package guru.qa.niffler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @Sql(scripts = "/currentUserShouldBeReturned.sql")
  @Test
  void currentUserShouldBeReturned() throws Exception {
    mockMvc.perform(get("/internal/users/current")
            .contentType(MediaType.APPLICATION_JSON)
            .param("username", "dima")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("dima"))
        .andExpect(jsonPath("$.fullname").value("Dmitrii Tuchs"))
        .andExpect(jsonPath("$.currency").value("RUB"))
        .andExpect(jsonPath("$.photo").isNotEmpty())
        .andExpect(jsonPath("$.photoSmall").isNotEmpty());
  }

  @Sql(scripts = "/allUsersShouldBeReturned.sql")
  @Test
  void allUsersShouldBeReturned() throws Exception {
    mockMvc.perform(get("/internal/users/all")
            .contentType(MediaType.APPLICATION_JSON)
            .param("username", "maria")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0].username").value("ivan"))
        .andExpect(jsonPath("$[1].username").value("bee"))
        .andExpect(jsonPath("$[2].username").value("snake"))
        .andExpect(jsonPath("$[3].username").value("duck"))
        .andExpect(jsonPath("$[4].username").value("fox"));

  }

  @Sql(scripts = "/userShouldBeReturnedUpdated.sql")
  @Test
  void userShouldBeReturnedUpdated() throws Exception {
    Optional<UserEntity> userEntity = userRepository.findByUsername("maria");
    UserJson user = UserJson.fromEntity(userEntity.get());

      UserJson userToUpdate = new UserJson(
            user.id(),
            user.username(),
            user.firstname(),
            user.surname() + " Updated",
            user.fullname() + " Updated",
            CurrencyValues.USD,
            user.photo(),
            user.photoSmall(),
            user.friendshipStatus()
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .post("/internal/users/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(userToUpdate))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(userToUpdate.username()))
            .andExpect(jsonPath("$.fullname").value(userToUpdate.fullname()))
            .andExpect(jsonPath("$.currency").value(containsString(String.valueOf(CurrencyValues.USD))))
            .andExpect(jsonPath("$.photo").isNotEmpty())
            .andExpect(jsonPath("$.photoSmall").isNotEmpty());
  }
}