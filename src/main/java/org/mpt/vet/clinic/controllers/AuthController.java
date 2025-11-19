package org.mpt.vet.clinic.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.Role;
import org.mpt.vet.clinic.dto.CreateOwnerDto;
import org.mpt.vet.clinic.mappers.UserMapper;
import org.mpt.vet.clinic.services.OwnerService;
import org.mpt.vet.clinic.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final OwnerService ownerService;
    private final UserMapper userMapper;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute(
                "request",
                new CreateOwnerDto("", "", "", "", "")
        );
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("request") CreateOwnerDto request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            if ("admin@example.com".equalsIgnoreCase(request.getEmail())) {
                userService.registerUser(userMapper.toUser(request), Role.RoleType.ADMIN);

            } else if ("manager@example.com".equalsIgnoreCase(request.getEmail())) {
                userService.registerUser(userMapper.toUser(request), Role.RoleType.MANAGER);

            } else {
                ownerService.registerOwner(request);
            }

            redirectAttributes.addFlashAttribute("success", "Регистрация прошла успешно!");
            return "redirect:/auth/login";

        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return "auth/register";
        }
    }
}
