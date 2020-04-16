package com.uanid.toy.simplefileserver.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * @author uanid
 * @since 2020-04-16
 */
@Controller
public class RootController {

    @GetMapping("/")
    public String root() {
        return "redirect:/html";
    }

    @GetMapping("/health/ping")
    @ResponseBody
    public String ping(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        return "\"ping\"";
    }}
