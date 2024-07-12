package com.ghtk.social_network.application.controller;

import com.ghtk.social_network.domain.model.form.UserUpdateForm;
import com.ghtk.social_network.domain.service.IUserService;
import com.ghtk.social_network.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private IUserService service;

    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id, @RequestBody UserUpdateForm form) {
        service.update(id, form);
    }
}
