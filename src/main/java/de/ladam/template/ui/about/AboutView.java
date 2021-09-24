package de.ladam.template.ui.about;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Version;

import de.ladam.template.ui.AppDrawer;
import de.ladam.template.util.i18n.TranslationKey;
import de.ladam.template.util.interfaces.PermissionTab;

@Route(value = "about", layout = AppDrawer.class)
public class AboutView extends VerticalLayout implements HasDynamicTitle, LocaleChangeObserver {

	private final Span info = new Span();
	private final Span github = new Span();

	public AboutView() {
		add(VaadinIcon.INFO_CIRCLE.create());
		add(info, github);
		setSizeFull();
		setJustifyContentMode(JustifyContentMode.CENTER);
		setAlignItems(Alignment.CENTER);

	}

	private void setText() {
		info.setText(TranslationKey.ABOUT_INFO_PARAMS.getTranslation(Version.getFullVersion()));
		github.setText(TranslationKey.ABOUT_GITHUB.getTranslation());
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		setText();
	}

	@Override
	public String getPageTitle() {
		return getTranslation(TranslationKey.DRAWER_TAB_ABOUT.toString());
	}
}
