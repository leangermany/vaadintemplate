package de.ladam.template.authentication;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

import de.ladam.template.viewmodels.SessionUserVM;

/**
 * Class for retrieving and setting the name of the current user of the current
 * session (without using JAAS). All methods of this class require that a
 * {@link VaadinRequest} is bound to the current thread.
 * 
 * 
 * @see VaadinService#getCurrentRequest()
 */
public final class User {

	/**
	 * The attribute key used to store the username in the session.
	 */
	public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = SessionUserVM.class.getCanonicalName();

	private User() {
	}

	/**
	 * Returns the name of the current user stored in the current session, or an
	 * empty string if no user name is stored.
	 * 
	 * @throws IllegalStateException if the current session cannot be accessed.
	 */
	public static SessionUserVM getCurrent() {
		SessionUserVM currentUser = (SessionUserVM) getCurrentRequest().getWrappedSession()
				.getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
		if (currentUser == null) {
			return createUser();
		} else {
			return currentUser;
		}
	}

	/**
	 * Sets the name of the current user and stores it in the current session. Using
	 * a {@code null} username will remove the username from the session.
	 * 
	 * @throws IllegalStateException if the current session cannot be accessed.
	 */
	public static void setCurrent(SessionUserVM currentUser) {
		if (currentUser == null) {
			getCurrentRequest().getWrappedSession().removeAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
		} else {
			getCurrentRequest().getWrappedSession().setAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY, currentUser);
		}
	}

	private static VaadinRequest getCurrentRequest() {
		VaadinRequest request = VaadinService.getCurrentRequest();
		if (request == null) {
			throw new IllegalStateException("No request bound to current thread.");
		}
		return request;
	}

	public static boolean isPresent() {
		return getCurrentRequest().getWrappedSession().getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY) != null;
	}

	private static SessionUserVM createUser() {
		SessionUserVM vm = new SessionUserVM();
		setCurrent(vm);
		return vm;
	}
}
