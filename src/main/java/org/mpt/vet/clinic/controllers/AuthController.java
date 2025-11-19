package org.mpt.vet.clinic.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mpt.vet.clinic.domains.Role;
import org.mpt.vet.clinic.dto.RegistrationDto;
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

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final OwnerService ownerService;
    private final UserMapper userMapper;

    @GetMapping("/login")
    public String login() {
        log.info("GET /auth/login - отображение страницы входа");
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.info("GET /auth/register - отображение формы регистрации");
        model.addAttribute(
                "request", new RegistrationDto("", "", "", "", "")
        );
        log.debug("Форма регистрации инициализирована с пустыми данными");
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("request") RegistrationDto request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        log.info("POST /auth/register - попытка регистрации пользователя с email: {}", request.getEmail());
        log.debug("Данные регистрации: имя={}, почта={}, пароль={}, адрес={}, телефон={}",
                request.getFullName(),
                request.getEmail(),
                request.getPassword(),
                request.getAddress(),
                request.getPhone()
        );

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при регистрации: {}", bindingResult.getAllErrors());
            log.debug("Количество ошибок валидации: {}", bindingResult.getErrorCount());
            return "auth/register";
        }

        try {
            String email = request.getEmail();

            if ("admin@example.com".equalsIgnoreCase(email)) {
                log.info("Регистрация пользователя с ролью ADMIN: {}", email);
                userService.registerUser(userMapper.toUser(request), Role.RoleType.ADMIN);
                log.debug("Администратор успешно зарегистрирован");

            } else if ("manager@example.com".equalsIgnoreCase(email)) {
                log.info("Регистрация пользователя с ролью MANAGER: {}", email);
                userService.registerUser(userMapper.toUser(request), Role.RoleType.MANAGER);
                log.debug("Менеджер успешно зарегистрирован");

            } else {
                log.info("Регистрация обычного владельца: {}", email);
                ownerService.registerOwner(request);
                log.debug("Владелец успешно зарегистрирован");
            }

            log.info("Успешная регистрация пользователя: {}", email);
            redirectAttributes.addFlashAttribute("success", "Регистрация прошла успешно!");
            return "redirect:/auth/login";

        } catch (IllegalArgumentException e) {
            log.error("Ошибка при регистрации пользователя {}: {}", request.getEmail(), e.getMessage());
            log.debug("Подробности ошибки:", e);
            bindingResult.rejectValue("email", "error.email", e.getMessage());
            return "auth/register";
        } catch (Exception e) {
            log.error("Неожиданная ошибка при регистрации пользователя {}: {}", request.getEmail(), e.getMessage());
            log.debug("Полный stack trace ошибки:", e);
            bindingResult.rejectValue(
                    "email",
                    "error.unexpected",
                    "Произошла непредвиденная ошибка"
            );
            return "auth/register";
        }
    }
}