package org.mpt.vet.clinic.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.Vet;
import org.mpt.vet.clinic.dto.VetDto;
import org.mpt.vet.clinic.services.VetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
class ManagerController {
    private final VetService vetService;

    @GetMapping("/manager/reports")
    public String managerReports(Model model) {
        model.addAttribute("monthlySales", 125_000);
        model.addAttribute("newUsers", 137);
        return "manager/reports";
    }

    @GetMapping("/manager/vets")
    public String listVets(Model model) {
        List<Vet> vets = vetService.findAll();
        model.addAttribute("vets", vets);
        return "manager/vets-list";
    }

    @GetMapping("/manager/vets/add")
    public String showAddVetForm(Model model) {
        model.addAttribute("vetDto", new VetDto());
        return "manager/vet-form";
    }

    @PostMapping("/manager/vets/add")
    public String addVet(
            @Valid @ModelAttribute("vetDto") VetDto vetDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "manager/vet-form";
        }

        if (vetService.existsByPhone(vetDto.getPhone())) {
            bindingResult.rejectValue(
                    "phone",
                    "error.vetDto",
                    "Ветеринар с таким телефоном уже существует"
            );
            return "manager/vet-form";
        }

        if (vetService.existsByNameAndSpecialtyAndClinicName(
                vetDto.getName(),
                vetDto.getSpecialty(),
                vetDto.getClinicName())) {
            bindingResult.rejectValue(
                    "name",
                    "error.vetDto",
                    "Ветеринар с таким именем, специализацией и клиникой уже существует"
            );
            return "manager/vet-form";
        }

        Vet vet = Vet.builder()
                .name(vetDto.getName())
                .specialty(vetDto.getSpecialty())
                .clinicName(vetDto.getClinicName())
                .phone(vetDto.getPhone())
                .build();

        vetService.save(vet);

        redirectAttributes.addFlashAttribute("success",
                "Ветеринар " + vet.getName() + " успешно добавлен!");
        return "redirect:/manager/vets";
    }

    @GetMapping("/manager/vets/edit/{id}")
    public String showEditVetForm(@PathVariable Long id, Model model) {
        Vet vet = vetService.findById(id);
        VetDto vetDto = new VetDto();
        vetDto.setName(vet.getName());
        vetDto.setSpecialty(vet.getSpecialty());
        vetDto.setClinicName(vet.getClinicName());
        vetDto.setPhone(vet.getPhone());

        model.addAttribute("vetDto", vetDto);
        model.addAttribute("vetId", id);
        model.addAttribute("isEdit", true);
        return "manager/vet-form";
    }

    @PostMapping("/manager/vets/edit/{id}")
    public String updateVet(
            @PathVariable Long id,
            @Valid @ModelAttribute("vetDto") VetDto vetDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "manager/vet-form";
        }

        Vet existingVet = vetService.findById(id);

        if (!existingVet.getPhone().equals(vetDto.getPhone()) &&
                vetService.existsByPhone(vetDto.getPhone())) {
            bindingResult.rejectValue(
                    "phone",
                    "error.vetDto",
                    "Ветеринар с таким телефоном уже существует"
            );
            return "manager/vet-form";
        }

        boolean nameOrSpecialtyOrClinicChanged = !existingVet.getName().equals(vetDto.getName()) ||
                !Objects.equals(existingVet.getSpecialty(), vetDto.getSpecialty()) ||
                !existingVet.getClinicName().equals(vetDto.getClinicName());

        if (nameOrSpecialtyOrClinicChanged &&
                vetService.existsByNameAndSpecialtyAndClinicNameAndIdNot(
                        vetDto.getName(),
                        vetDto.getSpecialty(),
                        vetDto.getClinicName(),
                        id)) {
            bindingResult.rejectValue(
                    "name",
                    "error.vetDto",
                    "Ветеринар с таким именем, специализацией и клиникой уже существует"
            );
            return "manager/vet-form";
        }

        existingVet.setName(vetDto.getName());
        existingVet.setSpecialty(vetDto.getSpecialty());
        existingVet.setClinicName(vetDto.getClinicName());
        existingVet.setPhone(vetDto.getPhone());

        vetService.save(existingVet);

        redirectAttributes.addFlashAttribute("success",
                "Ветеринар " + existingVet.getName() + " успешно обновлен!");
        return "redirect:/manager/vets";
    }

    @PostMapping("/manager/vets/delete/{id}")
    public String deleteVet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Vet vet = vetService.findById(id);
            vetService.deleteById(id);
            redirectAttributes.addFlashAttribute("success",
                    "Ветеринар " + vet.getName() + " успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Не удалось удалить ветеринара: " + e.getMessage());
        }
        return "redirect:/manager/vets";
    }
}
