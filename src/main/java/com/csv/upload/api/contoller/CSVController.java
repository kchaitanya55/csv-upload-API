package com.csv.upload.api.contoller;

import com.csv.upload.api.exception.util.CSVCustomException;
import com.csv.upload.api.model.PaginatedProductResponse;
import com.csv.upload.api.model.Product;
import com.csv.upload.api.repositories.CSVRepository;
import com.csv.upload.api.services.CSVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;

@RestController
public class CSVController {
    @Autowired
    private CSVService CSVService;

    @Autowired
    private CSVRepository CSVRepository;


    @PostMapping("/upload-csv")
    public ResponseEntity uploadFileAndSave(@RequestParam("file") MultipartFile file) throws IOException, CSVCustomException {
        return CSVService.uploadCSVDataTODB(file);
    }

    @PostMapping("/update")
    public ResponseEntity update(@RequestBody Product product){
        return CSVService.update(product);
    }


    @GetMapping("/data")
    public PaginatedProductResponse getProductInformation(@RequestParam(value = "prodID",required = false) String prodID, @RequestParam(value = "prodName",required = false) String prodName,@RequestParam(value = "createdBy",required = false) String createdBy,@RequestParam(value = "modifiedBy",required = false) String modifiedBy,@RequestParam(value = "ssid",required = false) String ssid,Pageable pageable) throws IOException {
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withMatcher("prodID", contains().ignoreCase())
                .withMatcher("prodName",contains().ignoreCase())
                .withMatcher("ssid",contains().ignoreCase())
                .withMatcher("createdBy",contains().ignoreCase())
                .withMatcher("modifiedBy",contains().ignoreCase());
        Product product = Product
                .builder()
                .prodID(prodID)
                .prodName(prodName)
                .ssid(ssid)
                .createdBy(createdBy)
                .modifiedBy(modifiedBy)
                .build();
        Page<Product> products= CSVRepository.findAll(Example.of(product,matcher),pageable);
        return PaginatedProductResponse.builder()
                .numberOfItems(products.getTotalElements()).numberOfPages(products.getTotalPages())
                .productList(products.getContent())
                .build();
    }

}
