package com.uanid.toy.simplefileserver.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author uanid
 * @since 2020-04-16
 */
@RequestMapping("/api")
@RestController
public class ApiController {

    @GetMapping("/ping")
    public String ping(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        return "\"ping\"";
    }


}
