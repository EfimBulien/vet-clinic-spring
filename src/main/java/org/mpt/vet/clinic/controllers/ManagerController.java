package org.mpt.vet.clinic.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class ManagerController {
    @GetMapping("/manager/reports")
    public String managerReports(Model model) {
        model.addAttribute("monthlySales", 125_000);
        model.addAttribute("newUsers", 137);
        return "manager/reports";
    }
}
