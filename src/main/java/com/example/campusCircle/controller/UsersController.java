package com.example.campusCircle.controller;


import org.springframework.web.bind.annotation.*;
import com.example.campusCircle.model.Users;
import com.example.campusCircle.service.UsersService;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public Users create(@RequestBody Users user) {
        return usersService.createUser(user);
    }

    @GetMapping("/{id}")
    public Users get(@PathVariable Long id) {
        return usersService.getUser(id);
    }

    @GetMapping
    public List<Users> getAll() {
        return usersService.getAllUsers();
    }

    @PutMapping("/{id}")
    public Users update(@PathVariable Long id, @RequestBody Users user) {
        return usersService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        usersService.deleteUser(id);
    }
}
