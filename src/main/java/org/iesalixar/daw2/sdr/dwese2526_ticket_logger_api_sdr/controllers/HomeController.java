package org.iesalixar.daw2.sdr.dwese2526_ticket_logger_api_sdr.controllers;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(Model model){
        return "index";
    }
}
