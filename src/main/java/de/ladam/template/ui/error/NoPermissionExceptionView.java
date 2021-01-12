package de.ladam.template.ui.error;

import javax.naming.NoPermissionException;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;

import de.ladam.template.authentication.AccessControl;
import de.ladam.template.authentication.AccessControlFactory;
import de.ladam.template.ui.login.LoginScreen;

public class NoPermissionExceptionView extends VerticalLayout implements HasErrorParameter<NoPermissionException> {

	private Span explanation;

	public NoPermissionExceptionView() {
		H1 header = new H1("No Permission");
		add(header);

		explanation = new Span();
		add(explanation);
	}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NoPermissionException> parameter) {
		final AccessControl accessControl = AccessControlFactory.getInstance().createAccessControl();
		if (!accessControl.isUserSignedIn()) {
			event.rerouteTo(LoginScreen.class);
			return HttpServletResponse.SC_FORBIDDEN;
		}
		explanation.setText(
				"You don't have the permission the access this route: '" + event.getLocation().getPath() + "'.");
		return HttpServletResponse.SC_FORBIDDEN;
	}

}
