package de.ladam.template.util.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

public class CookieHelper {

	public static int cookieAge_OneYear = 31556952;

	public static Cookie getCookieByName(String name) {
		Cookie sesseioncookie = null;
		try {
			HttpServletRequest request = (HttpServletRequest) VaadinRequest.getCurrent();
			Cookie[] cookies = request.getCookies();
			if (cookies != null)
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(name)) {
						sesseioncookie = cookie;
						break;
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
//			ApplicationError.LogAndMail(e, "Can not get cookie " + name, Level.WARN);
		}
		return sesseioncookie;
	}

	public static void setCookie(String name, String value, int maxAge) {
		try {
			Cookie c = new Cookie(name, value);
			c.setMaxAge(maxAge);
			c.setPath(VaadinService.getCurrentRequest().getContextPath());
			VaadinService.getCurrentResponse().addCookie(c);
		} catch (Exception e) {
			e.printStackTrace();
//			ApplicationError.LogAndMail(e, "Can not write cookie " + name, Level.WARN);
		}
	}

	public static enum CookieName {

		Theme("applicationtheme"), Locale("applicationlocale");

		private String value = "";

		private CookieName(String value) {
			this.value = value;
		}

		public String get() {
			return value;
		}

	}

}
