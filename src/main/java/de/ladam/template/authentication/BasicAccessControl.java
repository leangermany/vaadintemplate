package de.ladam.template.authentication;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import de.ladam.template.viewmodels.RoleVM;
import de.ladam.template.viewmodels.SessionUserVM;

/**
 * Default mock implementation of {@link AccessControl}. This implementation
 * accepts any string as a user if the password is the same string, and
 * considers the user "admin" as the only administrator.
 * 
 * @author vaadin
 */
public class BasicAccessControl implements AccessControl {

	@Override
	public boolean signIn(String username, String password) {
		if (username == null || username.isEmpty()) {
			return false;
		}

		if (!username.equals(password)) {
			return false;
		}

		SessionUserVM user = new SessionUserVM();
		user.setName(username);
		user.setValidated(true);
		templateRoles(user);
		User.setCurrent(user);
		return true;
	}

	@Deprecated
	private void templateRoles(SessionUserVM sessionUserVM) {
		sessionUserVM.addRole(Role.getRole(1));
		if (sessionUserVM.getName().toLowerCase().equals("admin")) {
			sessionUserVM.addRole(Role.getRole(2));
		}
	}

	@Override
	public boolean isUserSignedIn() {
		return User.getCurrent().isValidated();
	}

	@Override
	public boolean isUserInRole(RoleVM role) {
		SessionUserVM user = User.getCurrent();
		return user.getRoles().contains(role);
	}

	@Override
	public String getPrincipalName() {
		return User.getCurrent().getName();
	}

	@Override
	public void signOut() {
		VaadinSession.getCurrent().getSession().invalidate();
		UI.getCurrent().navigate("");
	}
}
