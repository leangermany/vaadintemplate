package de.ladam.template.ui.admin;

import java.util.HashMap;

import javax.naming.NoPermissionException;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.ladam.template.authentication.Permissions;
import de.ladam.template.components.tabsheet.TabSheet2;
import de.ladam.template.ui.AppDrawer;
import de.ladam.template.util.i18n.TranslationKey;
import de.ladam.template.util.interfaces.PermissionTab;

/**
 * Admin view that is registered dynamically on admin user login.
 * 
 * <p>
 * Note from ladam: A nested RouterLayout shouldn't have Route. Bug in
 * 14.4.5? Can have a Route Prefix<br>
 * </p>
 * 
 */
@PageTitle("Admin")
@Route(value = "admin", layout = AppDrawer.class, registerAtStartup = false)
public class AdminView extends VerticalLayout implements BeforeEnterObserver {

	private TabSheet2 tabSheet = new TabSheet2();
	private HashMap<Tab, Runnable> mappedTranslations = new HashMap<Tab, Runnable>();

	public AdminView() {
		tabSheet.getTabBar().setAutoselect(true);
		addTab(TranslationKey.ADMIN_PERMISSION_CAPTION, Admin_PermissionView.class);
		setMargin(false);
		setPadding(false);
		add(tabSheet.getLayout());
	}

	public void addTab(String title, Class<? extends Component> target) {
		tabSheet.addTab(title, target);
	}

	public void addTab(TranslationKey translationKey, Class<? extends Component> target,
			Object... translationKeyParams) {
		Tab tab = tabSheet.addTab(translationKey.getTranslation(translationKeyParams), target);
		mappedTranslations.put(tab, () -> tab.setLabel(translationKey.getTranslation(translationKeyParams)));
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		// checking for permission
		if (!Permissions.hasPermission(this.getClass())) {
			event.rerouteToError(NoPermissionException.class);
		}
	}

}
