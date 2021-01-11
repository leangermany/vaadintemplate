package de.ladam.template.authentication;

import java.time.LocalDateTime;
import java.util.Arrays;

import de.ladam.template.util.application.ApplicationLogger;
import de.ladam.template.util.common.AttributeRefresh;
import de.ladam.template.util.common.ExtendedList;
import de.ladam.template.viewmodels.RoleVM;

public class Role implements AttributeRefresh {

	private static Role instance = null;
	private static LocalDateTime lastRefresh = null;
	private static int refreshdifferenzMinutes = 10;

	public static Role instance() {
		return instance == null ? instance = new Role() : instance;
	}

	private Role() {
		eagerRefresh();
	}

	private ExtendedList<RoleVM> roles = new ExtendedList<RoleVM>();

	public static ExtendedList<RoleVM> getAllRoles() {
		instance().lazyRefresh();
		return instance().roles;
	}

	public static RoleVM getRole(int id) {
		instance().lazyRefresh();
		return instance().roles.Where(r -> r.getID() == id).SingleOrDefault();
	}

	public static RoleVM getRole(String rolename) {
		return Role.instance().roles.Where(r -> r.getName().equals(rolename)).SingleOrDefault();
	}

	private boolean getValues() {
		Boolean success = false;
		ApplicationLogger.trace("loading Roles");
		roles.clear();
		roles.addAll(Arrays.asList(buildTemplateRoles()));
		lastRefresh = LocalDateTime.now();
		success = true;
		return success;
	}

	@Deprecated
	private RoleVM[] buildTemplateRoles() {
		return new RoleVM[] { new RoleVM(1, "employee"), new RoleVM(2, "admin") };
	}
	
	public final String ADMIN_ROLE_NAME = "admin";

	@Override
	public boolean eagerRefresh() {
		return getValues();
	}

	@Override
	public boolean lazyRefresh() {
		if (lastRefresh == null || lastRefresh.isBefore(LocalDateTime.now().minusMinutes(refreshdifferenzMinutes))) {
			return eagerRefresh();
		} else
			return false;
	}

}
