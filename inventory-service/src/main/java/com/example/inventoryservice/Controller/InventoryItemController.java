package com.example.inventoryservice.Controller;

import com.example.inventoryservice.Exception.ProductNotFoundException;
import com.example.inventoryservice.Model.InventoryItem;
import com.example.inventoryservice.Service.InventoryItemService;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RefreshScope
@RequestMapping("/inventory")
@Slf4j
public class InventoryItemController {

    private InventoryItemService inventoryItemService;

    @Autowired
    public InventoryItemController (InventoryItemService inventoryItemService){
        this.inventoryItemService = inventoryItemService;
    }

    @PostMapping
    public void addInventory(@RequestBody InventoryItem inventoryItem){
        inventoryItemService.addInventory(inventoryItem);
    }

    @GetMapping
    public List<InventoryItem> findAllInventoryItems(){
        return inventoryItemService.findAllInventoryItems();
    }

    @GetMapping("/{productCode}")
    public ResponseEntity<InventoryItem> findInventoryByProductCode(@PathVariable String productCode){
        Optional<InventoryItem> optionalInventoryItem = inventoryItemService.findInventoryByProductCode(productCode);

        if(optionalInventoryItem.isPresent()){
            return new ResponseEntity(optionalInventoryItem, HttpStatus.OK);
        }
        else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public Optional<InventoryItem> updatedInventory(@RequestBody InventoryItem inventoryItem){
        Optional<InventoryItem> optionalInventoryItem = inventoryItemService.undateInventory(inventoryItem);
        optionalInventoryItem.orElseThrow(() -> new ProductNotFoundException("No product was found with given product Id"));
        return optionalInventoryItem;
    }

    @DeleteMapping("/{productCode}")
    public DeleteResult deleteInventoryByProductCode(@PathVariable String productCode){
        return inventoryItemService.deleteInventoryByProductCode(productCode);
    }

    @GetMapping("/init")
    public void initilizeMongo(){
        inventoryItemService.initMongoData();
    }
}
