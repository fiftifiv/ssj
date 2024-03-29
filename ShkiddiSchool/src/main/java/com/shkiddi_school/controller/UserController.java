package com.shkiddi_school.controller;

import com.shkiddi_school.domain.Role;
import com.shkiddi_school.domain.User;
import com.shkiddi_school.repos.UserRepo;
import com.shkiddi_school.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private UserService userService;

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userRepo.findAll());
        model.addAttribute("usersService", userService);
        return "userList";
    }

    @GetMapping("{user}")
    public String editUser(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }

    @GetMapping("delete/{user}")
    public String deleteUser(@PathVariable User user, Model model) {
        userRepo.delete(user);
        return "redirect:/user";
    }

    @PostMapping
    public String saveUser(

            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user
    ) {

        List<String> collect = form.entrySet()
                .stream()
                .map((entry) -> entry.getKey())
                .collect(Collectors.toList());


        Set<Role> roles = Arrays.stream(Role.values())
                .collect(Collectors.toMap(Role::name, role -> role))
                .entrySet()
                .stream()
                .filter((entry) -> collect.contains(entry.getKey()))
                .map(entry -> entry.getValue())
                .collect(Collectors.toSet());

        user.setUsername(form.get("username"));
        user.getRoles().clear();

        user.setRoles(roles);

        userRepo.save(user);
        return "redirect:/user";
    }
}
