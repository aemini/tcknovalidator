package com.aryaemini.nvi.model;

import org.apache.log4j.Logger;

import java.util.Locale;

public class Citizen {

	private Long tckNo;
	private String name, surname;
	private Integer birthYear;
	private Locale locale = new Locale("tr");

	private static final Logger logger = Logger.getLogger(Citizen.class);

	public Citizen() {
	}

	public Citizen(Long tckNo, String name, String surname, Integer birthYear) {
		this.tckNo = tckNo;
		this.name = name.toUpperCase(locale);
		this.surname = surname.toUpperCase(locale);
		this.birthYear = birthYear;
	}

	public Citizen(String tckNo, String name, String surname, String birthYear) throws NumberFormatException {
		this.tckNo = Long.parseLong(tckNo);
		this.name = name.toUpperCase(locale);
		this.surname = surname.toUpperCase(locale);
		this.birthYear = Integer.parseInt(birthYear);
	}

	public Long getTckNo() {
		return this.tckNo;
	}

	public void setTckNo(Long l) {
		this.tckNo = l;
	}

	public void setTckNo(String s) {
		try {
			this.tckNo = Long.parseLong(s);
		} catch (NumberFormatException e) {
			logger.warn("T.C. kimlik numarası rakamlardan oluşmalıdır.");
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
			logger.warn("Doğum yılı rakamlardan oluşmalıdır.");
		}
	}

}
