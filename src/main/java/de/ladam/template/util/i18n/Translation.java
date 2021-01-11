package de.ladam.template.util.i18n;

import static java.lang.System.setProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;

import de.ladam.template.util.application.ApplicationLogger;

/**
 * <p>
 * The class implements the com.vaadin.flow.i18n.I18NProvider.
 * </p>
 * <p>
 * The basic benefit of internationalization (I18N) is to be able to switch
 * languages at runtime.
 * </p>
 * <p>
 * The provider is mapping {@link I18NResourceBundle}s which stores the
 * translation to a(ny) key in one language. The provider class is linked to the
 * system property "vaadin.i18n.provider" so that the build in framework method
 * getTranslation() is calling this instance automatically (setup() called in an
 * implementation of {@link VaadinServiceInitListener}), so there is no need to
 * create an instance in each other class. The provided locals have to be
 * static.
 * <p>
 * <p>
 * The translation files are automatically synchronized with the enum {@link TranslationKey} at application startup.
 * </p>
 * <p>
 * <b>How to use:</b>
 * </p>
 * <p>
 * To use internalization in a meaningful way, views should implement the
 * interface {@link LocaleChangeObserver}. Vaadin components have already
 * implemented the getTranslation() method.
 * </p>
 * <p>
 * <code>VaadinService.getCurrent().getInstantiator().getI18NProvider().getTranslation(key, locale, params);</code>
 * </p>
 * <p>
 * To simplify the workflow, you can also call the getTranslation() method via
 * the {@link TranslationKey} enum.
 * </p>
 * <p>
 * <code>TranslationKey.KEY_NAME.getTranslation(locale, params);</code>
 * </p>
 * 
 * @author lam
 *
 */
public class Translation implements I18NProvider {
	private static final long serialVersionUID = -4194904582307869399L;

	private static final Map<String, I18NResourceBundle> resources = new HashMap<>();
	private static final Locale[] locals = { Locale.GERMAN, Locale.ENGLISH };
	private static final Locale defaultLocale = Locale.ENGLISH;

	private static boolean settedUp = false;

	public static void loadResources() {

		for (Locale locale : locals) {
			I18NResourceBundle RESOURCE_BUNDLE = new I18NResourceBundle(locale);
			if (RESOURCE_BUNDLE != null)
				if (!RESOURCE_BUNDLE.keySet().isEmpty())
					resources.put(locale.getISO3Language(), RESOURCE_BUNDLE);
		}

	}

	public Translation() {
	}

	@Override
	public List<Locale> getProvidedLocales() {
		return Arrays.asList(locals);
	}

	@Override
	public String getTranslation(String key, Locale locale, Object... params) {

		if (!settedUp) {
			setup();
		}

		I18NResourceBundle resourceBundle = resources.get(defaultLocale.getISO3Language());

		if (resources.containsKey(locale.getISO3Language())) {
			resourceBundle = resources.get(locale.getISO3Language());
		} else {
			ApplicationLogger.warn("missing i18n resource (i18n) " + locale.getISO3Language());
		}

		if (resourceBundle == null) {
			ApplicationLogger.error("missing default i18n resource");
			return "missing default i18n resource";
		}

		if (!resourceBundle.containsKey(key)) {
			ApplicationLogger.warn("missing resource key (i18n value) " + key);
			return key + " - " + locale;
		} else {
			return String.format(resourceBundle.getLocale(), resourceBundle.getString(key), params);
		}

	}

	public static void setup() {
		if (!settedUp) {
			ApplicationLogger.debug("Translation Setup...");
			loadResources();
			syncFiles();
			settedUp = true;
			setProperty("vaadin.i18n.provider", Translation.class.getName());
		} else {
			ApplicationLogger.debug("Translation Setup called, but it was already setted up.");
		}
	}

	/**
	 * The cast to this class should be possible.
	 * 
	 * @return I18NProvider
	 */
	public static I18NProvider getI18nProvider() {
		return VaadinService.getCurrent().getInstantiator().getI18NProvider();
	}

	/**
	 * UI.getCurrent().setLocale(L);
	 * 
	 * @param Locale to set
	 */
	public static void setUILocale(Locale L) {
		UI.getCurrent().setLocale(L);
		LocaleCookie.set(L);
	}

	/**
	 * UI.getCurrent().getLocale();
	 * 
	 * @return Locale
	 */
	public static Locale getUILocale() {
		return UI.getCurrent().getLocale();
	}

	public static Locale getLocaleByISO3Code(String iso3code) {
		if (iso3code != null)
			for (Locale locale : locals) {
				if (locale.getISO3Language().equals(iso3code))
					return locale;
			}
		return Locale.ENGLISH;
	}

	public static ComboBox<Locale> createLocaleComboBox() {
		ComboBox<Locale> locale_comboBox = new ComboBox<Locale>();
		locale_comboBox.setItems(getI18nProvider().getProvidedLocales());
		locale_comboBox.setItemLabelGenerator(loc -> loc.getDisplayLanguage());
		locale_comboBox.setPreventInvalidInput(true);
		locale_comboBox.setLabel("Locale");
		return locale_comboBox;
	}

	public static void syncFiles() {

		for (Locale locale : locals) {

			Path p = Paths.get("src\\main\\resources\\translations",
					String.format("catering_translation_%s.properties", locale.getISO3Language()));

			syncFile(p, locale);

		}

	}

	private static void syncFile(Path path, Locale locale) {

		boolean synced = false;

		if (Files.exists(path)) {

			List<TranslationKey> toWrite = buildList(resources.get(locale.getISO3Language()));
			String content = buildString(toWrite, locale);
			if (!content.isEmpty()) {
				ApplicationLogger.info(String.format("syncing property file \"%s\" with %d new keys", path.toString(),
						toWrite.size()));
				try {

					Files.write(path, content.getBytes(), StandardOpenOption.APPEND);
					synced = true;

				} catch (IOException e) {
					e.printStackTrace();
					ApplicationLogger.error(e, "could not append propertie file " + path.toString());
				}
			}

		} else {

			List<TranslationKey> toWrite = buildList(resources.get(locale.getISO3Language()));
			String content = buildString(toWrite, locale);
			if (!content.isEmpty()) {
				ApplicationLogger.info(String.format("syncing property file \"%s\" with %d new keys", path.toString(),
						toWrite.size()));
				try {

					Files.write(path, content.getBytes());
					synced = true;

				} catch (IOException e) {
					e.printStackTrace();
					ApplicationLogger.error(e, "could not append propertie file " + path.toString());
				}
			}

		}

		if (synced)
			loadResources();

	}

	private static List<TranslationKey> buildList(ResourceBundle resourceBundle) {
		List<TranslationKey> keys = new ArrayList<>(Arrays.asList(TranslationKey.values()));

		if (resourceBundle == null) {
			return keys;
		}

		List<TranslationKey> toRemove = new ArrayList<>();
		for (TranslationKey translationKey : keys) {
			if (resourceBundle.containsKey(translationKey.toString())) {
				toRemove.add(translationKey);
			}
		}

		try {

			keys.removeAll(toRemove);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return keys;
	}

	private static String buildString(List<TranslationKey> keys, Locale locale) {
		String content = "";
		for (TranslationKey translationKey : keys) {
			content += String.format("%1$s = %2$s translation key %1$s not setted\r\n", translationKey.toString(),
					locale.getISO3Language());
		}
		return content;
	}

}
