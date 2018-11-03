package com.aryaemini.nvi.interfaces;

public interface IdentityCard extends Citizen {

	Integer getBirthDay();

	Integer getBirthMonth();

	Integer getIdCardNumber();

	String getIdCardSerial();

	String getTckCardSerialNumber();

	boolean isSurnameNotSpecified();

	boolean isBirthDayNotSpecified();

	boolean isBirthMonthNotSpecified();

}
