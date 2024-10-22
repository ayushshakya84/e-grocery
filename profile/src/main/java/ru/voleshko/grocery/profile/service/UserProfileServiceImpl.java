package ru.voleshko.grocery.profile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.voleshko.grocery.profile.exception.EntityNotFoundException;
import ru.voleshko.lib.auth.UnauthorizedException;
import ru.voleshko.lib.auth.User;

import javax.ws.rs.NotFoundException;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    @Value("${keycloak.realm.name}")
    private String keycloakRealm;

    private final Keycloak keycloakClient;

    @Override
    public UserRepresentation getUserProfile(String userId) {
        if (isNull(userId)) {
            throw new UnauthorizedException("Required data about User is not present to find profile");
        }

        try {
            return getUserResource(userId).toRepresentation();
        } catch (NotFoundException exception) {
            String msg = "User with id = " + userId + " was not found";
            log.error(msg);
            throw new EntityNotFoundException(msg);
        }
    }

    @Override
    public UserRepresentation changeUserProfile(String userId, User changedUser) {
        if (isNull(userId)) {
            throw new UnauthorizedException("Required data about User is not present to find profile");
        }

        UserResource userResource = getUserResource(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();

        userRepresentation.setFirstName(changedUser.getFirstName());
        userRepresentation.setLastName(changedUser.getLastName());
        userRepresentation.setEmail(changedUser.getEmail());

        userResource.update(userRepresentation);

        return userRepresentation;
    }

    private UserResource getUserResource(String userId) {
        return keycloakClient
                .realm(keycloakRealm)
                .users()
                .get(userId);
    }
}
