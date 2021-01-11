package de.ladam.template.viewmodels;

import java.util.ArrayList;

import de.ladam.template.util.application.ApplicationTheme;
import de.ladam.template.util.application.ApplicationTheme.ThemeName;

public class SessionUserVM {

	private String name;
	private ThemeName themeName = ThemeName.Light;
	private ArrayList<RoleVM> roles = new ArrayList<RoleVM>();

	private boolean validated = false;

	public SessionUserVM() {
		themeName = ApplicationTheme.getCurrentTheme();
	}

	public SessionUserVM(String name, ArrayList<RoleVM> roles) {
		super();
		this.name = name;
		this.roles = roles;
		themeName = ApplicationTheme.getCurrentTheme();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<RoleVM> getRoles() {
		return roles;
	}

	public void setRoles(ArrayList<RoleVM> roles) {
		this.roles = roles;
	}

	public void addRole(RoleVM role) {
		this.roles.add(role);
	}

	public ThemeName getThemeName() {
		return themeName;
	}

	public void setThemeName(ThemeName themeName) {
		this.themeName = themeName;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public boolean isSuperadmin() {
		return false;
	}

}
