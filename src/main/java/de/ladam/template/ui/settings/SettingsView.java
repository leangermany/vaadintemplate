package de.ladam.template.ui.settings;

import java.util.Locale;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.ladam.template.ui.AppDrawer;
import de.ladam.template.util.application.ApplicationTheme;
import de.ladam.template.util.application.ApplicationTheme.ThemeName;
import de.ladam.template.util.i18n.LocaleCookie;
import de.ladam.template.util.i18n.Translation;
import de.ladam.template.util.i18n.TranslationKey;

@Route(value = "settings", layout = AppDrawer.class)
public class SettingsView extends VerticalLayout implements HasDynamicTitle, LocaleChangeObserver {

	Select<ApplicationTheme.ThemeName> themenameComboBox = new Select<ApplicationTheme.ThemeName>();
	Select<Locale> localeComboBox = new Select<>();

	public SettingsView() {
		setSpacing(false);
		setAlignItems(FlexComponent.Alignment.STRETCH);
		add(createThemeSettings());
		add(createLocaleSettings());
	}

	private Component createThemeSettings() {
		themenameComboBox.setItems(ThemeName.values());
		themenameComboBox.setItemLabelGenerator(o -> o.toString());
		themenameComboBox.setValue(ApplicationTheme.getUserTheme());
		themenameComboBox.addValueChangeListener(e -> {
			ApplicationTheme.setTheme(e.getValue());
		});
		return themenameComboBox;
	}

	private Component createLocaleSettings() {
		localeComboBox.setItems(Translation.getI18nProvider().getProvidedLocales());
		localeComboBox.setItemLabelGenerator(o -> o.getDisplayLanguage(o));
		localeComboBox.setValue(Translation.getUILocale());
		localeComboBox.addValueChangeListener(e -> {
			Translation.setUILocale(e.getValue());
		});
		return localeComboBox;
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		themenameComboBox.setLabel(TranslationKey.SETTINGS_THEME_LABEL.getTranslation()); 
		localeComboBox.setLabel(TranslationKey.SETTINGS_LOCALE_TITLE.getTranslation());
	}

	@Override
	public String getPageTitle() {
		return getTranslation(TranslationKey.SETTINGS_TITLE.toString());
	}

}
