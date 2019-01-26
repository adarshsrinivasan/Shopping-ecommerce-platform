package com.example.inventoryservice.Service;

import com.example.inventoryservice.Model.InventoryItem;
import com.example.inventoryservice.Repository.InventoryItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    public InventoryItemService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public Optional<InventoryItem> findInventoryByProductCode(String productCode){
        return inventoryItemRepository.findByProductCode(productCode);
    }
}
