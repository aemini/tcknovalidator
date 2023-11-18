package com.aryaemini.nvi.model;

import com.aryaemini.nvi.interfaces.IdentityCard;
import lombok.Builder;
import lombok.Getter;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Builder
@Getter
public class Identity implements IdentityCard {

	private String identityNumber;
	private String firstName;
	private String lastName;
	private Date birthDate;
	private String idCardSerial;
	private Integer idCardNumber;
	private String tckCardSerialNumber;

	@Override
	public Integer getBirthDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthDate);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public Integer getBirthMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthDate);
		return calendar.get(Calendar.MONTH);
	}

	@Override
	public boolean isSurnameNotSpecified() {
		return Objects.isNull(lastName);
	}

	@Override
	public boolean isBirthDayNotSpecified() {
		return false;
	}

	@Override
	public boolean isBirthMonthNotSpecified() {
		return false;
	}

	@Override
	public Integer getBirthYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthDate);
		return calendar.get(Calendar.YEAR);
	}

	@Override
	public boolean validateIdCardNumber() {
		return Objects.nonNull(idCardSerial) && Objects.nonNull(idCardNumber);
	}

	@Override
	public boolean validateTckCardSerialNumber() {
		return Objects.nonNull(tckCardSerialNumber);
	}

}
