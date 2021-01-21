package de.ladam.template.util.i18n;

import java.util.Locale;

import com.vaadin.flow.component.UI;

/**
 * see {@link Translation}
 * 
 * @author ladam
 *
 */
public enum TranslationKey {
	PLACEHOLDER_TEXT,
	/**
	 * general Buttons
	 */
	OK_BUTTON, YES_BUTTON, NO_BUTTON, BACK_BUTTON, ABORT_BUTTON, SAVE_BUTTON, REFRESH_BUTTON, CLOSE_BUTTON,
	DELETE_BUTTON, LOGOUT_BUTTON,
	/**
	 * general Notifocations
	 */
	NOTI_FATAL_ERROR_TITLE, NOTI_FATAL_ERROR_DESC,
	/**
	 * Login
	 */
	LOGIN_TITEL, LOGIN_SUBTITEL, LOGIN_FIELD_CAPTION, LOGIN_FIELD_USERNAME, LOGIN_FIELD_PASSWORD, LOGIN_BUTTON,
	LOGIN_FAIL_NOTI, NOTI_LOGOUT_DIALOG_QUESTION, LOGIN_HEADER_CAPTION, LOGIN_HEADER_TEXT, LOGIN_HINT,
	/**
	 * AppDrawerTabs
	 */
	DRAWER_TAB_INVENTORY, DRAWER_TAB_ABOUT, DRAWER_TAB_ADMIN,
	/**
	 * Settings Window
	 */
	SETTINGS_TITLE, SETTINGS_LOCALE_TITLE, SETTINGS_LOCALE_INFO_SPAN, SETTINGS_WINDOW_LOCALE_SET_BUTTON, SETTINGS_THEME_LABEL,
	/*
	 * AboutView
	 */
	ABOUT_INFO_PARAMS, ABOUT_GITHUB,
	/*
	 * 
	 */
	ELEMENT_TITLE,
	/*
	 * Admin -> TabPermissionManagement
	 */
	ADMIN_PERMISSION_CAPTION, ADMIN_PERMISSION_GRID_TABS, ADMIN_PERMISSION_GRID_GROUPS, ADMIN_PERMISSION_INFO,
	/**
	 * VaadinUpload
	 */
	VAADIN_UPLOAD_BUTTON, VAADIN_UPLOAD_LABEL;

	public String getTranslation(Object... params) {
		return getTranslation(UI.getCurrent().getLocale(), params);
	}

	public String getTranslation(Locale locale, Object... params) {
		return Translation.getI18nProvider().getTranslation(this.toString(), locale, params);
	}

}
