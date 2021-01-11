package de.ladam.template;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

import de.ladam.template.authentication.AccessControl;
import de.ladam.template.authentication.AccessControlFactory;
import de.ladam.template.ui.login.LoginScreen;
import de.ladam.template.util.application.ApplicationTheme;
import de.ladam.template.util.i18n.LocaleCookie;
import de.ladam.template.util.i18n.Translation;

/**
 * This class is used to listen to BeforeEnter event of all UIs in order to
 * check whether a user is signed in or not before allowing entering any page.
 * It is registered in a file named
 * com.vaadin.flow.server.TemplateInitListener in META-INF/services.
 */
public class TemplateInitListener implements VaadinServiceInitListener {
	@Override
	public void serviceInit(ServiceInitEvent initEvent) {
		final AccessControl accessControl = AccessControlFactory.getInstance().createAccessControl();

		initEvent.getSource().addUIInitListener(uiInitEvent -> {
			uiInitEvent.getUI().addBeforeEnterListener(enterEvent -> {
				if (!accessControl.isUserSignedIn() && !LoginScreen.class.equals(enterEvent.getNavigationTarget())) {
					enterEvent.rerouteTo(LoginScreen.class);
				}
				ApplicationTheme.setTheme();
			});
			uiInitEvent.getUI().setLocale(LocaleCookie.get());
		});

		Translation.setup();
	}
}
