package de.ladam.template.ui.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexWrap;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;

import de.ladam.template.authentication.Role;
import de.ladam.template.authentication.TabPermissions;
import de.ladam.template.util.i18n.TranslationKey;
import de.ladam.template.viewmodels.PermissionVM;

public class Admin_PermissionView extends VerticalLayout implements LocaleChangeObserver {

	private final Span caption = new Span();
	private final Button save = new Button();
	private final Span info = new Span();

	private final Span captionPermission = new Span();
	private final Span captionRole = new Span();

	private final PermissionGrid permissionGrid = new PermissionGrid();
	private final RoleGrid roleGrid = new RoleGrid();

	private final Notification saveNoti = new Notification("", 4000, Position.BOTTOM_STRETCH);

	public Admin_PermissionView() {
		buildLayout();
		init();
	}

	private void init() {
		save.addClickListener(s -> safe());

		roleGrid.setItems(Role.getAllRoles());
		roleGrid.setEnabled(false);

		permissionGrid.setItems(TabPermissions.instance().getPermissionVMs().values());
		permissionGrid.addSelectionListener(selectionEvent -> {
			roleGrid.deselectAll();
			if (permissionGrid.asSingleSelect().getOptionalValue().isPresent()) {
				roleGrid.setEnabled(true);
				PermissionVM selectedVM = permissionGrid.asSingleSelect().getValue();
				selectedVM.getRoles().forEach(role -> roleGrid.select(role));
			} else {
				roleGrid.setEnabled(false);
			}
		});

		saveNoti.addThemeVariants(NotificationVariant.LUMO_PRIMARY);

		setSizeFull();
	}

	private void safe() {
		saveNoti.open();
	}

	private void buildLayout() {
		caption.getStyle().set("font-size", "150%");
		HorizontalLayout head = new HorizontalLayout(caption, save);
		head.setAlignItems(Alignment.CENTER);
		head.setWidthFull();
		VerticalLayout infoLayout = new VerticalLayout(info);
		VerticalLayout permissionLayout = new VerticalLayout(captionPermission, permissionGrid);
		VerticalLayout groupLayout = new VerticalLayout(captionRole, roleGrid);
		FlexLayout body = new FlexLayout(permissionLayout, groupLayout);
		body.setSizeFull();
		body.setFlexWrap(FlexWrap.WRAP);
		body.setFlexBasis("300px", permissionLayout, groupLayout);
		body.setFlexGrow(1, permissionLayout, groupLayout);
		body.setFlexShrink(1, permissionLayout, groupLayout);
		add(head, infoLayout, body);
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		setText();
	}

	private void setText() {
		caption.setText(TranslationKey.ADMIN_PERMISSION_CAPTION.getTranslation());
		info.setText(TranslationKey.ADMIN_PERMISSION_INFO.getTranslation());
		save.setText(TranslationKey.SAVE_BUTTON.getTranslation());
		saveNoti.setText("Implement save...");

		captionPermission.setText(getTranslation(TranslationKey.ADMIN_PERMISSION_GRID_TABS.toString()));
		captionRole.setText(TranslationKey.ADMIN_PERMISSION_GRID_GROUPS.getTranslation());
	}

}
