package com.authorization_server.server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Sirve la página de login propia (templates/login.html).
 * El POST /login lo procesa Spring Security (formLogin), no este controlador.
 */
@Controller
public class LoginController {

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("frontendUrl", frontendUrl);
        return "login";
    }

    /** El server no tiene página de inicio: "/" lleva a la consola. */
    @GetMapping("/")
    public String home() {
        return "redirect:" + frontendUrl + "/login";
    }
}
