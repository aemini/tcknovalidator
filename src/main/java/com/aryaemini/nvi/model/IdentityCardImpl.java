package com.aryaemini.nvi.model;

import com.aryaemini.nvi.interfaces.IdentityCard;

import java.util.Locale;
import java.util.logging.Logger;

public class IdentityCardImpl implements IdentityCard {

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
	private static final Logger logger = Logger.getLogger(IdentityCardImpl.class.getName());

	private IdentityCardImpl() {
	}

	public IdentityCardImpl(IdentityCard identityCard) {
		setTckNo(identityCard.getTckNo());
		setName(identityCard.getName());
		setSurname(identityCard.getSurname());
		setBirthDay(identityCard.getBirthDay());
		setBirthMonth(identityCard.getBirthMonth());
		setBirthYear(identityCard.getBirthYear());
		setIdCardSerial(identityCard.getIdCardSerial());
		setIdCardNumber(identityCard.getIdCardNumber());
		setTckCardSerialNumber(identityCard.getTckCardSerialNumber());
	}

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
			logger.fine("Not a number " + e.getMessage());
			this.tckNo = null;
		}
	}

	public String getName() {
		return name.toUpperCase(locale).trim();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		if (surname != null && surname.length() > 0) {
			return surname.toUpperCase(locale).trim();
		}
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public boolean isSurnameNotSpecified() {
		return surname == null;
	}

	public Integer getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Integer birthDay) {
		if (birthDay != null && birthDay > 0) {
			this.birthDay = birthDay;
		} else {
			this.birthDay = null;
		}
	}

	public void setBirthDay(String strBirthDay) {
		try {
			Integer birthDay = Integer.parseInt(strBirthDay);
			if (birthDay > 0) {
				this.birthDay = birthDay;
			} else {
				this.birthDay = null;
			}
		} catch (NumberFormatException e) {
			logger.fine("Doğum günü rakamlardan oluşmalıdır.");
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
			if (birthMonth > 0) {
				this.birthMonth = birthMonth;
			} else {
				this.birthMonth = null;
			}
		} catch (NumberFormatException e) {
			logger.fine("Doğum ayı rakamlardan oluşmalıdır.");
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
			logger.fine("Doğum yılı rakamlardan oluşmalıdır.");
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
			logger.fine("Kimlik seri numarası rakamlardan oluşmalıdır.");
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
