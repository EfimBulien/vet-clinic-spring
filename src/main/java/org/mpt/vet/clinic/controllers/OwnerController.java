package org.mpt.vet.clinic.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.Owner;
import org.mpt.vet.clinic.domains.User;
import org.mpt.vet.clinic.dto.CreateOwnerDto;
import org.mpt.vet.clinic.dto.UpdateOwnerDto;
import org.mpt.vet.clinic.services.OwnerService;
import org.mpt.vet.clinic.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/owners")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;

    @GetMapping
    public String ownersList(Model model) {
        List<Owner> owners = ownerService.findAll();
        List<User> availableUsers = ownerService.getAvailableUsers();

        model.addAttribute("owners", owners);
        model.addAttribute("availableUsers", availableUsers);
        model.addAttribute("createOwnerRequest", new CreateOwnerDto());
        return "admin/owners-list";
    }

    @PostMapping
    public String addOwner(
            @Valid @ModelAttribute("createOwnerRequest") CreateOwnerDto request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.createOwnerRequest", bindingResult);
            redirectAttributes.addFlashAttribute("createOwnerRequest", request);
            redirectAttributes.addFlashAttribute("error", "Ошибка валидации данных");
            return "redirect:/admin/owners";
        }

        try {
            // Проверяем уникальность телефона
            if (request.getPhone() != null && !request.getPhone().isEmpty()
                    && ownerService.existsByPhone(request.getPhone())) {
                redirectAttributes.addFlashAttribute("error", "Пользователь с таким телефоном уже существует");
                return "redirect:/admin/owners";
            }

            // Проверяем, не привязан ли уже пользователь
            if (ownerService.existsByUserId(request.getUserId())) {
                redirectAttributes.addFlashAttribute("error", "Этот пользователь уже привязан к другому владельцу");
                return "redirect:/admin/owners";
            }

            // Создаем владельца
            Owner owner = Owner.builder()
                    .fullName(request.getFullName())
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .build();

            Owner savedOwner = ownerService.createOwner(owner, request.getUserId());

            redirectAttributes.addFlashAttribute("success",
                    "Владелец " + request.getFullName() + " успешно создан!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании владельца: " + e.getMessage());
        }

        return "redirect:/admin/owners";
    }

    @GetMapping("/edit/{id}")
    public String showEditOwnerForm(@PathVariable Long id, Model model) {
        Owner owner = ownerService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Владелец не найден"));

        // Создаем DTO из сущности
        UpdateOwnerDto editOwnerDto = new UpdateOwnerDto();
        editOwnerDto.setFullName(owner.getFullName());
        editOwnerDto.setPhone(owner.getPhone());
        editOwnerDto.setAddress(owner.getAddress());

        model.addAttribute("ownerId", id);
        model.addAttribute("owner", owner); // для отображения email
        model.addAttribute("editOwnerDto", editOwnerDto);
        return "admin/owners-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateOwner(
            @PathVariable Long id,
            @Valid @ModelAttribute("editOwnerDto") UpdateOwnerDto editOwnerDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            // Если есть ошибки валидации, возвращаемся на форму редактирования
            Owner owner = ownerService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Владелец не найден"));
            model.addAttribute("owner", owner);
            model.addAttribute("ownerId", id);
            return "admin/owners-edit";
        }

        try {
            Owner ownerDetails = new Owner();
            ownerDetails.setFullName(editOwnerDto.getFullName());
            ownerDetails.setPhone(editOwnerDto.getPhone());
            ownerDetails.setAddress(editOwnerDto.getAddress());

            ownerService.updateOwner(id, ownerDetails);
            redirectAttributes.addFlashAttribute("success",
                    "Данные владельца " + editOwnerDto.getFullName() + " обновлены");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при обновлении данных: " + e.getMessage());
        }

        return "redirect:/admin/owners";
    }

    @PostMapping("/delete/{id}")
    public String deleteOwner(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Owner owner = ownerService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Владелец не найден"));

            ownerService.deleteById(id);

            redirectAttributes.addFlashAttribute("success",
                    "Владелец " + owner.getFullName() + " успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Не удалось удалить владельца: " + e.getMessage());
        }

        return "redirect:/admin/owners";
    }
}