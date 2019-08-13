package com.zben.eshop.cache.ha.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @DESC:
 * @author: jhon.zhou
 * @date: 2019/8/8 0008 18:34
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hi hello";
    }
}
