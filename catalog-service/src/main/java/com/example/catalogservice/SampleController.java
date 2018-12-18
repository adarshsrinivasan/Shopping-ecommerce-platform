package com.example.catalogservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class SampleController {
    @Value("${name}")
    String name;

    @GetMapping("/name")
    public String name()
    {
        return name;
    }
}
