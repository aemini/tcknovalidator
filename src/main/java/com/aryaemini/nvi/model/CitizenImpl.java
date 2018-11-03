package com.aryaemini.nvi.model;

import com.aryaemini.nvi.interfaces.Citizen;

import java.util.Locale;
import java.util.logging.Logger;

public class CitizenImpl implements Citizen {

	private Long tckNo;
	private String name, surname;
	private Integer birthYear;
	private Locale locale = new Locale("tr");
	private static final Logger logger = Logger.getLogger(CitizenImpl.class.getName());

	private CitizenImpl() {
	}

	public CitizenImpl(Citizen citizen) {
		setTckNo(citizen.getTckNo());
		setName(citizen.getName());
		setSurname(citizen.getSurname());
		setBirthYear(citizen.getBirthYear());
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
			logger.fine("Not a number " + e.getMessage());
			this.tckNo = null;
		}
	}

	public String getName() {
		return this.name.toUpperCase(locale).trim();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return this.surname.toUpperCase(locale).trim();
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Integer getBirthYear() {
		return this.birthYear;
	}

	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	public void setBirthYear(String birthYear) {
		try {
			this.birthYear = Integer.parseInt(birthYear);
		} catch (NumberFormatException e) {
			logger.fine("Not a number " + e.getMessage());
			this.birthYear = null;
		}
	}

}
