package ru.voleshko.grocery.profile.service;

import org.keycloak.representations.idm.UserRepresentation;
import ru.voleshko.lib.auth.User;

public interface UserProfileService {

    UserRepresentation getUserProfile(String userId);

    UserRepresentation changeUserProfile(String userId, User changedUser);

}
