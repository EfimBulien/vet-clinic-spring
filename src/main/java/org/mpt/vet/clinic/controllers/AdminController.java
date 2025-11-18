package org.mpt.vet.clinic.controllers;

import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.services.RoleService;
import org.mpt.vet.clinic.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("userCount",  userService.findAll().size());
        model.addAttribute("roleCount",  roleService.findAll().size());
        return "admin/dashboard";
    }
}
