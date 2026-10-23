package com.viptables.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.viptables.config.SecurityConfig;
import com.viptables.dto.ManagerDashboardResponse;
import com.viptables.security.DatabaseUserDetailsService;
import com.viptables.service.ManagerService;

@WebMvcTest(ManagerController.class)
@Import(SecurityConfig.class)
class ManagerAuthorizationTest {

    private static final String TEST_PASSWORD_HASH =
            new BCryptPasswordEncoder().encode("password");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerService managerService;

    @MockBean
    private DatabaseUserDetailsService databaseUserDetailsService;

    @Test
    void unauthenticatedUserCannotAccessManagerDashboard() throws Exception {
        mockMvc.perform(get("/api/manager/dashboard"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedApiRequestDoesNotTriggerBrowserBasicAuthChallenge() throws Exception {
        mockMvc.perform(get("/api/manager/dashboard"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("WWW-Authenticate"));
    }

    @Test
    void customerCannotAccessManagerDashboard() throws Exception {
        UserDetails customer = User.withUsername("customer@example.com")
                .password(TEST_PASSWORD_HASH)
                .roles("CUSTOMER")
                .build();
        when(databaseUserDetailsService.loadUserByUsername("customer@example.com"))
                .thenReturn(customer);

        mockMvc.perform(get("/api/manager/dashboard")
                        .with(httpBasic("customer@example.com", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void managerCanAccessManagerDashboard() throws Exception {
        UserDetails manager = User.withUsername("manager@example.com")
                .password(TEST_PASSWORD_HASH)
                .roles("MANAGER")
                .build();
        when(databaseUserDetailsService.loadUserByUsername("manager@example.com"))
                .thenReturn(manager);
        when(managerService.getDashboard())
                .thenReturn(new ManagerDashboardResponse(2, 4, 1, 0));

        mockMvc.perform(get("/api/manager/dashboard")
                        .with(httpBasic("manager@example.com", "password")))
                .andExpect(status().isOk());
    }
}
