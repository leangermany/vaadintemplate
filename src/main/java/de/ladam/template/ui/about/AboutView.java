package de.ladam.template.ui.about;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Version;

import de.ladam.template.ui.AppDrawer;
import de.ladam.template.util.application.PermissionTab;
import de.ladam.template.util.i18n.TranslationKey;

@Route(value = "about", layout = AppDrawer.class)
@PermissionTab(key = "1df7a87e-0c9f-4343-bb23-4c3f74d165fa", name = "AboutView")
public class AboutView extends HorizontalLayout implements HasDynamicTitle, LocaleChangeObserver {

	private final Span info = new Span();

	public AboutView() {
		add(VaadinIcon.INFO_CIRCLE.create());
		add(info);
		setSizeFull();
		setJustifyContentMode(JustifyContentMode.CENTER);
		setAlignItems(Alignment.CENTER);

	}

	private void setText() {
		info.setText(TranslationKey.ABOUT_INFO_PARAMS.getTranslation(Version.getFullVersion()));
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