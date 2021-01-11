package de.ladam.template.ui.login;

import java.util.Locale;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.ladam.template.authentication.AccessControl;
import de.ladam.template.authentication.AccessControlFactory;
import de.ladam.template.util.i18n.TranslationKey;

/**
 * UI content when the user is not logged in yet.
 * 
 * @author vaadin
 */
@Route("Login")
@PageTitle("Login")
@CssImport("./styles/shared-styles.css")
public class LoginScreen extends FlexLayout implements LocaleChangeObserver {

	private AccessControl accessControl;
	LoginForm loginForm = new LoginForm();

	H1 loginInfoHeader = new H1("Login Information");
	Span loginInfoText = new Span("Log in as \"admin\" to have full access. Log in with any "
			+ "other username to have read-only access. For all users, the password is same as the username.");

	public LoginScreen() {
		accessControl = AccessControlFactory.getInstance().createAccessControl();
		buildUI();
	}

	private void buildUI() {
		setSizeFull();
		setClassName("login-screen");

		// login form, centered in the available part of the screen
		loginForm.addLoginListener(this::login);
		loginForm.addForgotPasswordListener(event -> Notification.show(TranslationKey.LOGIN_HINT.getTranslation()));

		// layout to center login form when there is sufficient screen space
		FlexLayout centeringLayout = new FlexLayout();
		centeringLayout.setSizeFull();
		centeringLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		centeringLayout.setAlignItems(Alignment.CENTER);
		centeringLayout.add(loginForm);

		// information text about logging in
		Component loginInformation = buildLoginInformation();

		add(loginInformation);
		add(centeringLayout);

		setText();
		setLoginI18N();
	}

	private Component buildLoginInformation() {
		VerticalLayout loginInformation = new VerticalLayout();
		loginInformation.setClassName("login-information");

		loginInfoHeader.setWidth("100%");
		loginInfoText.setWidth("100%");
		loginInformation.add(loginInfoHeader);
		loginInformation.add(loginInfoText);

		return loginInformation;
	}

	private void login(LoginForm.LoginEvent event) {
		if (accessControl.signIn(event.getUsername(), event.getPassword())) {
			getUI().get().navigate("");
		} else {
			event.getSource().setError(true);
		}
	}

	private void setText() {
		loginInfoHeader.setText(TranslationKey.LOGIN_HEADER_CAPTION.getTranslation());
		loginInfoText.setText(TranslationKey.LOGIN_HEADER_TEXT.getTranslation());
	}

	private void setLoginI18N() {
		if (UI.getCurrent().getLocale().equals(Locale.GERMAN)) {
			// TODO implement TranslationKeys
			LoginI18n login18_german = LoginI18n.createDefault();
			login18_german.getForm().setTitle("Einloggen");
			login18_german.getForm().setUsername("Benutzername");
			login18_german.getForm().setPassword("Passwort");
			login18_german.getForm().setSubmit("Los");
			login18_german.getForm().setForgotPassword("Passwort vergessen?");
			login18_german.getErrorMessage().setTitle("Benutzername oder Passwort falsch");
			login18_german.getErrorMessage()
					.setMessage("Bitte überprüfen Sie Ihre Eingaben und probieren es noch einmal.");
			login18_german.setAdditionalInformation("Extra Infos...");
			loginForm.setI18n(login18_german);
		} else {
			loginForm.setI18n(LoginI18n.createDefault());
		}
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		setText();
		setLoginI18N();
	}
}
