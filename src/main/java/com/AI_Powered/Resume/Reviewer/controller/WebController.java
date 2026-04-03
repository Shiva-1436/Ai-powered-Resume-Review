package com.AI_Powered.Resume.Reviewer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login.html";
    }

    @GetMapping("/index.html")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/login.html")
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping("/register.html")
    public String register() {
        return "forward:/register.html";
    }
}