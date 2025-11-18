package org.mpt.vet.clinic.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.Cat;
import org.mpt.vet.clinic.services.CatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/cats")
@RequiredArgsConstructor
public class CatController {

    private final CatService catService;

    @GetMapping
    public String listCats(Model model) {
        model.addAttribute("cats", catService.findAll());
        return "cats/list";
    }

    @GetMapping({"/add", "/edit/{id}"})
    public String showForm(@PathVariable(required = false) Long id, Model model) {
        Cat cat = id == null ? new Cat() : catService.findById(id);
        if (id != null && cat == null) {
            return "redirect:/cats";
        }
        model.addAttribute("cat", cat);
        model.addAttribute("isEdit", id != null);
        return "cats/form";
    }

    @PostMapping("/save")
    public String saveCat(@Valid @ModelAttribute("cat") Cat cat,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", cat.getId() != null);
            return "cats/form";
        }

        catService.save(cat);
        redirectAttributes.addFlashAttribute("message",
                cat.getId() == null ? "Кошка добавлена!" : "Кошка обновлена!");
        return "redirect:/cats";
    }

    @PostMapping("/delete/{id}")
    public String deleteCat(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean force,
            RedirectAttributes ra) {

        boolean hasLinks = catService.hasOwners(id);
        if (hasLinks && (force == null || !force)) {
            ra.addFlashAttribute("modalCatId", id);
            ra.addFlashAttribute("modalMessage", "Кошка имеет владельцев. Удалить вместе со связями?");
            return "redirect:/cats";
        }

        catService.deleteById(id, force != null && force);
        ra.addFlashAttribute("message", "Кошка удалена!");
        return "redirect:/cats";
    }

    @PostMapping("/delete-selected")
    public String deleteSelectedCats(
            @RequestParam(value = "selectedIds", required = false) List<Long> ids,
            @RequestParam(value = "force", required = false) Boolean force,
            RedirectAttributes ra) {

        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("message", "Выберите хотя бы одну кошку.");
            return "redirect:/cats";
        }

        List<Long> blocked = new ArrayList<>();
        for (Long id : ids) {
            if (catService.hasOwners(id)) {
                blocked.add(id);
            }
        }

        if (!blocked.isEmpty() && (force == null || !force)) {
            ra.addFlashAttribute("modalCatIds", blocked);
            ra.addFlashAttribute("modalMessage", "Некоторые кошки имеют владельцев. Удалить со связями?");
            return "redirect:/cats";
        }

        ids.forEach(id -> catService.deleteById(id, true));
        ra.addFlashAttribute("message", "Выбранные кошки удалены!");
        return "redirect:/cats";
    }
}