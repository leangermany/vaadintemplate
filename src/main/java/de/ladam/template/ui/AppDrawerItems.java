package de.ladam.template.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

import de.ladam.template.ui.about.AboutView;
import de.ladam.template.ui.admin.AdminView;
import de.ladam.template.ui.inventory.InventoryWrapper;
import de.ladam.template.util.common.ExtendedList;
import de.ladam.template.util.i18n.TranslationKey;

public class AppDrawerItems {

	public static ExtendedList<SidebarChildVM> buildSet() {
		ExtendedList<SidebarChildVM> list = new ExtendedList<>();

		list.add(new SidebarChildVM(TranslationKey.DRAWER_TAB_INVENTORY, InventoryWrapper.class));
		list.add(new SidebarChildVM(TranslationKey.DRAWER_TAB_ABOUT, AboutView.class));
		list.add(new SidebarChildVM(TranslationKey.DRAWER_TAB_ADMIN, AdminView.class));

		return list;
	}

	public static class SidebarParentVM {

		private final VaadinIcon vaadinIcon;
		private final TranslationKey translationKey;
		private final ExtendedList<SidebarChildVM> children = new ExtendedList<>();

		public SidebarParentVM(VaadinIcon vaadinIcon, TranslationKey translationKey) {
			super();
			this.vaadinIcon = vaadinIcon;
			this.translationKey = translationKey;
		}

		public VaadinIcon getVaadinIcon() {
			return vaadinIcon;
		}

		public TranslationKey getTranslationKey() {
			return translationKey;
		}

		public ExtendedList<SidebarChildVM> getChildren() {
			return children;
		}

		public void addChild(SidebarChildVM sidebarChildVM) {
			this.children.add(sidebarChildVM);
		}

	}

	public static class SidebarChildVM {

		private final VaadinIcon vaadinIcon;
		private final TranslationKey translationKey;
		private final Class<? extends Component> targetClass;

		public SidebarChildVM(TranslationKey translationKey, Class<? extends Component> targetClass) {
			super();
			this.vaadinIcon = VaadinIcon.CARET_RIGHT;
			this.translationKey = translationKey;
			this.targetClass = targetClass;
		}

		public VaadinIcon getVaadinIcon() {
			return vaadinIcon;
		}

		public TranslationKey getTranslationKey() {
			return translationKey;
		}

		public Class<? extends Component> getTargetClass() {
			return targetClass;
		}

	}

}
