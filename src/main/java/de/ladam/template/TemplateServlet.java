package de.ladam.template;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.flow.server.InitParameters;
import com.vaadin.flow.server.VaadinServlet;

/**
 * 
 * <p>https://vaadin.com/docs/flow/advanced/tutorial-all-vaadin-properties.html</p>
 * 
 * @author leand
 *
 */
@WebServlet(value = "/*", asyncSupported = true, initParams = {
		@WebInitParam(name = InitParameters.I18N_PROVIDER, value = "de.ladam.template.util.i18n.Translation") })
public class TemplateServlet extends VaadinServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2842686896839213084L;

}