package ru.voleshko.lib.auth;


import java.util.UUID;

public interface UserService {

    User getUserAuthentication();

    UUID getUserId();
}
