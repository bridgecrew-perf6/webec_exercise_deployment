package ch.fhnw.webec.booklist.controller;

import ch.fhnw.webec.booklist.service.UserService;
import com.mitchellbosecke.pebble.boot.autoconfigure.PebbleAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AboutController.class)
@Import(PebbleAutoConfiguration.class)
public class AboutControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void testAboutPage() throws Exception {
        this.mockMvc.perform(get("/about"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("About")));
    }
}
