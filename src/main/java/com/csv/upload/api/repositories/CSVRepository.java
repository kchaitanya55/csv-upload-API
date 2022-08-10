package com.csv.upload.api.repositories;

import com.csv.upload.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface CSVRepository extends JpaRepository<Product,String> {
    Product findBySsid(String ssid);
    Product findByProdID(String prodID);
}
