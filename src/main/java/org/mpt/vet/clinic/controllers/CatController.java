package org.mpt.vet.clinic.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mpt.vet.clinic.domains.Cat;
import org.mpt.vet.clinic.services.CatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/cats")
@RequiredArgsConstructor
public class CatController {
    private final CatService catService;

    @GetMapping
    public String listCats(Model model) {
        log.info("GET /cats - получение списка всех кошек");
        List<Cat> cats = catService.findAll();
        log.debug("Найдено {} кошек", cats.size());
        model.addAttribute("cats", cats);
        return "cats/list";
    }

    @GetMapping({"/add", "/edit/{id}"})
    public String showForm(@PathVariable(required = false) Long id, Model model) {
        if (id == null) {
            log.info("GET /cats/add - отображение формы добавления новой кошки");
        } else {
            log.info("GET /cats/edit/{} - отображение формы редактирования кошки", id);
        }

        Cat cat = id == null ? new Cat() : catService.findById(id);

        if (id != null && cat == null) {
            log.warn("Кошка с ID {} не найдена, перенаправление на список", id);
            return "redirect:/cats";
        }

        model.addAttribute("cat", cat);
        model.addAttribute("isEdit", id != null);
        log.debug("Форма подготовлена для кошки: {}", cat);
        return "cats/form";
    }

    @PostMapping("/save")
    public String saveCat(@Valid @ModelAttribute("cat") Cat cat,
                          BindingResult result,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        log.info("POST /cats/save - сохранение кошки: {}", cat);

        if (result.hasErrors()) {
            log.warn("Ошибки валидации при сохранении кошки: {}", result.getAllErrors());
            model.addAttribute("isEdit", cat.getId() != null);
            return "cats/form";
        }

        try {
            Cat savedCat = catService.save(cat);
            String message = cat.getId() == null ? "Кошка добавлена!" : "Кошка обновлена!";
            log.info("Успешно сохранена кошка: {} с ID: {}", savedCat.getName(), savedCat.getId());
            redirectAttributes.addFlashAttribute("message", message);
        } catch (Exception e) {
            log.error("Ошибка при сохранении кошки: {}", cat, e);
            redirectAttributes.addFlashAttribute("error", "Ошибка при сохранении кошки");
            return "redirect:/cats";
        }

        return "redirect:/cats";
    }

    @PostMapping("/delete/{id}")
    public String deleteCat(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean force,
            RedirectAttributes ra) {

        log.info("POST /cats/delete/{} - удаление кошки, force: {}", id, force);

        boolean hasLinks = catService.hasOwners(id);
        log.debug("Кошка ID {} имеет владельцев: {}", id, hasLinks);

        if (hasLinks && (force == null || !force)) {
            log.info("Кошка ID {} имеет связи, требуется подтверждение удаления", id);
            ra.addFlashAttribute("modalCatId", id);
            ra.addFlashAttribute(
                    "modalMessage",
                    "Кошка имеет владельцев. Удалить вместе со связями?"
            );
            return "redirect:/cats";
        }

        try {
            catService.deleteById(id, force != null && force);
            log.info("Кошка ID {} успешно удалена", id);
            ra.addFlashAttribute("message", "Кошка удалена!");
        } catch (Exception e) {
            log.error("Ошибка при удалении кошки ID: {}", id, e);
            ra.addFlashAttribute("error", "Ошибка при удалении кошки");
        }

        return "redirect:/cats";
    }

    @PostMapping("/delete-selected")
    public String deleteSelectedCats(
            @RequestParam(value = "selectedIds", required = false) List<Long> ids,
            @RequestParam(value = "force", required = false) Boolean force,
            RedirectAttributes ra) {

        log.info("POST /cats/delete-selected - массовое удаление кошек: {}, force: {}", ids, force);

        if (ids == null || ids.isEmpty()) {
            log.warn("Попытка массового удаления без выбранных кошек");
            ra.addFlashAttribute("message", "Выберите хотя бы одну кошку.");
            return "redirect:/cats";
        }

        List<Long> blocked = new ArrayList<>();
        for (Long id : ids) {
            if (catService.hasOwners(id)) {
                blocked.add(id);
            }
        }

        log.debug("Заблокированные для удаления кошки (имеют владельцев): {}", blocked);

        if (!blocked.isEmpty() && (force == null || !force)) {
            log.info("Найдены кошки с владельцами {}, требуется подтверждение удаления", blocked);
            ra.addFlashAttribute("modalCatIds", blocked);
            ra.addFlashAttribute(
                    "modalMessage",
                    "Некоторые кошки имеют владельцев. Удалить со связями?"
            );
            return "redirect:/cats";
        }

        try {
            ids.forEach(id -> catService.deleteById(id, true));
            log.info("Успешно удалены {} кошек: {}", ids.size(), ids);
            ra.addFlashAttribute("message", "Выбранные кошки удалены!");
        } catch (Exception e) {
            log.error("Ошибка при массовом удалении кошек: {}", ids, e);
            ra.addFlashAttribute("error", "Ошибка при удалении кошек");
        }

        return "redirect:/cats";
    }
}