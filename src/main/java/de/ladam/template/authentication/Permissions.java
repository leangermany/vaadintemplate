package de.ladam.template.authentication;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import de.ladam.template.util.application.ApplicationLogger;
import de.ladam.template.util.common.AttributeRefresh;
import de.ladam.template.util.common.ExtendedList;
import de.ladam.template.util.interfaces.PermissionTab;
import de.ladam.template.viewmodels.PermissionVM;
import de.ladam.template.viewmodels.RoleVM;
import de.ladam.template.viewmodels.SessionUserVM;

public class Permissions implements AttributeRefresh {

	public static final String parentPackage = "de.ladam.template";

	private static Permissions instance = null;

	public static Permissions instance() {
		return instance == null ? instance = new Permissions() : instance;
	}

	private final HashMap<Class<?>, PermissionVM> permissionVMs = new HashMap<Class<?>, PermissionVM>();
	private LocalDateTime lastRefresh = null;
	private final static int refreshDelayMinutes = 10;

	private Permissions() {
		init();
	}

	public HashMap<Class<?>, PermissionVM> getPermissionVMs() {
		return permissionVMs;
	}

	public PermissionVM getPermissionVM(String key) {
		return ExtendedList.asExtendedList(permissionVMs.values()).Where(vm -> vm.getKey().equals(key))
				.SingleOrDefault();
	}

	public PermissionVM getPermissionVM(Class<?> c) {
		return permissionVMs.get(c);
	}

	/**
	 * Calls User.getCurrent() to get user permissions
	 */
	public static boolean hasPermission(Class<?> c) {
		return hasPermission(c, User.getCurrent());
	}

	public static boolean hasPermission(Class<?> c, SessionUserVM userVM) {
		if (userVM.isSuperadmin()) {
			return true;
		}
		if (userVM.getRoles().isEmpty())
			return false;
		return instance()._hasPermission(instance().getPermissionVM(c), userVM.getRoles());
	}

	/**
	 * warning: does not check for super user
	 * 
	 */
	public static boolean hasPermission(Class<?> c, List<RoleVM> groupids) {
		return instance()._hasPermission(instance().getPermissionVM(c), groupids);
	}

	/**
	 * warning: does not check for super user
	 */
	public static boolean hasPermission(PermissionVM permissionVM, List<RoleVM> groupIDs) {
		return instance()._hasPermission(permissionVM, groupIDs);
	}

	public boolean _hasPermission(PermissionVM permissionVM, List<RoleVM> groupIDs) {
		if (permissionVM == null) {
			return true;
		}
		if (groupIDs.isEmpty()) {
			return false;
		}
		return permissionVM.getRoles().Contains((o1, o2) -> o1.equals(o2), groupIDs);
	}

	private void init() {
		loadAnnotadedClasses();
		syncValuesWithDatabase();
		loadDependencies();
	}

	private void loadAnnotadedClasses() {
		Reflections reflections = new Reflections(parentPackage);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(PermissionTab.class);

		for (Class<?> c : annotated) {
			if (c.isAnnotationPresent(PermissionTab.class)) {
				PermissionTab at = c.getAnnotation(PermissionTab.class);
				if (checkDoubleValues(c, at.key())) {
//					CateringLogger.trace("Load ApplicationTab annotation from class" + c.getName());
					permissionVMs.put(c, new PermissionVM(c, at.key(), at.name()));
				} else {
					ApplicationLogger
							.warn("TabPermission: " + c.getName() + " was not mapped because of doubled values.");
				}
			}
		}
	}

	private boolean checkDoubleValues(Class<?> c, String key) {
		if (permissionVMs.containsKey(c)) {
			ApplicationLogger.fatal("@TabPermission: class " + c.getName()
					+ " was already mapped. Check that there is only one ApplicationTab annotation.");
			return false;
		}

		for (Map.Entry<Class<?>, PermissionVM> entry : permissionVMs.entrySet()) {
			if (entry.getValue().getKey().equals(key)) {
				ApplicationLogger
						.fatal("@TabPermission: key " + key + " is already in use!. Check that keys are unique!");
				return false;
			}
		}

		return true;
	}

	private void syncValuesWithDatabase() {
		// TODO implement your databse
	}

	private Boolean loadDependencies() {
		if (Role.getAllRoles().isEmpty()) {
			// prevent further exceptions
			ApplicationLogger
					.warn("There are no GroupVMs delivered to TabPermission. Dependencies are not referenced.");
			return false;
		}
		HashMap<String, ExtendedList<RoleVM>> refs = loadTamplateReferences();
		permissionVMs.forEach((key, permission) -> {
			if (refs.containsKey(permission.getKey())) {
				ExtendedList<RoleVM> roleRefs = refs.get(permission.getKey());
				permission.getRoles().clear();
				roleRefs.forEach(role -> permission.addRoleVM(role));
			}
		});
		lastRefresh = LocalDateTime.now();
		return true;
	}

	/**
	 * TODO build own implementation, like load References from Database
	 * 
	 * @return
	 */
	@Deprecated
	private HashMap<String, ExtendedList<RoleVM>> loadTamplateReferences() {
		HashMap<String, ExtendedList<RoleVM>> deps = new HashMap<String, ExtendedList<RoleVM>>();
		/**
		 * InventoryView
		 */
		deps.put("a5254b62-b12a-4326-82b6-fc84a0d60eec", Role.getAllRoles());
		/**
		 * AboutView
		 */
		deps.put("1df7a87e-0c9f-4343-bb23-4c3f74d165fa", Role.getAllRoles());
		/**
		 * AdminView
		 */
		deps.put("db5ad5c0-ba75-4c7f-a7b2-5d2ef12e8242", ExtendedList.asExtendedList(new RoleVM[] { Role.getRole(2) }));
		return deps;
	}

	@Override
	public boolean eagerRefresh() {
		Boolean succ = loadDependencies();
		return succ == null ? false : succ;
	}

	@Override
	public boolean lazyRefresh() {
		if (lastRefresh == null || lastRefresh.isAfter(LocalDateTime.now().minusMinutes(refreshDelayMinutes))) {
			return eagerRefresh();
		} else {
			return false;
		}
	}

}
