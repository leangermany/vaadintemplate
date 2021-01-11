package de.ladam.template.util.application;

import javax.servlet.http.Cookie;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.dom.ThemeList;

import de.ladam.template.authentication.User;
import de.ladam.template.util.common.CookieHelper;
import de.ladam.template.util.common.ExtendedList;
import de.ladam.template.util.common.CookieHelper.CookieName;

public class ApplicationTheme {

	public static void setTheme() {
		ThemeName sessionName = getCurrentTheme();
		setTheme(sessionName);
	}

	public static void setTheme(ThemeName themeName) {
		if (themeName == null) {
			ThemeName c = getCookieTheme();
			if (c != null) {
				themeName = c;
			}
		}
		if (themeName == null) {
			themeName = ThemeName.Light;
		}
		ThemeList themes = UI.getCurrent().getElement().getThemeList();
		if (themes.isEmpty() || !themes.contains(themeName.getName())) {
			themes.removeIf(o -> true);
			themes.add(themeName.getName());
		}
		set(themeName);
	}

	public static ThemeName getCurrentTheme() {
		if (User.isPresent()) {
			return getUserTheme();
		} else {
			ThemeName currT = getCookieTheme();
			return currT != null ? currT : ThemeName.Light;
		}
	}

	public static ThemeName getUserTheme() {
		return User.getCurrent().getThemeName();
	}

	public static ThemeName getCookieTheme() {
		Cookie themeC = CookieHelper.getCookieByName(CookieName.Theme.get());
		if (themeC != null && themeC.getValue() != null) {
			return get(themeC.getValue());
		}
		return null;
	}

	public static ThemeName get(String name) {
		return ExtendedList.asExtendedList(ThemeName.values()).Where(o -> o.getName().equalsIgnoreCase(name))
				.SingleOrDefault();
	}

	private static void set(ThemeName themeName) {
		if (themeName != null) {
			User.getCurrent().setThemeName(themeName);
			CookieHelper.setCookie(CookieName.Theme.get(), themeName.getName(), CookieHelper.cookieAge_OneYear);
		}
	}

	public static enum ThemeName {
		Light("light"), Light_Gold("light-gold"), Dark("dark"), Small_Font("small"), Crazy("crazy");

		private final String name;

		private ThemeName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

}
