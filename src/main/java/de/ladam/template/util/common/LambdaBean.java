package de.ladam.template.util.common;

public class LambdaBean<T> {

	private T bean = null;

	public LambdaBean(T initial) {
		this.bean = initial;
	}

	public LambdaBean() {

	}

	public void set(T value) {
		this.bean = value;
	}

	public T get() {
		return this.bean;
	}

}
