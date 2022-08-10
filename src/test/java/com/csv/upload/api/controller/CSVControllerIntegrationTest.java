package com.csv.upload.api.controller;

import com.csv.upload.api.contoller.CSVController;
import com.csv.upload.api.model.Product;
import com.csv.upload.api.model.Response;
import com.csv.upload.api.services.CSVService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CSVControllerIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    @InjectMocks
    private CSVController csvController;

    @MockBean
    private CSVService csvService;


    @Test
    void integrationTestUpload() throws Exception {
        MockMultipartFile csv = new MockMultipartFile("test.csv", "test.csv", "text/csv", "prodID,prodName,ssid\n1,Fan,ABCDEF1234\n1,Cooler,ABCDEF1235".getBytes());
        when(csvService.uploadCSVDataTODB(csv)).thenReturn(new ResponseEntity(new Response(),HttpStatus.OK));
        mockMvc= MockMvcBuilders.standaloneSetup(csvController).build();
        mockMvc.perform( MockMvcRequestBuilders
                        .multipart("/upload-csv")
                        .file("file",csv.getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk());
    }
    @Test
    void integrationTestUpdate() throws Exception {
        mockMvc= MockMvcBuilders.standaloneSetup(csvController).build();

        mockMvc.perform( MockMvcRequestBuilders
                        .post("/update")
                        .content("{\"prodID\":\"1\",\"prodName\":\"ABCD\",\"ssid\":\"ABCD12346\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk());
    }

    @Test
    void productInformationTest() throws Exception {
        mockMvc= MockMvcBuilders.standaloneSetup(csvController).setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        mockMvc.perform( MockMvcRequestBuilders
                        .get("/data")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }



}
