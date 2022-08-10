package com.csv.upload.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @CsvBindByName(column = "prodID")
    private String prodID;
    @CsvBindByName(column = "prodName")
    private String prodName;
    @CsvBindByName(column ="ssid")
    private String ssid;

    @CsvIgnore
    private Date createdDate;
    @CsvIgnore
    private String createdBy;
    @CsvIgnore
    private Date modifiedDate;
    @CsvIgnore
    private String modifiedBy;



}
