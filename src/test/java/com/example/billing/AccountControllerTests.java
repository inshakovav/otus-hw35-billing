package com.example.billing;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

//@SpringBootTest
@Slf4j
@ActiveProfiles("test")
class AccountControllerTests {

    @Autowired
    private WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .build();
    }
    @Test
    @Disabled
    void createOrder() throws Exception {
        // before

        // act
        mockMvc.perform(
                post("http://localhost:8082/account/top-up/2")
                        .content("{ \"amount\":\"2.1\"}")
                        .contentType(MediaType.APPLICATION_JSON)
        );
    }
}
