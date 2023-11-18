package com.aryaemini.nvi.interfaces;

public interface IdentityCard extends Person {

	Integer getBirthDay();

	Integer getBirthMonth();

	Integer getIdCardNumber();

	String getIdCardSerial();

	String getTckCardSerialNumber();

	boolean isSurnameNotSpecified();

	boolean isBirthDayNotSpecified();

	boolean isBirthMonthNotSpecified();

	boolean validateIdCardNumber();

	boolean validateTckCardSerialNumber();

}
