package com.csv.upload.api.service;

import com.csv.upload.api.exception.util.CSVCustomException;
import com.csv.upload.api.model.Product;
import com.csv.upload.api.model.Response;
import com.csv.upload.api.repositories.CSVRepository;
import com.csv.upload.api.services.impl.CSVServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @InjectMocks
    private CSVServiceImpl csvService;
    @Mock
    private CSVRepository csvRepository;

    @Test
    void uploadAndSavePositiveTest() throws IOException, CSVCustomException {
        MultipartFile multipartFile=mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("abc.csv");
        String data="prodID,prodName,ssid\n" +
                "1,Fan,ABCDEF1234";
        InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));
        System.out.println(in);
        when(multipartFile.getInputStream()).thenReturn(in);
        ResponseEntity<Response> responseEntity=csvService.uploadCSVDataTODB(multipartFile);
        //System.out.println(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
        Assertions.assertEquals("File uploaded and data saved Successfully",responseEntity.getBody().getMessage());

    }

    @Test
    void uploadAndSaveNotCSVNegativeTest() throws IOException, CSVCustomException {
        MultipartFile multipartFile=mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("abc.pdf");

        Assertions.assertThrows(CSVCustomException.class,()->csvService.uploadCSVDataTODB(multipartFile));

    }
    @Test
    void uploadAndSaveDuplicateTest() throws IOException, CSVCustomException {
        MultipartFile multipartFile=mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("abc.csv");
        String data="prodID,prodName,ssid\n" +
                "1,Fan,ABCDEF1234\n" +
                "1,Cooler,ABCDEF1235";
        InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));
        when(multipartFile.getInputStream()).thenReturn(in);
        Assertions.assertThrows(CSVCustomException.class,()->csvService.uploadCSVDataTODB(multipartFile));
    }

    @Test
    void updateTest() throws JsonProcessingException {
        Product product=new Product();
        product.setProdID("1");
        product.setSsid("ABC123456");
        product.setProdName("Cooler");
        Assertions.assertDoesNotThrow(()->csvService.update(product));

    }
}
