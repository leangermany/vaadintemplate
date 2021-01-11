package de.ladam.template.viewmodels;

import de.ladam.template.util.common.ExtendedList;

public class PermissionVM {

	private Class<?> target;
	private String key;
	private String name;

	private ExtendedList<RoleVM> roles = new ExtendedList<>();

	public PermissionVM(Class<?> target, String key, String name) {
		super();
		this.target = target;
		this.key = key;
		this.name = name;
	}

	public Class<?> getTarget() {
		return target;
	}

	public void setTarget(Class<?> target) {
		this.target = target;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ExtendedList<RoleVM> getRoles() {
		return roles;
	}

	public void setRoleVMs(ExtendedList<RoleVM> roles) {
		this.roles = roles;
	}
	
	public void addRoleVM(RoleVM role) {
		this.roles.add(role);
	}

}
