package de.ladam.template.ui.admin;

import java.util.Set;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;

import de.ladam.template.viewmodels.RoleVM;

public class RoleGrid extends Grid<RoleVM> {

	public RoleGrid() {
		super(RoleVM.class, false);
		Column<RoleVM> namecol = addColumn("name").setSortable(false);
		sort(GridSortOrder.asc(namecol).build());
		setSelectionMode(SelectionMode.MULTI);	
	}

	@Override
	public Set<RoleVM> getSelectedItems() {
		return super.asMultiSelect().getSelectedItems();
	}

}
