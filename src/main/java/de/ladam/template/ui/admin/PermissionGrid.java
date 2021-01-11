package de.ladam.template.ui.admin;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;

import de.ladam.template.viewmodels.PermissionVM;

public class PermissionGrid extends Grid<PermissionVM> {

	public PermissionGrid() {
		super(PermissionVM.class, false);
		Column<PermissionVM> namecol = addColumn("name").setSortable(false);
		sort(GridSortOrder.asc(namecol).build());
		setSelectionMode(SelectionMode.SINGLE);
	}

}
