package com.csv.upload.api.services.impl;

import com.csv.upload.api.exception.util.CSVCustomException;
import com.csv.upload.api.model.Product;
import com.csv.upload.api.model.Response;
import com.csv.upload.api.repositories.CSVRepository;
import com.csv.upload.api.services.CSVService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Service
public class CSVServiceImpl implements CSVService {
    @Autowired
    private CSVRepository csvRepository;


    @Override
    public ResponseEntity uploadCSVDataTODB(MultipartFile file) throws IOException, CSVCustomException {
        if (file.isEmpty() || !file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).equalsIgnoreCase("csv")) {
            throw new CSVCustomException("Please select a CSV file to upload.");
        }

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<Product> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(Product.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Product> products=csvToBean.stream().collect(Collectors.toList());
            List<Product> duplicateIds=getDuplicates(products,"prodID");
            List<Product> duplicatessid=getDuplicates(products,"ssid");
            if(duplicateIds!=null && duplicateIds.size()>0){
                throw new CSVCustomException("Duplicates Found for item with ID:"+duplicateIds.stream().map(d->d.getProdID()).collect(Collectors.toSet()).stream().collect(Collectors.joining(",")));
            }else if(duplicatessid!=null && duplicatessid.size()>0){
                throw new CSVCustomException("Duplicates Found for item with SSID:"+duplicatessid.stream().map(d->d.getSsid()).collect(Collectors.toSet()).stream().collect(Collectors.joining(",")));
            }

            List<String> messages=validateAndSave(products);
            Response response=new Response();
            if(messages.size()>0 && products.size()> messages.size()){
                response.setMessage("File uploaded and data saved successfully but failed for some ["+messages.stream().collect(Collectors.joining(","))+"]");
                response.setStatus(HttpStatus.OK.value());
            }else if(messages.size()>0 && products.size()== messages.size()){
                response.setMessage("File uploaded and data saving failed  ["+messages.stream().collect(Collectors.joining(","))+"]");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
            } else {
                response.setMessage("File uploaded and data saved Successfully");
                response.setStatus(HttpStatus.OK.value());
            }
            response.setTimeStamp(System.currentTimeMillis());

            return new ResponseEntity(response,response.getStatus()==HttpStatus.OK.value()?HttpStatus.OK:HttpStatus.BAD_REQUEST);
        }



    }

    @Override
    public ResponseEntity update(Product product) {
        int status=HttpStatus.BAD_REQUEST.value();
        String message=null;
        if(product.getProdName()!=null && (product.getProdName().length()<3 || product.getProdName().length()>20)){
            message="Product name should be more than 3  and Less than 20 characters";
        }else if(product.getSsid()!=null && product.getSsid().length()!=10){
            message="SSID length should be equal to 10 ";
        }else if(csvRepository.findByProdID(product.getProdID())==null){
            message="Product doesn't exist";
        }else {
            message="Product updated Successfully";
            status=HttpStatus.OK.value();
            product.setModifiedBy("Admin");
            product.setModifiedDate(new Date(System.currentTimeMillis()));
            csvRepository.save(product);
        }
        Response response=new Response();
        response.setMessage(message);
        response.setStatus(status);
        response.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity(response,response.getStatus()==HttpStatus.OK.value()?HttpStatus.OK:HttpStatus.BAD_REQUEST);
    }

    private List<Product> getDuplicates(List<Product> products,String key){
        return getDuplicatesMap(products,key).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
    private Map<String, List<Product>> getDuplicatesMap(List<Product> personList,String key) {
        Stream<Product> productStream=personList.stream();
        if(key=="prodID")
            return productStream.collect(groupingBy(Product::getProdID));
        else if(key=="ssid")
            return  productStream.collect(groupingBy(Product::getSsid));
        return new HashMap<>();
    }

    private List<String> validateAndSave(List<Product> products){
        List<String> messages=new ArrayList<>();
        products.forEach(product -> {
             if(product.getProdName()!=null && (product.getProdName().length()<3 || product.getProdName().length()>20)){
                messages.add("Product name should be more than 3  and Less than 20 characters for prodID->"+product.getProdID());
             }else if(product.getSsid()!=null && product.getSsid().length()!=10){
                messages.add("SSID length should be equal to 10 for prodID->"+product.getProdID());
             }
             else if(csvRepository.findByProdID(product.getProdID())!=null){
                messages.add("ID already exists->prodID->"+product.getProdID());
            }else if(csvRepository.findBySsid(product.getSsid())!=null){
                messages.add("SSID already exists->ssid->"+product.getProdID());
            }else {
                 product.setCreatedBy("Admin");
                 product.setCreatedDate(new Date(System.currentTimeMillis()));
                 csvRepository.save(product);
             }
        });
        return messages;
    }
}
