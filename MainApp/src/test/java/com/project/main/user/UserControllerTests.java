package com.project.main.user;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.project.main.user.UserAccount;
import com.project.main.user.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

@SpringBootTest
public class UserControllerTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private UserAccountRepository userRepository;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testGetByEmail_UserFound() throws Exception {
        UserAccount userAccount = new UserAccount();
        userAccount.setEmail("test30@test.lt");

        when(userRepository.findByEmail("test30@test.lt")).thenReturn(Optional.of(userAccount));

        mockMvc.perform(get("/api/rest/user/get")
                .param("email", "test30@test.lt")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test30@test.lt"));
    }

    @Test
    public void testGetByEmail_UserNotFound() throws Exception {
        when(userRepository.findByEmail("unknown@test.lt")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/rest/user/get")
                .param("email", "unknown@test.lt")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}