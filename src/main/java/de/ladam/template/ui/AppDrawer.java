package de.ladam.template.ui;

import java.util.Optional;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
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
public class AppDrawer extends AppLayout implements LocaleChangeObserver, BeforeEnterObserver {

	public final String ApplicationName = "Template";

	private Image logo = null;
	private final Span applicationName = new Span("Template");

	private final Button settingsButton = new Button();
	private final Button logoutButton = new Button();
	private final Tabs menu;

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
		settingsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		settingsButton.addClassName("navbar-button");
		settingsButton.addClickListener(route -> {
			UI.getCurrent().navigate(SettingsView.class);
		});
		logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		logoutButton.addClassName("navbar-button");
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

	private Component createDrawerContent(Tabs menu) {
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
		layout.add(logoLayout, menu, settingsButton, logoutButton);
		return layout;
	}

	private Tabs createMenu() {
		final Tabs tabs = new Tabs();
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
		tabs.setId("tabs");
//		tabs.add(createMenuItems());
		return tabs;
	}

	private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
		final Tab tab = new Tab();
		tab.add(new RouterLink(text, navigationTarget));
		ComponentUtil.setData(tab, Class.class, navigationTarget);
		return tab;
	}

	private static Tab createTab(SidebarMenuItem sidebarChildVM) {
		return createTab(sidebarChildVM.getTranslationKey().getTranslation(), sidebarChildVM.getTargetClass());
	}

	private void addToMenu(Tab tab) {
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
		getTabForComponent(getContent()).ifPresent(menu::setSelectedTab);
		navbarTitle.setText(getCurrentPageTitle());
	}

	private Optional<Tab> getTabForComponent(Component component) {
		return menu.getChildren().filter(tab -> ComponentUtil.getData(tab, Class.class).equals(component.getClass()))
				.findFirst().map(Tab.class::cast);
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
		addTabsOnPersmission();
		setText();
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		/**
		 * the beforeEnter method isn't useful for building something visually, because
		 * it's called on every navigation on child layouts.<br>
		 * It could be used to check on permission rights: Permissions.hasPermission(event.getNavigationTarget())
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

}
