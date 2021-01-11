package de.ladam.template.util.i18n;

import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * The ISO3 language is a the letter abbreviation like "eng".<br>
 * Filepattern: catering_translation_XXX.properties
 * 
 * @author lam
 *
 */
public class I18NResourceBundle extends ResourceBundle {

	private Map<String, String> resources = new HashMap<>();
	private Locale locale;
	/**
	 * fullpath "src/main/resources/translations/"
	 */
	private final static String direction = "translations/";

	public I18NResourceBundle(Locale locale) {
		this.locale = locale;

		String path = direction + "catering_translation_" + locale.getISO3Language() + ".properties";

		try {
			Properties properties = new Properties();
			InputStream is = I18NResourceBundle.class.getClassLoader().getResourceAsStream(path);
			if (is == null)
				return;
			properties.load(is);
			for (String key : properties.stringPropertyNames()) {
				String value = properties.getProperty(key);
				resources.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected Object handleGetObject(String key) {
		return resources.get(key);
	}

	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(resources.keySet());
	}

	public Locale getLocale() {
		return locale;
	}

}
