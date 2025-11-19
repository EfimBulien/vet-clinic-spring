package org.mpt.vet.clinic.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.Role;
import org.mpt.vet.clinic.domains.User;
import org.mpt.vet.clinic.dto.CreateUserDto;
import org.mpt.vet.clinic.services.RoleService;
import org.mpt.vet.clinic.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("userCount", users.size());
        model.addAttribute("roleCount", roleService.findAll().size());
        model.addAttribute("users", users);
        model.addAttribute("createUserRequest", new CreateUserDto());
        return "admin/dashboard";
    }

    @PostMapping("/users")
    public String addUser(
            @Valid @ModelAttribute("createUserRequest") CreateUserDto request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Ошибка валидации данных");
            return "redirect:/admin/dashboard";
        }

        try {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());

            Set<Role.RoleType> roleTypes = (request.getRoles() != null ? request.getRoles().stream()
                    .map(String::toUpperCase)
                    .map(Role.RoleType::valueOf)
                    .collect(Collectors.toSet()) : Set.of(Role.RoleType.USER));

            userService.registerUser(user, roleTypes);

            redirectAttributes.addFlashAttribute("success",
                    "Пользователь " + request.getEmail() + " успешно создан!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании пользователя: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        List<Role> allRoles = roleService.findAll();

        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "admin/edit-user";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUserRoles(
            @PathVariable Long id,
            @RequestParam(value = "roles", required = false) List<String> roles,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Set<Role.RoleType> roleTypes = (roles != null ? roles.stream()
                    .map(String::toUpperCase)
                    .map(Role.RoleType::valueOf)
                    .collect(Collectors.toSet()) : Set.of(Role.RoleType.USER));

            userService.updateRoles(id, roleTypes);

            User user = userService.findById(id);
            redirectAttributes.addFlashAttribute("success",
                    "Роли пользователя " + user.getEmail() + " обновлены");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при обновлении ролей: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }

//    @PostMapping("/users/{id}/roles")
//    public String updateRoles(
//            @PathVariable Long id,
//            @RequestParam List<String> roles,
//            RedirectAttributes redirectAttributes
//    ) {
//        try {
//            Set<Role.RoleType> roleTypes = roles.stream()
//                    .map(Role.RoleType::valueOf)
//                    .collect(Collectors.toSet());
//
//            userService.updateRoles(id, roleTypes);
//
//            redirectAttributes.addFlashAttribute("success", "Роли пользователя обновлены");
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении ролей: " + e.getMessage());
//        }
//
//        return "redirect:/admin/dashboard";
//    }

    @PostMapping("/users/{id}")
    public String deleteUser(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            User user = userService.findById(id);
            userService.deleteById(id);

            redirectAttributes.addFlashAttribute("success",
                    "Пользователь " + user.getEmail() + " успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Не удалось удалить пользователя: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }
}