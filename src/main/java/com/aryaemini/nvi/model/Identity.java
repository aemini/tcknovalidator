package com.aryaemini.nvi.model;

import com.aryaemini.nvi.interfaces.IdentityCard;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Calendar;
import java.util.Objects;

@Getter
@SuperBuilder(toBuilder=true)
@ToString
public class Identity extends Citizen implements IdentityCard {

	private Integer birthDay;
	private Integer birthMonth;
	private String idCardSerial;
	private Integer idCardNumber;
	private String tckCardSerialNumber;

	@Override
	public Integer getBirthDay() {
		var calendar = Calendar.getInstance();
		if (Objects.nonNull(getBirthDate())) {
			calendar.setTime(getBirthDate());
			birthDay = calendar.get(Calendar.DAY_OF_MONTH);
		}
		return birthDay;
	}

	@Override
	public Integer getBirthMonth() {
		var calendar = Calendar.getInstance();
		if (Objects.nonNull(getBirthDate())) {
			calendar.setTime(getBirthDate());
			birthMonth = calendar.get(Calendar.MONTH);
		}
		return birthMonth;
	}

	@Override
	public boolean isSurnameNotSpecified() {
		return Objects.isNull(getLastName());
	}

	@Override
	public boolean isBirthDayNotSpecified() {
		return Objects.isNull(getBirthDay());
	}

	@Override
	public boolean isBirthMonthNotSpecified() {
		return Objects.isNull(getBirthMonth());
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
