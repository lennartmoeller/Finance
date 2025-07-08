package com.lennartmoeller.finance.controller;

import static com.lennartmoeller.finance.testbuilder.AccountDTOBuilder.anAccount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lennartmoeller.finance.dto.AccountDTO;
import com.lennartmoeller.finance.service.AccountService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService service;

    @Nested
    class GetRequests {
        @Test
        void returnsAllAccounts() throws Exception {
            List<AccountDTO> accounts =
                    List.of(anAccount().withId(1L).withLabel("a").build());
            when(service.findAll()).thenReturn(accounts);

            mockMvc.perform(get("/api/accounts"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(accounts)));
        }

        @Test
        void returnsAccountByIdWhenFound() throws Exception {
            AccountDTO dto = anAccount().withId(2L).withLabel("acc").build();
            when(service.findById(2L)).thenReturn(Optional.of(dto));

            mockMvc.perform(get("/api/accounts/2"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(dto)));
        }

        @Test
        void returnsNotFoundWhenMissing() throws Exception {
            when(service.findById(3L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/accounts/3")).andExpect(status().isNotFound());
        }
    }

    @Nested
    class PostRequests {
        @Test
        void createsAccountWhenIdIsNull() throws Exception {
            AccountDTO dto = anAccount().withLabel("new").build();
            when(service.save(any(AccountDTO.class))).thenAnswer(invocation -> invocation.getArgument(0));

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(dto)));

            ArgumentCaptor<AccountDTO> captor = ArgumentCaptor.forClass(AccountDTO.class);
            verify(service).save(captor.capture());
            assertThat(captor.getValue().getLabel()).isEqualTo("new");
            verify(service, never()).findById(any());
        }

        @Test
        void updatesExistingAccount() throws Exception {
            AccountDTO dto = anAccount().withId(5L).withLabel("lbl").build();
            AccountDTO saved = anAccount().withId(5L).withLabel("lbl").build();
            when(service.findById(5L)).thenReturn(Optional.of(new AccountDTO()));
            when(service.save(any(AccountDTO.class))).thenReturn(saved);

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(saved)));
        }

        @Test
        void createsNewWhenIdUnknown() throws Exception {
            AccountDTO dto = anAccount().withId(7L).build();
            AccountDTO saved = anAccount().withId(8L).build();
            when(service.findById(7L)).thenReturn(Optional.empty());
            when(service.save(any(AccountDTO.class))).thenReturn(saved);

            mockMvc.perform(post("/api/accounts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(saved)));

            ArgumentCaptor<AccountDTO> captor = ArgumentCaptor.forClass(AccountDTO.class);
            verify(service).save(captor.capture());
            assertThat(captor.getValue().getId()).isNull();
        }
    }

    @Nested
    class DeleteRequests {
        @Test
        void deletesAccount() throws Exception {
            mockMvc.perform(delete("/api/accounts/9")).andExpect(status().isNoContent());
            verify(service).deleteById(9L);
        }
    }
}
