package com.csv.upload.api.services;

import com.csv.upload.api.exception.util.CSVCustomException;
import com.csv.upload.api.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface CSVService {
    ResponseEntity uploadCSVDataTODB(MultipartFile file) throws IOException, CSVCustomException;
    ResponseEntity update(Product product);
}
