package de.ladam.template.ui.error;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;

public class ClassNotFoundExceptionView extends VerticalLayout implements HasErrorParameter<ClassNotFoundException> {

	private Span explanation;

	public ClassNotFoundExceptionView() {
		H1 header = new H1("Class not found");
		add(header);

		explanation = new Span();
		add(explanation);
	}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<ClassNotFoundException> parameter) {
		explanation.setText(parameter.getException().getMessage());
		return HttpServletResponse.SC_FORBIDDEN;
	}

}
