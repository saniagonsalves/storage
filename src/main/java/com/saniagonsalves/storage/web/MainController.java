package com.saniagonsalves.storage.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class MainController {

    @GetMapping("/login")
    public String login() {
        log.info("com.saniagonsalves.storage.web.MainController.login");
        return "login";
    }

    @GetMapping("/")
    public String home() {
        log.info("com.saniagonsalves.storage.web.MainController.home");
        return "redirect:/list";
    }
}
