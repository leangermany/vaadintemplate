package de.ladam.template.viewmodels;

public class RoleVM {
	private int ID;
	private String name;

	public RoleVM(int iD, String name) {
		super();
		ID = iD;
		this.name = name;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
