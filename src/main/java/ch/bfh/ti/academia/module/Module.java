/*
 * Academia (c) 2021, Bern University of Applied Sciences, Switzerland
 */

package ch.bfh.ti.academia.module;

import java.util.Objects;

/**
 * The class Module represents a teaching module.
 */
public class Module {

	private String nr;
	private String name;
	private String description;

	public Module() {
	}

	public Module(String nr, String name, String description) {
		this.nr = nr;
		this.name = name;
		this.description = description;
	}

	public String getNr() {
		return nr;
	}

	public void setNr(String nr) {
		this.nr = nr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		return Objects.equals(nr, ((Module) other).nr);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nr);
	}

	@Override
	public String toString() {
		return "Module{number=" + nr + ", name='" + name + "', description='" + description + "'}";
	}
}
