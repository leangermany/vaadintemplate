package de.ladam.template.ui;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import de.ladam.template.authentication.AccessControlFactory;
import de.ladam.template.authentication.Permissions;
import de.ladam.template.authentication.User;
import de.ladam.template.ui.AppDrawerItems.SidebarMenuItem;
import de.ladam.template.ui.settings.SettingsView;
import de.ladam.template.util.application.ApplicationLogger;
import de.ladam.template.util.common.ExtendedList;
import de.ladam.template.util.i18n.TranslationKey;
import de.ladam.template.viewmodels.SessionUserVM;

/**
 * The main layout. Contains the navigation menu.
 */
@Push
@Theme(value = Lumo.class)
@PWA(name = "Template", shortName = "Template", enableInstallPrompt = true, manifestPath = "manifest.json")
@CssImport("./styles/shared-styles.css")
@CssImport("styles/drawer-styles.css")
@CssImport(value = "./styles/menu-buttons.css", themeFor = "vaadin-button")
/**
 * theme file is importetd here
 * 
 * @author lam
 *
 */
@CssImport("./theme/custom-themes.css")
public class AppDrawer extends AppLayout implements LocaleChangeObserver, BeforeEnterObserver, PageConfigurator {

	public final String ApplicationName = "Template";

	private Image logo = null;
	private final Span applicationName = new Span("Template");

	private final Button settingsButton = new Button();
	private final Button logoutButton = new Button();
	private final VerticalLayout menu;
	private final VerticalLayout subMenu = new VerticalLayout();

	private Span navbarTitle;

	public AppDrawer() {
		// Note! Image resource url is resolved here as it is dependent on the
		// execution mode (development or production) and browser ES level
		// support
		final String resolvedImage = VaadinService.getCurrent().resolveResource("img/table-logo.png",
				VaadinSession.getCurrent().getBrowser());
		logo = new Image(resolvedImage, "");
		setPrimarySection(Section.DRAWER);
		addToNavbar(false, createHeaderContent());
		menu = createMenu();
		addToDrawer(createDrawerContent(menu));
		settingsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		settingsButton.addClickListener(route -> {
			UI.getCurrent().navigate(SettingsView.class);
		});
		logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		logoutButton.setId("loginbutton");
		logoutButton.addClickListener(route -> {
			logout();
		});
	}

