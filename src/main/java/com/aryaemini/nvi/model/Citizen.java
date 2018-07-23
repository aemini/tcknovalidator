package com.aryaemini.nvi.model;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Citizen {

	private Long tckNo;
	private String name, surname;
	private Integer birthYear;
	private Locale locale = new Locale("tr");

	private static final Logger logger = Logger.getLogger(Citizen.class.getName());

	public Citizen() {
	}

	public Citizen(Long tckNo, String name, String surname, Integer birthYear) {
		this.tckNo = tckNo;
		this.name = name.toUpperCase(locale);
		this.surname = surname.toUpperCase(locale);
		this.birthYear = birthYear;
	}

	public Citizen(String tckNo, String name, String surname, String birthYear) {
		this.name = name.toUpperCase(locale);
		this.surname = surname.toUpperCase(locale);
		setTckNo(tckNo);
		setBirthYear(birthYear);
	}

	public Long getTckNo() {
		return this.tckNo;
	}

	public void setTckNo(Long l) {
		this.tckNo = l;
	}

	public void setTckNo(String tckNo) {
		try {
			this.tckNo = Long.parseLong(tckNo);
		} catch (NumberFormatException e) {
			logger.log(Level.FINE, "Not a number " + e.getMessage());
			this.tckNo = null;
		}
	}

	public String getName() {
		return this.name;
	}

	public void setName(String s) {
		this.name = s.toUpperCase(locale);
	}

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String s) {
		this.surname = s.toUpperCase(locale);
	}

	public Integer getBirthYear() {
		return this.birthYear;
	}

	public void setBirthYear(Integer i) {
		this.birthYear = i;
	}

	public void setBirthYear(String s) {
		try {
			this.birthYear = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			logger.log(Level.FINE, "Not a number " + e.getMessage());
			this.birthYear = null;
		}
	}

}
