package com.noamwolf.android.fitcompanion.model;

import com.google.common.base.Objects;

public class Application {
	private String packageName;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("PackageName", packageName)
				.toString();
	}
}
