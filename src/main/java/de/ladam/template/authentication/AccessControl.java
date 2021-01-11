package de.ladam.template.authentication;

import java.io.Serializable;

import de.ladam.template.viewmodels.RoleVM;

/**
 * Simple interface for authentication and authorization checks.
 */
public interface AccessControl extends Serializable {

    String ADMIN_ROLE_NAME = "admin";
    String ADMIN_USERNAME = "admin";

    boolean signIn(String username, String password);

    boolean isUserSignedIn();

    boolean isUserInRole(RoleVM role);

    String getPrincipalName();

    void signOut();
}
