package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entities.Role;
import ru.kata.spring.boot_security.demo.entities.User;
import ru.kata.spring.boot_security.demo.exceptionHandling.NoSuchUserException;
import ru.kata.spring.boot_security.demo.exceptionHandling.UserIncorrectData;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.List;

@RestController
@RequestMapping ("/admin/api")
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/roles")
    public List<Role> getRoles() {
        return userService.listRoles();
    }

    @PostMapping("/users")
    public User addUser(@RequestBody User newUser) {
        userService.addUser(newUser);
        return newUser;
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);
        return "User with ID = " + id + " was deleted.";
    }

    @PatchMapping("/users/{id}")
    public String changeUser(@PathVariable Long id, @RequestBody User changedUser) {
        System.out.println(changedUser);
        userService.changeUser(id, changedUser);
        return "User with ID = " + id + " was updated.";
    }

    @ExceptionHandler
    public ResponseEntity<UserIncorrectData> handlerException(NoSuchUserException exception) {
        UserIncorrectData data = new UserIncorrectData();
        data.setInfo(exception.getMessage());
        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }
}
