package org.mpt.vet.clinic.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mpt.vet.clinic.domains.*;
import org.mpt.vet.clinic.dto.CreateVetVisitDto;
import org.mpt.vet.clinic.services.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
class UserController {
    private final UserService userService;
    private final OwnerService ownerService;
    private final CatService catService;
    private final VetVisitService vetVisitService;
    private final CatOwnerService catOwnerService;
    private final VetService vetService;

    @GetMapping("/user/profile")
    public String userProfile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();

            User user = userService.findByEmail(email);

            model.addAttribute("email", email);
            model.addAttribute("roles", userDetails.getAuthorities()
                    .stream()
                    .map(ga -> ga.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.toList()));
            model.addAttribute("user", user);

            try {
                Owner owner = ownerService.findByUserId(user.getId());
                List<Cat> cats = ownerService.findOwnCatsByUserId(user.getId());

                model.addAttribute("owner", owner);
                model.addAttribute("cats", cats);
                model.addAttribute("hasOwner", true);
            } catch (RuntimeException e) {
                model.addAttribute("owner", null);
                model.addAttribute("cats", Collections.emptyList());
                model.addAttribute("hasOwner", false);
            }
        }
        return "user/profile";
    }

    @GetMapping("/user/profile/edit")
    public String editProfileForm(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userService.findByEmail(email);

            try {
                Owner owner = ownerService.findByUserId(user.getId());
                model.addAttribute("owner", owner);
                model.addAttribute("hasOwner", true);
            } catch (RuntimeException e) {
                model.addAttribute("hasOwner", false);
            }

            model.addAttribute("email", email);
            model.addAttribute("roles", userDetails.getAuthorities().stream()
                    .map(ga -> ga.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.toList()));
        }
        return "user/edit-profile";
    }

    @PostMapping("/user/profile/update")
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Owner owner = ownerService.getCurrentOwner(authentication);
            ownerService.updateOwnerInfo(owner.getId(), fullName, phone, address);

            redirectAttributes.addFlashAttribute("success", "Данные профиля обновлены!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при обновлении данных: " + e.getMessage());
        }

        return "redirect:/user/profile";
    }

    @GetMapping("/user/visits")
    public String userVisits(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userService.findByEmail(email);

            try {
                Owner owner = ownerService.findByUserId(user.getId());
                List<VetVisit> visits = vetVisitService.findByOwnerId(owner.getId());

                long completedVisits = visits.stream()
                        .filter(v -> v.getDiagnosis() != null)
                        .count();

                long pendingVisitsCount = visits.stream()
                        .filter(v -> v.getDiagnosis() == null)
                        .count();

                BigDecimal totalCost = visits.stream()
                        .map(VetVisit::getCost)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                long visitsThisYear = visits.stream()
                        .filter(v -> v.getVisitDate().getYear() == LocalDate.now().getYear())
                        .count();

                model.addAttribute("visits", visits);
                model.addAttribute("totalCost", totalCost);
                model.addAttribute("visitsThisYear", visitsThisYear);
                model.addAttribute("completedVisits", completedVisits);
                model.addAttribute("pendingVisitsCount", pendingVisitsCount);
                model.addAttribute("hasOwner", true);

            } catch (RuntimeException e) {
                model.addAttribute("visits", Collections.emptyList());
                model.addAttribute("hasOwner", false);
            }

            model.addAttribute("email", email);
            model.addAttribute("roles", userDetails.getAuthorities().stream()
                    .map(ga -> ga.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.toList()));
        }
        return "user/visits";
    }

    @GetMapping("/user/visits/book")
    public String showBookVisitForm(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());

            try {
                Owner owner = ownerService.findByUserId(user.getId());
                List<Cat> cats = ownerService.findCatsByUserId(user.getId());
                List<Vet> vets = vetService.findAll();

                model.addAttribute("createVisitDto", new CreateVetVisitDto());
                model.addAttribute("cats", cats);
                model.addAttribute("vets", vets);
                model.addAttribute("minDate", LocalDate.now());
                model.addAttribute("hasOwner", true);

            } catch (RuntimeException e) {
                model.addAttribute("hasOwner", false);
            }
        }
        return "user/book-visit";
    }

    @PostMapping("/user/visits/book")
    public String bookVisit(
            @Valid @ModelAttribute("createVisitDto") CreateVetVisitDto createVisitDto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            ownerService.findByUserId(user.getId());

            List<Cat> cats = ownerService.findCatsByUserId(user.getId());
            List<Vet> vets = vetService.findAll();

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.createVisitDto", bindingResult);
            redirectAttributes.addFlashAttribute("createVisitDto", createVisitDto);
            redirectAttributes.addFlashAttribute("cats", cats);
            redirectAttributes.addFlashAttribute("vets", vets);
            redirectAttributes.addFlashAttribute("minDate", LocalDate.now());

            return "redirect:/user/visits/book";
        }

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());
            Owner owner = ownerService.findByUserId(user.getId());

            Cat cat = catService.findById(createVisitDto.getCatId());
            Vet vet = vetService.findById(createVisitDto.getVetId());

            VetVisit visit = VetVisit.builder()
                    .cat(cat)
                    .vet(vet)
                    .visitDate(createVisitDto.getVisitDate())
                    .diagnosis(null)
                    .treatment(null)
                    .cost(null)
                    .build();

            VetVisit savedVisit = vetVisitService.createVisit(visit, owner.getId());

            redirectAttributes.addFlashAttribute("success",
                    "Запись на прием для " + savedVisit.getCat().getName() + " успешно создана на " +
                            savedVisit.getVisitDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            return "redirect:/user/visits";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при создании записи: " + e.getMessage());
            return "redirect:/user/visits/book";
        }
    }

    @GetMapping("/user/visits/pending")
    public String pendingVisits(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByEmail(userDetails.getUsername());

            try {
                Owner owner = ownerService.findByUserId(user.getId());
                List<VetVisit> pendingVisits = vetVisitService.findPendingVisitsByOwnerId(owner.getId());

                model.addAttribute("pendingVisits", pendingVisits);
                model.addAttribute("hasOwner", true);

            } catch (RuntimeException e) {
                model.addAttribute("hasOwner", false);
            }

            model.addAttribute("email", userDetails.getUsername());
            model.addAttribute("roles", userDetails.getAuthorities().stream()
                    .map(ga -> ga.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.toList()));
        }
        return "user/pending-visits";
    }

    @GetMapping("/user/cat/add")
    public String showAddCatForm(Model model) {
        model.addAttribute("cat", new Cat());
        return "user/add-cat";
    }

    @PostMapping("/user/cat/add")
    public String addCat(
            @Valid @ModelAttribute("cat") Cat cat,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "user/add-cat";
        }

        try {
            ownerService.addCatToOwner(cat, authentication);
            redirectAttributes.addFlashAttribute(
                    "success", "Кошка \"" + cat.getName() + "\" добавлена!"
            );
            return "redirect:/user/profile";
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/cat/add";
        }
    }

    @GetMapping("/user/cat/edit/{catId}")
    public String editCatForm(@PathVariable Long catId, Model model, Authentication auth) {
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userService.findByEmail(email);
        Owner owner = ownerService.findByUserId(user.getId());

        Cat cat = catService.findById(catId);

        if (!catOwnerService.existsByCatIdAndOwnerId(catId, owner.getId())) {
            throw new SecurityException("Эта кошка вам не принадлежит");
        }

        LocalDate ownershipEndDate = catOwnerService.findByCatIdAndOwnerId(catId, owner.getId())
                .map(CatOwner::getOwnershipEndDate)
                .orElse(null);

        model.addAttribute("cat", cat);
        model.addAttribute("isEdit", true);
        model.addAttribute("ownerId", owner.getId());
        model.addAttribute("ownershipEndDate", ownershipEndDate);

        return "user/edit-cat";
    }

    @PostMapping("/user/cat/save")
    public String saveCat(
            @Valid @ModelAttribute("cat") Cat cat,
            BindingResult bindingResult,
            @RequestParam String action,
            @RequestParam Long catId,
            @RequestParam Long ownerId,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            String email = ((UserDetails) auth.getPrincipal()).getUsername();
            User user = userService.findByEmail(email);
            ownerService.findByUserId(user.getId());

            LocalDate ownershipEndDate = catOwnerService.findByCatIdAndOwnerId(catId, ownerId)
                    .map(CatOwner::getOwnershipEndDate)
                    .orElse(null);

            model.addAttribute("isEdit", true);
            model.addAttribute("ownerId", ownerId);
            model.addAttribute("ownershipEndDate", ownershipEndDate);

            return "user/edit-cat";
        }

        if ("save".equals(action)) {
            Cat existingCat = catService.findById(cat.getId());

            existingCat.setName(cat.getName());
            existingCat.setAge(cat.getAge());
            existingCat.setBreed(cat.getBreed());

            catService.save(existingCat);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Кошка " + existingCat.getName() + " обновлена"
            );
        } else if ("relinquish".equals(action)) {
            CatOwner catOwner = catOwnerService.findByCatIdAndOwnerId(catId, ownerId)
                    .orElseThrow(() -> new IllegalStateException("Связь владения не найдена"));

            catOwner.setOwnershipEndDate(LocalDate.now());
            catOwnerService.save(catOwner);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Вы больше не владелец кошки " + cat.getName()
            );
        }

        return "redirect:/user/profile";
    }
}