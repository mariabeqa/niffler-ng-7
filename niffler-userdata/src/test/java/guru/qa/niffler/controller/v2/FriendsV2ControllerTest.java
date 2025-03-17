package guru.qa.niffler.controller.v2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class FriendsV2ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Sql(scripts = "/allFriendsShouldBeReturnedPageable.sql")
    @Test
    void allFriendsShouldBeReturned() throws Exception {
        mockMvc.perform(get("/internal/v2/friends/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "sasha")
                        .param("page", "0")
                        .param("size", "2")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value("1"))
                .andExpect(jsonPath("$.totalElements").value("2"))
                .andExpect(jsonPath("$.size").value("2"))
                .andExpect(jsonPath("$.content", hasSize(2)));
    }
}
