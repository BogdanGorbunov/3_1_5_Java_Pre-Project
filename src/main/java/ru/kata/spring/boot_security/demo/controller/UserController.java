package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.services.UserService;

@Controller
@RequestMapping()
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/logout")
    public String looutPage() {
        return "login";
    }

    @GetMapping("/user")
    public String userPage(Authentication authentication, Model model) {
        model.addAttribute("currentUser", userService.loadUserByUsername(authentication.getName()));
        return "user";
    }

    @GetMapping("/admin")
    public String adminPage(Authentication authentication, Model model) {
        model.addAttribute("currentUser", userService.loadUserByUsername(authentication.getName()));
        model.addAttribute("allUsers", userService.getAllUsers());
        model.addAttribute("newUser", new User());
        model.addAttribute("roles", userService.listRoles());
        return "admin";
    }

    @PostMapping("/admin")
    public String addUser(@ModelAttribute("user") User newUser) {
        userService.addUser(newUser);
        return "redirect:/admin";
    }

    @DeleteMapping("admin/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin";
    }

    @PatchMapping("admin/edit/{id}")
    public String changeUser(@PathVariable Long id, @ModelAttribute("user") User user) {
        userService.changeUser(id, user);
        return "redirect:/admin";
    }
}
