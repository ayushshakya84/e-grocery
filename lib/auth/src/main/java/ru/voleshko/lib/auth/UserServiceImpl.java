package ru.voleshko.lib.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ObjectMapper objectMapper;
    private static final String BEARER = "Bearer";
    private final Base64 decoder = new Base64();

    @Override
    public User getUserAuthentication() {
        Optional<String> token =
                ofNullable(
                        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                )
                .map(ServletRequestAttributes::getRequest)
                .map(req -> req.getHeader(HttpHeaders.AUTHORIZATION))
                .map(Object::toString)
                .map(getTokenPayload());

        if (token.isEmpty()) {
            throw new UnauthorizedException("Authorization error: token was not provided");
        }

        return userFromToken(token.get());
    }

    @Override
    public UUID getUserId() {
        return UUID.fromString(getUserAuthentication().getId());
    }

    private Function<String, String> getTokenPayload() {
        return token -> new String(decoder.decode(
                token.replace(BEARER, "")
                        .trim()
                        .split("\\.")[1])
        );
    }

    @SneakyThrows
    private User userFromToken(String token) {
        JsonNode tree = objectMapper.readTree(token);
        return new User(
                asTextOrNull(tree.get("sub")),
                asTextOrNull(tree.get("given_name")),
                asTextOrNull(tree.get("family_name")),
                asTextOrNull(tree.get("email")),
                parseRoles(tree)
        );
    }

    private List<String> parseRoles(JsonNode tree) {
        JsonNode rolesNode = tree.get("resource_access").get("account").get("roles");
        if (rolesNode.isArray()) {
            return StreamSupport.stream(rolesNode.spliterator(), false)
                    .map(JsonNode::asText)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String asTextOrNull(JsonNode node) {
        return ofNullable(node).map(JsonNode::asText).orElse(null);
    }
}
