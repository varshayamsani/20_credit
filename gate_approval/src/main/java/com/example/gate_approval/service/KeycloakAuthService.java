package com.example.gate_approval.service;

import com.example.gate_approval.dto.KeycloakAuthResponse;
import com.example.gate_approval.dto.LoginUserDto;
import com.example.gate_approval.dto.RegisterUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.ws.rs.core.Response;
import jakarta.transaction.Transactional;

import java.util.Collections;
import java.util.*;
import java.util.Map;
import java.util.HashMap;
import java.security.Principal;
import org.springframework.context.ApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAuthService {

    private final Keycloak keycloakAdmin;
    private final RestTemplate restTemplate;
    private final ApplicationContext applicationContext;
    private final ObjectMapper objectMapper;

    @Value("${" + "keycloak.auth-server-url}")
    private String keycloakAuthServerUrl;

    @Value("${" + "keycloak.realm}")
    private String realm;

    @Value("${" + "keycloak.client-id}")
    private String clientId;

    @Value("${" + "keycloak.client-secret}")
    private String clientSecret;

    @Value("${" + "keycloak.default-role}")
    private String defaultRole;

    // -------------------------------------------------------------------------
    // ADD USER DYNAMICALLY
    // -------------------------------------------------------------------------
/**
     *Add user (/add-user) - user added by admin
     *
     */
    public String addUser(com.example.gate_approval.dto.UserResource userResource) {
        Map<String,Object> resourceMap = userResource.getResourceMap();
        Map<String,Object> authMap = userResource.getAuthMap();
        String resourceName = userResource.getResourceName();
        
        Object repository = null;
        Object savedEntity = null;

        try {
            // 1. Get the Entity Class dynamically
            String entityClassNameStr = "com.example.gate_approval.entity." + resourceName;
            Class<?> entityClass = Class.forName(entityClassNameStr);

            // 2. Convert Map to Entity
            Object entity = objectMapper.convertValue(resourceMap, entityClass);

            // 3. Find the Repository bean dynamically
            String repositoryBeanName = Character.toLowerCase(resourceName.charAt(0)) + resourceName.substring(1) + "Repository";
            repository = applicationContext.getBean(repositoryBeanName);

            // 4. Save the entity
            Method saveMethod = repository.getClass().getMethod("save", Object.class);
            savedEntity = saveMethod.invoke(repository, entity);

            // 5. Extract ID
            Method getIdMethod = savedEntity.getClass().getMethod("getId");
            Object idValue = getIdMethod.invoke(savedEntity);
            String customId = String.valueOf(idValue);

            // 6. Create User in Keycloak
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setEmail((String) authMap.get("email"));
            user.setUsername((String) authMap.get("userName"));
            user.setFirstName((String) authMap.get("firstName"));
            user.setLastName((String) authMap.get("lastName"));
            
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue((String) authMap.get("password"));
            user.setCredentials(Collections.singletonList(credential));

            // Set custom attributes
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("custom_id", Collections.singletonList(customId));
            attributes.put("resource_type", Collections.singletonList(resourceName));
            user.setAttributes(attributes);

            RealmResource realmResource = keycloakAdmin.realm(realm);
            Response response = realmResource.users().create(user);

            if (response.getStatus() == 409) {
                // Conflict, user already exists -> rollback DB save
                Method deleteMethod = repository.getClass().getMethod("delete", Object.class);
                deleteMethod.invoke(repository, savedEntity);
                return "User already exists";
            }

            if (response.getStatus() != 201) {
                // Other failure -> rollback DB save
                Method deleteMethod = repository.getClass().getMethod("delete", Object.class);
                deleteMethod.invoke(repository, savedEntity);
                String body = response.readEntity(String.class);
                throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus() + " – " + body);
            }

            // Successfully created user
            String location = response.getHeaderString("Location");
            String keycloakUserId = location.substring(location.lastIndexOf("/") + 1);
            log.info("Created Keycloak user with ID: {} and custom_id: {}", keycloakUserId, customId);

            // 7. Assign default role
            if (defaultRole != null && !defaultRole.trim().isEmpty()) {
                try {
                    RoleRepresentation defaultRoleRep = realmResource.roles().get(defaultRole).toRepresentation();
                    realmResource.users().get(keycloakUserId).roles().realmLevel()
                            .add(Collections.singletonList(defaultRoleRep));
                    log.info("Assigned default role '{}' to new user {} (keycloakId: {})", defaultRole, authMap.get("userName"), keycloakUserId);
                } catch (Exception roleEx) {
                    log.error("Could not assign default role '{}' to user {}: {}", defaultRole, keycloakUserId, roleEx.getMessage());
                    // We don't necessarily rollback for role assignment failure if the user was created,
                    // but we log it as an error.
                }
            } else {
                log.warn("No default role configured, skipping role assignment for user {}", keycloakUserId);
            }

            return "User created successfully";

        } catch (Exception e) {
            log.error("Unexpected error in addUser", e);
            if (repository != null && savedEntity != null) {
                try {
                    Method deleteMethod = repository.getClass().getMethod("delete", Object.class);
                    deleteMethod.invoke(repository, savedEntity);
                } catch (Exception rollbackEx) {
                    log.error("Failed to rollback entity after User creation failure", rollbackEx);
                }
            }
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    // -------------------------------------------------------------------------
    // REGISTER
    // -------------------------------------------------------------------------

    /**
     *User self register (/register)
     *
     */
        public String register(com.example.gate_approval.dto.UserResource userResource) {
            Map<String,Object> resourceMap = userResource.getResourceMap();
            Map<String,Object> authMap = userResource.getAuthMap();
            String resourceName = userResource.getResourceName();

            Object repository = null;
            Object savedEntity = null;

            try {
                // 1. Get the Entity Class dynamically
                String entityClassNameStr = "com.example.gate_approval.entity." + resourceName;
                Class<?> entityClass = Class.forName(entityClassNameStr);

                // 2. Convert Map to Entity
                Object entity = objectMapper.convertValue(resourceMap, entityClass);

                // 3. Find the Repository bean dynamically
                String repositoryBeanName = Character.toLowerCase(resourceName.charAt(0)) + resourceName.substring(1) + "Repository";
                repository = applicationContext.getBean(repositoryBeanName);

                // 4. Save the entity
                Method saveMethod = repository.getClass().getMethod("save", Object.class);
                savedEntity = saveMethod.invoke(repository, entity);

                // 5. Extract ID
                Method getIdMethod = savedEntity.getClass().getMethod("getId");
                Object idValue = getIdMethod.invoke(savedEntity);
                String customId = String.valueOf(idValue);

                // 6. Create User in Keycloak
                UserRepresentation user = new UserRepresentation();
                user.setEnabled(true);
                user.setEmail((String) authMap.get("email"));
                user.setUsername((String) authMap.get("userName"));
                user.setFirstName((String) authMap.get("firstName"));
                user.setLastName((String) authMap.get("lastName"));

                CredentialRepresentation credential = new CredentialRepresentation();
                credential.setTemporary(false);
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue((String) authMap.get("password"));
                user.setCredentials(Collections.singletonList(credential));

                // Set custom attributes
                Map<String, List<String>> attributes = new HashMap<>();
                attributes.put("custom_id", Collections.singletonList(customId));
                attributes.put("resource_type", Collections.singletonList(resourceName));
                user.setAttributes(attributes);

                RealmResource realmResource = keycloakAdmin.realm(realm);
                Response response = realmResource.users().create(user);

                if (response.getStatus() == 409) {
                    // Conflict, user already exists -> rollback DB save
                    Method deleteMethod = repository.getClass().getMethod("delete", Object.class);
                    deleteMethod.invoke(repository, savedEntity);
                    return "User already exists";
                }

                if (response.getStatus() != 201) {
                    // Other failure -> rollback DB save
                    Method deleteMethod = repository.getClass().getMethod("delete", Object.class);
                    deleteMethod.invoke(repository, savedEntity);
                    String body = response.readEntity(String.class);
                    throw new RuntimeException("Failed to create user in Keycloak. Status: " + response.getStatus() + " – " + body);
                }

                // Successfully created user
                String location = response.getHeaderString("Location");
                String keycloakUserId = location.substring(location.lastIndexOf("/") + 1);
                log.info("Created Keycloak user with ID: {} and custom_id: {}", keycloakUserId, customId);

                // 7. Assign default role
                if (defaultRole != null && !defaultRole.trim().isEmpty()) {
                    try {
                        RoleRepresentation defaultRoleRep = realmResource.roles().get(defaultRole).toRepresentation();
                        realmResource.users().get(keycloakUserId).roles().realmLevel()
                                .add(Collections.singletonList(defaultRoleRep));
                        log.info("Assigned default role '{}' to new user {} (keycloakId: {})", defaultRole, authMap.get("userName"), keycloakUserId);
                    } catch (Exception roleEx) {
                        log.error("Could not assign default role '{}' to user {}: {}", defaultRole, keycloakUserId, roleEx.getMessage());
                        // We don't necessarily rollback for role assignment failure if the user was created,
                        // but we log it as an error.
                    }
                } else {
                    log.warn("No default role configured, skipping role assignment for user {}", keycloakUserId);
                }

                return "User created successfully";

            } catch (Exception e) {
                log.error("Unexpected error in addUser", e);
                if (repository != null && savedEntity != null) {
                    try {
                        Method deleteMethod = repository.getClass().getMethod("delete", Object.class);
                        deleteMethod.invoke(repository, savedEntity);
                    } catch (Exception rollbackEx) {
                        log.error("Failed to rollback entity after User creation failure", rollbackEx);
                    }
                }
                throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
            }
        }

    // -------------------------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------------------------

    /**
     * Authenticates the user against Keycloak using the Resource Owner Password
     * Credentials (ROPC) grant and returns the token response.
     */
    public KeycloakAuthResponse login(LoginUserDto dto) {
        return fetchToken(dto.getUserName(), dto.getPassword());
    }

    // -------------------------------------------------------------------------
    // LOGOUT
    // -------------------------------------------------------------------------

    /**
     * Revokes the user session in Keycloak by calling the token revocation endpoint.
     *
     * @param authHeader the raw "Authorization: Bearer <token>" header value
     */
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String revokeUrl = keycloakAuthServerUrl
                + "/realms/" + realm
                + "/protocol/openid-connect/revoke";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("token", token);

        restTemplate.postForEntity(revokeUrl, new HttpEntity<>(form, headers), String.class);
        log.info("Token revoked successfully.");
    }

    // -------------------------------------------------------------------------
    // ASSIGN ROLE
    // -------------------------------------------------------------------------

    /**
     * Assigns a realm-level role to the user identified by email.
     *
     *
     * @param roleName name of the Keycloak realm role to assign
     */
   public void assignClientRole(String username,
                                String roleName) {

       RealmResource realmResource = keycloakAdmin.realm(realm);

       // Find user by username
       List<UserRepresentation> users =
               realmResource.users().search(username, true);

       if (users == null || users.isEmpty()) {
           throw new RuntimeException(
                   "User not found with username: " + username
           );
       }

       // Exact username match
       UserRepresentation user = users.stream()
               .filter(u -> username.equals(u.getUsername()))
               .findFirst()
               .orElseThrow(() ->
                       new RuntimeException(
                               "User not found with username: " + username
                       )
               );

       String userId = user.getId();

       // Find client
       List<ClientRepresentation> clients =
               realmResource.clients().findByClientId(clientId);

       if (clients == null || clients.isEmpty()) {
           throw new RuntimeException("Client not found: " + clientId);
       }

       ClientRepresentation client = clients.get(0);
       String clientUuid = client.getId();

       // Find client role
       RoleRepresentation clientRole;

       try {
           clientRole = realmResource.clients()
                   .get(clientUuid)
                   .roles()
                   .get(roleName)
                   .toRepresentation();

       } catch (Exception e) {
           throw new RuntimeException(
                   "Client role not found: " + roleName
           );
       }

       // Assign role
       UserResource userResource =
               realmResource.users().get(userId);

       userResource.roles()
               .clientLevel(clientUuid)
               .add(Collections.singletonList(clientRole));

       log.info(
               "Assigned role '{}' from client '{}' to user '{}'",
               roleName,
               clientId,
               username
       );
   }

    // -------------------------------------------------------------------------
    // INTERNAL HELPERS
    // -------------------------------------------------------------------------

    private KeycloakAuthResponse fetchToken(String username, String password) {
        String tokenUrl = keycloakAuthServerUrl
                + "/realms/" + realm
                + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("username", username);
        form.add("password", password);
        form.add("scope", "openid");

        ResponseEntity<KeycloakAuthResponse> resp = restTemplate.postForEntity(
                tokenUrl,
                new HttpEntity<>(form, headers),
                KeycloakAuthResponse.class
        );

        if (resp.getBody() == null) {
            throw new RuntimeException("Empty token response from Keycloak");
        }
        return resp.getBody();
    }
    public String getUserId(Principal principal) {
        Object actualPrincipal = principal;
        String userId = "";
        if (principal instanceof org.springframework.security.core.Authentication) {
            actualPrincipal = ((org.springframework.security.core.Authentication) principal).getPrincipal();
        }

        if (actualPrincipal instanceof org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal) {
            org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal oauth2Principal =
                    (org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal) actualPrincipal;

            // 1. Try finding by custom_id attribute first (direct mapping saved by KeycloakAuthService)
            Object customIdAttr = oauth2Principal.getAttribute("custom_id");

            if (customIdAttr != null) {
                userId = String.valueOf(customIdAttr);
            }
        }

        return userId;
    }


    @Transactional
    public List<String> addUsers(List<com.example.gate_approval.dto.UserResource> userResources) {

        List<String> results = new ArrayList<>();

        for (com.example.gate_approval.dto.UserResource userResource : userResources) {

            Map<String, Object> resourceMap = userResource.getResourceMap();
            Map<String, Object> authMap = userResource.getAuthMap();
            String resourceName = userResource.getResourceName();

            Object repository = null;
            Object savedEntity = null;

            try {

                // 1. Get Entity Class dynamically
                String entityClassNameStr =
                       "com.example.gate_approval.entity." + resourceName;

                Class<?> entityClass = Class.forName(entityClassNameStr);

                // 2. Convert Map to Entity
                Object entity =
                        objectMapper.convertValue(resourceMap, entityClass);

                // 3. Get Repository Bean dynamically
                String repositoryBeanName =
                        Character.toLowerCase(resourceName.charAt(0))
                                + resourceName.substring(1)
                                + "Repository";

                repository = applicationContext.getBean(repositoryBeanName);

                // 4. Save Entity
                Method saveMethod =
                        repository.getClass().getMethod("save", Object.class);

                savedEntity = saveMethod.invoke(repository, entity);

                // 5. Extract ID
                Method getIdMethod =
                        savedEntity.getClass().getMethod("getId");

                Object idValue = getIdMethod.invoke(savedEntity);

                String customId = String.valueOf(idValue);

                // =====================================
                // CREATE KEYCLOAK USER
                // =====================================

                String generatedPassword =
                        UUID.randomUUID().toString().substring(0, 8) + "@Xyz1";

                UserRepresentation user = new UserRepresentation();

                user.setEnabled(true);
                user.setEmail((String) authMap.get("email"));
                user.setUsername((String) authMap.get("userName"));
                user.setFirstName((String) authMap.get("firstName"));
                user.setLastName((String) authMap.get("lastName"));

                // Force password update
                user.setRequiredActions(
                        Collections.singletonList("UPDATE_PASSWORD"));

                CredentialRepresentation credential =
                        new CredentialRepresentation();

                credential.setTemporary(true);
                credential.setType(CredentialRepresentation.PASSWORD);
                credential.setValue(generatedPassword);

                user.setCredentials(
                        Collections.singletonList(credential));

                // Custom attributes
                Map<String, List<String>> attributes =
                        new HashMap<>();

                attributes.put(
                        "custom_id",
                        Collections.singletonList(customId));

                attributes.put(
                        "resource_type",
                        Collections.singletonList(resourceName));

                user.setAttributes(attributes);

                RealmResource realmResource =
                        keycloakAdmin.realm(realm);

                Response response =
                        realmResource.users().create(user);

                // =====================================
                // HANDLE DUPLICATE USER
                // =====================================

                if (response.getStatus() == 409) {

                    Method deleteMethod =
                            repository.getClass()
                                    .getMethod("delete", Object.class);

                    deleteMethod.invoke(repository, savedEntity);

                    results.add(
                            authMap.get("userName")
                                    + " -> User already exists");

                    continue;
                }

                // =====================================
                // HANDLE FAILURE
                // =====================================

                if (response.getStatus() != 201) {

                    Method deleteMethod =
                            repository.getClass()
                                    .getMethod("delete", Object.class);

                    deleteMethod.invoke(repository, savedEntity);

                    String body =
                            response.readEntity(String.class);

                    results.add(
                            authMap.get("userName")
                                    + " -> Failed : "
                                    + body);

                    continue;
                }

                // =====================================
                // SUCCESS
                // =====================================

                String location =
                        response.getHeaderString("Location");

                String keycloakUserId =
                        location.substring(location.lastIndexOf("/") + 1);

                log.info(
                        "Created Keycloak user with ID: {} and custom_id: {}",
                        keycloakUserId,
                        customId
                );

                // =====================================
                // ASSIGN DEFAULT ROLE
                // =====================================

                if (defaultRole != null && !defaultRole.trim().isEmpty()) {

                    try {

                        RoleRepresentation defaultRoleRep =
                                realmResource.roles()
                                        .get(defaultRole)
                                        .toRepresentation();

                        realmResource.users()
                                .get(keycloakUserId)
                                .roles()
                                .realmLevel()
                                .add(Collections.singletonList(defaultRoleRep));

                        log.info(
                                "Assigned role '{}' to user {}",
                                defaultRole,
                                authMap.get("userName")
                        );

                    } catch (Exception roleEx) {

                        log.error(
                                "Role assignment failed for user {}",
                                authMap.get("userName"),
                                roleEx
                        );
                    }

                } else {

                    log.warn(
                            "No default role configured for user {}",
                            authMap.get("userName")
                    );
                }


                results.add(
                        authMap.get("userName")
                                + " -> User created successfully"
                );

            } catch (Exception e) {

                log.error("Unexpected error in addUsers", e);

                // Rollback DB entity
                if (repository != null && savedEntity != null) {

                    try {

                        Method deleteMethod =
                                repository.getClass()
                                        .getMethod("delete", Object.class);

                        deleteMethod.invoke(repository, savedEntity);

                    } catch (Exception rollbackEx) {

                        log.error(
                                "Rollback failed",
                                rollbackEx
                        );
                    }
                }

                results.add(
                        authMap.get("userName")
                                + " -> Failed : "
                                + e.getMessage()
                );
            }
        }

        return results;
    }

}
