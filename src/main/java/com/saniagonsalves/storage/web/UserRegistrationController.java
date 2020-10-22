package com.saniagonsalves.storage.web;

import com.saniagonsalves.storage.service.UserService;
import com.saniagonsalves.storage.web.dto.UserInfoDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

    private final UserService service;

    public UserRegistrationController(UserService service) {
        super();
        this.service = service;
    }

    @ModelAttribute("user")
    public UserInfoDto getUserInfoDto() {
        return new UserInfoDto();
    }

    @GetMapping
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping
    public String registerUser(@ModelAttribute("user") UserInfoDto user) {
        service.save(user);
        return "redirect:/registration?success";
    }
}

