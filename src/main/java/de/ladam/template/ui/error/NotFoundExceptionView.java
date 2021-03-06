package de.ladam.template.ui.error;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.ParentLayout;

import de.ladam.template.authentication.AccessControl;
import de.ladam.template.authentication.AccessControlFactory;
import de.ladam.template.ui.AppDrawer;
import de.ladam.template.ui.login.LoginScreen;

import javax.servlet.http.HttpServletResponse;

/**
 * View shown when trying to navigate to a view that does not exist using
 * 
 * @author vaadin
 */
@ParentLayout(AppDrawer.class)
public class NotFoundExceptionView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    private Span explanation;

    public NotFoundExceptionView() {
        H1 header = new H1("The view could not be found.");
        add(header);

        explanation = new Span();
        add(explanation);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        final AccessControl accessControl = AccessControlFactory
                .getInstance().createAccessControl();
        if (!accessControl.isUserSignedIn()) {
            event.rerouteTo(LoginScreen.class);
            return HttpServletResponse.SC_FORBIDDEN;
        }
        explanation.setText("Could not navigate to '"
                + event.getLocation().getPath() + "'.");
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
