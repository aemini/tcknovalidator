package com.aryaemini.nvi.model;

import com.aryaemini.nvi.interfaces.Person;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Getter
@SuperBuilder(toBuilder=true)
@ToString
public class Citizen implements Person {

	protected static final Calendar calendar = Calendar.getInstance();
	private String identityNumber;
	private String firstName;
	private String lastName;
	private Date birthDate;
	private Integer birthYear;

	public Integer getBirthYear() {
		if (Objects.nonNull(birthDate)) {
			calendar.setTime(birthDate);
			birthYear = calendar.get(Calendar.YEAR);
		}
		return birthYear;
	}

}
