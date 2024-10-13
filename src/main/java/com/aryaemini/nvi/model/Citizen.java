package com.aryaemini.nvi.model;

import com.aryaemini.nvi.interfaces.Person;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Calendar;
import java.util.Date;

@Builder
@Getter
@ToString
public class Citizen implements Person {

	private String identityNumber;
	private String firstName;
	private String lastName;
	private Date birthDate;

	public Integer getBirthYear() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(birthDate);
		return calendar.get(Calendar.YEAR);
	}

}
