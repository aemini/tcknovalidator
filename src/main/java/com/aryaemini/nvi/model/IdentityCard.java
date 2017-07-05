package com.aryaemini.nvi.model;

import org.apache.log4j.Logger;

import java.util.Locale;

public class IdentityCard extends Citizen {

	private Boolean surnameNotSpecified;
	private Integer birthDay;
	private Boolean birthDayNotSpecified;
	private Integer birthMonth;
	private Boolean birthMonthNotSpecified;
	private String idCardSerial;
	private Integer idCardNumber;
	private String idCardSerialNumber;

	private Locale locale = new Locale("tr");
	private static final Logger logger = Logger.getLogger(IdentityCard.class);

	public IdentityCard() {
	}

	public IdentityCard(Long tckNo,
						String name,
						String surname,
						Boolean surnameNotSpecified,
						Integer birthDay,
						Boolean birthDayNotSpecified,
						Integer birthMonth,
						Boolean birthMonthNotSpecified,
						Integer birthYear,
						String idCardSerial,
						Integer idCardNumber) {
		setTckNo(tckNo);
		setName(name);
		setSurname(surname);
		setBirthYear(birthYear);

		this.surnameNotSpecified = surnameNotSpecified;
		this.birthDay = birthDay;
		this.birthDayNotSpecified = birthDayNotSpecified;
		this.birthMonth = birthMonth;
		this.birthMonthNotSpecified = birthMonthNotSpecified;
		this.idCardSerial = idCardSerial;
		this.idCardNumber = idCardNumber;
	}

	public Boolean getSurnameNotSpecified() {
		return surnameNotSpecified;
	}

	public void setSurnameNotSpecified(Boolean surnameNotSpecified) {
		this.surnameNotSpecified = surnameNotSpecified;
	}

	public Integer getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Integer birthDay) {
		this.birthDay = birthDay;
	}

	public Boolean getBirthDayNotSpecified() {
		return birthDayNotSpecified;
	}

	public void setBirthDayNotSpecified(Boolean birthDayNotSpecified) {
		this.birthDayNotSpecified = birthDayNotSpecified;
	}

	public Integer getBirthMonth() {
		return birthMonth;
	}

	public void setBirthMonth(Integer birthMonth) {
		this.birthMonth = birthMonth;
	}

	public Boolean getBirthMonthNotSpecified() {
		return birthMonthNotSpecified;
	}

	public void setBirthMonthNotSpecified(Boolean birthMonthNotSpecified) {
		this.birthMonthNotSpecified = birthMonthNotSpecified;
	}

	public String getIdCardSerial() {
		return idCardSerial;
	}

	public void setIdCardSerial(String idCardSerial) {
		this.idCardSerial = idCardSerial;
	}

	public Integer getIdCardNumber() {
		return idCardNumber;
	}

	public void setIdCardNumber(Integer idCardNumber) {
		this.idCardNumber = idCardNumber;
	}

	public String getIdCardSerialNumber() {
		return idCardSerialNumber;
	}

	public void setIdCardSerialNumber(String idCardSerialNumber) {
		this.idCardSerialNumber = idCardSerialNumber;
	}

}
