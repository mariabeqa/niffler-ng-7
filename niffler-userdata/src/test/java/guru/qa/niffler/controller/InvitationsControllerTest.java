package guru.qa.niffler.controller;

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

import static guru.qa.niffler.model.FriendshipStatus.FRIEND;
import static guru.qa.niffler.model.FriendshipStatus.INVITE_SENT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class InvitationsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Sql(scripts = "/invitationShouldBeSent.sql")
    @Test
    void invitationShouldBeSent() throws Exception {
        String username = "sergey";
        String targetUsername = "alina";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/internal/invitations/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", username)
                        .param("targetUsername", targetUsername)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendshipStatus").value(INVITE_SENT.name()))
                .andExpect(jsonPath("$.username").value(targetUsername));
    }

    @Sql(scripts = "/invitationShouldBeAccepted.sql")
    @Test
    void invitationShouldBeAccepted() throws Exception {
        String username = "snake";
        String targetUsername = "duck";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/internal/invitations/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", username)
                        .param("targetUsername", targetUsername)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendshipStatus").value(FRIEND.name()))
                .andExpect(jsonPath("$.username").value(targetUsername));
    }

    @Sql(scripts = "/invitationShouldBeDeclined.sql")
    @Test
    void invitationShouldBeDeclined() throws Exception {
        String username = "snake";
        String targetUsername = "duck";

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/internal/invitations/decline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", username)
                        .param("targetUsername", targetUsername)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendshipStatus").doesNotExist())
                .andExpect(jsonPath("$.username").value(targetUsername));
    }
}
