package com.example.vendorservice.Model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "vendor")
public class Vendor {
    @Id
    @Column(name = "vendor_id", nullable = false, unique = true)
    private String vendorId;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column(name = "location", nullable = false, unique = true)
    private Integer location;
}
