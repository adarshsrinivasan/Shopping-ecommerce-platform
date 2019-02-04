package com.example.vendorservice.Controller;

import com.example.vendorservice.Model.ProductInventory;
import com.example.vendorservice.Service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendor")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @PostMapping("/{vendorId}/add-product")
    public void addProduct(@PathVariable String vendorId, @RequestBody List<ProductInventory> productInventories){
        vendorService.addProduct(vendorId, productInventories);
    }
}
