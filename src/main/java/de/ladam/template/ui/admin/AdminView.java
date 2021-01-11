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

import de.ladam.template.authentication.TabPermissions;
import de.ladam.template.ui.AppDrawer;
import de.ladam.template.ui.components.tabsheet.TabSheet2;
import de.ladam.template.util.application.PermissionTab;
import de.ladam.template.util.i18n.TranslationKey;

/**
 * Admin view that is registered dynamically on admin user login.
 * 
 * <p>
 * Note from ladam: A nested RouterLayout shouldn't have Route. Bug in
 * 14.4.5?<br>
 * Helped with {@link AdminRoute}.
 * </p>
 * 
 */
@PageTitle("Admin")
@Route(value = "admin", layout = AppDrawer.class)
@PermissionTab(key = "db5ad5c0-ba75-4c7f-a7b2-5d2ef12e8242", name = "AdminView")
public class AdminView extends VerticalLayout implements BeforeEnterObserver {

	private TabSheet2 tabSheet = new TabSheet2();
	private HashMap<Tab, Runnable> mappedTranslations = new HashMap<Tab, Runnable>();

	public AdminView() {
		tabSheet.getTabBar().setAutoselect(true);
		addTab("test", Admin_PermissionView.class);
		setMargin(false);
		setPadding(false);
		add(tabSheet.getLayout());
	}

	private void addTab(String title, Class<? extends Component> target) {
		tabSheet.addTab(title, target);
	}

	private void addTab(TranslationKey translationKey, Class<? extends Component> target,
			Object... translationKeyParams) {
		Tab tab = tabSheet.addTab(translationKey.getTranslation(translationKeyParams), target);
		mappedTranslations.put(tab, () -> tab.setLabel(translationKey.getTranslation(translationKeyParams)));
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		// checking for permission
		if (!TabPermissions.hasPermission(this.getClass())) {
			event.rerouteToError(NoPermissionException.class);
		}
	}

}