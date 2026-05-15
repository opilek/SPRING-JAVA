package org.example.web;

import org.example.models.User;
import org.example.services.UserServiceInterface;
import org.example.services.impl.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController
{
    private final UserServiceInterface userService;

    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @GetMapping
    public List<User> list()
    {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id)
    {
        return userService.findById(id);

    }
}
