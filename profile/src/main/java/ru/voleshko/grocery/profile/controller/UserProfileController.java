package ru.voleshko.grocery.profile.controller;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.*;
import ru.voleshko.grocery.profile.service.UserProfileService;
import ru.voleshko.lib.auth.User;
import ru.voleshko.lib.auth.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class UserProfileController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    @ApiOperation(value = "Получить профиль [Пользователя]")
    @GetMapping("/my-profile")
    public UserRepresentation getUserProfile() {
        User user = userService.getUserAuthentication();
        return userProfileService.getUserProfile(user.getId());
    }

    @ApiOperation(value = "Изменить профиль [Пользователя]")
    @PatchMapping("/my-profile")
    public UserRepresentation changeUserProfile(@RequestBody User changedUser) {
        User user = userService.getUserAuthentication();
        return userProfileService.changeUserProfile(user.getId(), changedUser);
    }
}
