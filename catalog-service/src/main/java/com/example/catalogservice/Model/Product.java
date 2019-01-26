package com.example.catalogservice.Model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private String name;
    private String description;
    private double price;

    @Transient
    private boolean inStock =true;

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}