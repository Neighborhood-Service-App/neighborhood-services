package com.neighborhoodservice.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SimpleController {

    @GetMapping
    public String getHello() {
        return "Hello World!";
    }

}