	private Component createHeaderContent() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setId("header");
		layout.getThemeList().set("dark", true);
		layout.setWidthFull();
		layout.setSpacing(false);
		layout.setAlignItems(FlexComponent.Alignment.CENTER);
		layout.add(new DrawerToggle());
		navbarTitle = new Span();
		layout.add(navbarTitle);
		return layout;
	}

	private Component createDrawerContent(Component menu) {
		VerticalLayout layout = new VerticalLayout();
		layout.addClassName("navbar-layout");
		layout.setSizeFull();
		layout.setPadding(false);
		layout.setSpacing(false);
		layout.getThemeList().set("spacing-s", true);
		layout.setAlignItems(FlexComponent.Alignment.STRETCH);
		HorizontalLayout logoLayout = new HorizontalLayout();
		logoLayout.setId("logo");
		logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		logoLayout.add(logo);
		logoLayout.add(applicationName);
		logoLayout.getStyle().set("padding", "15px");
		subMenu.add(settingsButton, logoutButton);
		subMenu.getThemeList().add("spacing-s");
//		subMenu.setPadding(false);
		layout.add(logoLayout, menu, subMenu);
		return layout;
	}

	private VerticalLayout createMenu() {
		final VerticalLayout tabs = new VerticalLayout();
		tabs.setId("tabs");
		tabs.getThemeList().add("spacing-s");
		return tabs;
	}

	private static Button createTab(String text, Class<? extends Component> navigationTarget) {
		Button b = new Button(text);
		b.addClickListener(c -> UI.getCurrent().navigate(navigationTarget));
		b.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		return b;
	}

	private static Button createTab(SidebarMenuItem sidebarChildVM) {
		return createTab(sidebarChildVM.getTranslationKey().getTranslation(), sidebarChildVM.getTargetClass());
	}

	private void addToMenu(Component tab) {
		menu.add(tab);
	}

	private void logout() {
		AccessControlFactory.getInstance().createAccessControl().signOut();
	}

	/**
	 * Adds tabs to the menu depending on the permission.
	 */
	private void addTabsOnPersmission() {
		menu.removeAll();
		SessionUserVM user = User.getCurrent();
		ExtendedList<SidebarMenuItem> sidebarItems = AppDrawerItems.buildSet();
		sidebarItems.removeIf(item -> !Permissions.hasPermission(item.getTargetClass(), user));
		for (SidebarMenuItem sidebarChildVM : sidebarItems) {
			registerRoute(sidebarChildVM.getTargetClass());
			addToMenu(createTab(sidebarChildVM));
		}
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		navbarTitle.setText(getCurrentPageTitle());
		addTabsOnPersmission();
		setText();
	}

	private String getCurrentPageTitle() {
		// getContent().getClass().getAnnotation(PageTitle.class).value();
		String tabname = this.ApplicationName;
		Component content = getContent();
		if (content.getClass().isAnnotationPresent(PageTitle.class)) {
			tabname = content.getClass().getAnnotation(PageTitle.class).value();
		} else if (content instanceof HasDynamicTitle) {
			tabname = ((HasDynamicTitle) content).getPageTitle();
		}
		return tabname;
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);

	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		/**
		 * the beforeEnter method isn't useful for building something visually, because
		 * it's called on every navigation on child layouts.<br>
		 * It could be used to check on permission rights:
		 * Permissions.hasPermission(event.getNavigationTarget())
		 * 
		 * @author lam
		 */
	}

	private void registerRoute(Class<? extends Component> target) {
		// register the view dynamically only for this session
		// as logout will purge the session route registry, no need to
		// unregister the view on logout
		try {
			if (!RouteConfiguration.forSessionScope().isRouteRegistered(target)) {
				ApplicationLogger.trace("registering route for target: " + target.getSimpleName());
				RouteConfiguration.forSessionScope().setRoute(target.getAnnotation(Route.class).value(), target,
						this.getClass());
			}
			// register @RouteAlias if not already present to session scope
			if (target.isAnnotationPresent(RouteAlias.class)) {
				for (RouteAlias alias : target.getAnnotationsByType(RouteAlias.class)) {
					if (!RouteConfiguration.forSessionScope().isRouteRegistered(target)) {
						ApplicationLogger.trace("registering alias for target: " + target.getSimpleName());
						RouteConfiguration.forSessionScope().setRoute(alias.value(), target, this.getClass());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		addTabsOnPersmission();
		setText();
	}

	private void setText() {
		settingsButton.setText(TranslationKey.SETTINGS_TITLE.getTranslation());
		logoutButton.setText(TranslationKey.LOGOUT_BUTTON.getTranslation());
	}

	@Override
	public void configurePage(InitialPageSettings settings) {
		/**
		 * change this to remove the default style completely or set css !importent
		 * values for class .v-loading-indicator<br>
		 * https://vaadin.com/docs/v14/flow/advanced/tutorial-loading-indicator.html
		 */
		settings.getLoadingIndicatorConfiguration().setApplyDefaultTheme(true);

		/**
		 * meta tags added to the html head
		 */
		settings.addMetaTag("title", "Vaadin Template Application");
		settings.addMetaTag("description", "This is a Webpage with How-Tos and Infos");
		settings.addMetaTag("keywords", "vaadin, template");

		settings.addMetaTag("og:title", "Vaadin Template Application");
		settings.addMetaTag("og:type", "Website");
		settings.addMetaTag("og:description", "This is a Webpage with How-Tos and Infos Nachrichten und Chatrooms");
		settings.addMetaTag("og:url", "www.domain.de");
		settings.addMetaTag("og:site_name", "Vaadin Template Application");
		settings.addMetaTag("og:locale", "en_EN");

		// Use this for adding javascript import to your html head
		// the folder is /src/main/webapp/frontend/
		// UI.getCurrent().getPage().addJavaScript("url/file.js");

	}

}
