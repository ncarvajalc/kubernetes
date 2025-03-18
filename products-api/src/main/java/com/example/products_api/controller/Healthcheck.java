package com.example.products_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Healthcheck {

    @RequestMapping("/")
    public String healthcheck() {
        return "OK";
    }
}
