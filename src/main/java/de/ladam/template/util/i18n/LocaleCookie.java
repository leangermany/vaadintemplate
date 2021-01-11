package de.ladam.template.util.i18n;

import java.util.Locale;

import javax.servlet.http.Cookie;

import de.ladam.template.util.common.CookieHelper;
import de.ladam.template.util.common.CookieHelper.CookieName;

public class LocaleCookie {

	private LocaleCookie() {
	}

	public static void set(Locale locale) {
		CookieHelper.setCookie(CookieName.Locale.get(), locale.getISO3Language(), CookieHelper.cookieAge_OneYear);
	}

	public static Locale get() {
		Cookie themeC = CookieHelper.getCookieByName(CookieName.Locale.get());
		if (themeC != null && themeC.getValue() != null) {
			final Locale[] locals = { Locale.GERMAN, Locale.ENGLISH };
			for (Locale locale : locals) {
				if (locale.getISO3Language().equals(themeC.getValue()))
					return locale;
			}
		}
		return Locale.ENGLISH;
	}

}
