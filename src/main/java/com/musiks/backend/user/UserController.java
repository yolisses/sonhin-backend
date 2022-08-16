package com.musiks.backend.user;

import com.musiks.backend.auth.Auth;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    Auth auth;
    UserRepo userRepo;

    @GetMapping("/me")
    User me(HttpServletRequest req) {
        return auth.getUser(req);
    }


    @PostMapping("/{id}/follow")
    void follow(HttpServletRequest req, @PathVariable long id) {
        var currentUser = auth.getUser(req);
        if (currentUser.id == id) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A user can't follow himself"
            );
        }
        var user = userRepo.findById(id);
        if (user.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"
            );
        }
        currentUser.follows.add(user.get());
        userRepo.save(currentUser);
    }

    @DeleteMapping("/{id}/follow")
    void unfollow(HttpServletRequest req, @PathVariable long id) {
        var currentUser = auth.getUser(req);
        var user = userRepo.findById(id);
        if (user.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User not found"
            );
        }
        currentUser.follows.remove(user.get());
        userRepo.save(currentUser);
    }
}
