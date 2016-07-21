package com.aryaemini.nvi;

public class Citizen {

	private String tckNo, name, surname;
	private int birthYear;

	public Citizen(String tckNo, String name, String surname, int birthYear) {
		this.tckNo = tckNo;
		this.name = name;
		this.surname = surname;
		this.birthYear = birthYear;
	}

	public String getTckNo() {
		return this.tckNo;
	}

	public String getName() {
		return this.name;
	}

	public String getSurname() {
		return this.surname;
	}

	public int getBirthYear() {
		return this.birthYear;
	}

}
