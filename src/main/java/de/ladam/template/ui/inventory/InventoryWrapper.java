package de.ladam.template.ui.inventory;

import javax.naming.NoPermissionException;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import de.ladam.template.authentication.Permissions;
import de.ladam.template.ui.AppDrawer;
import de.ladam.template.util.i18n.TranslationKey;
import de.ladam.template.util.interfaces.PermissionTab;

@Route(value = "inventory", layout = AppDrawer.class)
@RouteAlias(value = "", layout = AppDrawer.class)
//@PermissionTab(key = "a5254b62-b12a-4326-82b6-fc84a0d60eec", name = "InventoryView")
public class InventoryWrapper extends Div implements BeforeEnterObserver, HasDynamicTitle {

	private InventoryView view = null;

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		// checks if user has permission
		if (Permissions.hasPermission(this.getClass())) {
			initView();
		} else {
			// if not route to error view
			event.rerouteToError(NoPermissionException.class);
		}
	}

	private void initView() {
		if (view == null) {
			view = new InventoryView();
			add(view);
		}
	}

	@Override
	public String getPageTitle() {
		return getTranslation(TranslationKey.DRAWER_TAB_INVENTORY.toString());
	}

	public InventoryWrapper() {
	}

}
