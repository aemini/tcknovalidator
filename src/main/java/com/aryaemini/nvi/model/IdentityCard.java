package com.aryaemini.nvi.model;

import org.apache.log4j.Logger;

import java.util.Locale;

public class IdentityCard {

	private Long tckNo;
	private String name;
	private String surname;
	private Integer birthDay;
	private Integer birthMonth;
	private Integer birthYear;
	private String idCardSerial;
	private Integer idCardNumber;
	private String tckCardSerialNumber;

	private Locale locale = new Locale("tr");
	private static final Logger logger = Logger.getLogger(IdentityCard.class);

	public Long getTckNo() {
		return tckNo;
	}

	public void setTckNo(Long tckNo) {
		this.tckNo = tckNo;
	}

	public void setTckNo(String tckNo) {
		try {
			this.tckNo = Long.parseLong(tckNo);
		} catch (NumberFormatException e) {
			logger.info("T.C. kimlik numarası rakamlardan oluşmalıdır.");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.toUpperCase(locale);
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		if(surname != null && surname.length() > 0) {
			this.surname = surname.toUpperCase(locale);
		} else {
			this.surname = null;
		}
	}

	public boolean isSurnameNotSpecified() {
		return surname == null;
	}

	public Integer getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Integer birthDay) {
		if(birthDay != null && birthDay > 0) {
			this.birthDay = birthDay;
		} else {
			this.birthDay = null;
		}
	}

	public void setBirthDay(String strBirthDay) {
		try {
			Integer birthDay = Integer.parseInt(strBirthDay);
			if(birthDay > 0) {
				this.birthDay = birthDay;
			} else {
				this.birthDay = null;
			}
		} catch (NumberFormatException e) {
			logger.info("Doğum günü rakamlardan oluşmalıdır.");
			this.birthDay = null;
		}
	}

	public boolean isBirthDayNotSpecified() {
		return birthDay == null;
	}

	public Integer getBirthMonth() {
		return birthMonth;
	}

	public void setBirthMonth(Integer birthMonth) {
		if(birthMonth != null && birthMonth > 0) {
			this.birthMonth = birthMonth;
		} else {
			this.birthMonth = null;
		}
	}

	public void setBirthMonth(String strBirthMonth) {
		try {
			Integer birthMonth = Integer.parseInt(strBirthMonth);
			if(birthMonth > 0) {
				this.birthMonth = birthMonth;
			} else {
				this.birthMonth = null;
			}
		} catch (NumberFormatException e) {
			logger.info("Doğum ayı rakamlardan oluşmalıdır.");
			this.birthMonth = null;
		}
	}

	public boolean isBirthMonthNotSpecified() {
		return birthMonth == null;
	}

	public Integer getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	public void setBirthYear(String birthYear) {
		try {
			this.birthYear = Integer.parseInt(birthYear);
		} catch (NumberFormatException e) {
			logger.info("Doğum yılı rakamlardan oluşmalıdır.");
			this.birthYear = null;
		}
	}

	public String getIdCardSerial() {
		return idCardSerial;
	}

	public void setIdCardSerial(String idCardSerial) {
		this.idCardSerial = stringOrNull(idCardSerial);
	}

	public Integer getIdCardNumber() {
		return idCardNumber;
	}

	public void setIdCardNumber(Integer idCardNumber) {
		this.idCardNumber = idCardNumber;
	}

	public void setIdCardNumber(String idCardNumber) {
		try {
			this.idCardNumber = Integer.parseInt(idCardNumber);
		} catch (NumberFormatException e) {
			logger.info("Kimlik seri numarası rakamlardan oluşmalıdır.");
			this.idCardNumber = null;
		}
	}

	public boolean validateIdCardNumber() {
		return idCardSerial != null && idCardNumber != null;
	}

	public String getTckCardSerialNumber() {
		return tckCardSerialNumber;
	}

	public void setTckCardSerialNumber(String tckCardSerialNumber) {
		this.tckCardSerialNumber = stringOrNull(tckCardSerialNumber);
	}

	public boolean validateTckCardSerialNumber() {
		return tckCardSerialNumber != null;
	}

	private String stringOrNull(String string) {
		if(string == null || string.length() < 1) {
			return null;
		}
		return string.toUpperCase(locale);
	}

}
