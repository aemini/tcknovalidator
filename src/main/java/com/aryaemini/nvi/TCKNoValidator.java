package com.aryaemini.nvi;

import com.aryaemini.nvi.exception.EmptyFieldException;
import com.aryaemini.nvi.exception.TCKNoValidationException;
import com.aryaemini.nvi.interfaces.IdentityCard;
import com.aryaemini.nvi.interfaces.Person;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class TCKNoValidator {

	private static final Logger logger = LoggerFactory.getLogger(TCKNoValidator.class);
	private static final String MESSAGE_EMPTY_FIELD = "Doldurulmamış alanlar bulunuyor. T.C. kimlik numarası doğrulaması yapılmadı.";
	private static final String MESSAGE_LENGTH = "T.C. kimlik numarası 11 haneli olmalıdır.";
	private static final String MESSAGE_UNEXPECTED_RESPONSE = "Nüfus müdürlüğü'nden beklenmedik yanıt alındı. İşlem tamamlanamadı.";
	private static final String SERVICE_URL_IDENTITY_CARD_VALIDATION = "https://tckimlik.nvi.gov.tr/Service/KPSPublicV2.asmx";
	private static final String SERVICE_URL_PERSON_VALIDATION = "https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx";
	private static TCKNoValidator instance;
	private final MessageFactory messageFactory;

	private TCKNoValidator() throws SOAPException {
		messageFactory = MessageFactory.newInstance();
	}

	public static synchronized TCKNoValidator getInstance() {
		try {
			if (Objects.isNull(instance)) {
				instance = new TCKNoValidator();
			}
			return instance;
		} catch (SOAPException e) {
			throw new TCKNoValidationException(e.getMessage(), e);
		}
	}

	private boolean localValidate(String tckNo) {
		if (Objects.isNull(tckNo) || tckNo.length() != 11) {
			logger.trace(MESSAGE_LENGTH);
			return false;
		}
		try {
			int odds = 0;
			int evens = 0;
			int sum10 = 0;
			for (int i = 0; i < 10; i++) {
				int digit = Character.getNumericValue(tckNo.charAt(i));
				if (i % 2 == 0) {
					odds += digit;
				} else if (i < 8) {
					evens += digit;
				}
				sum10 += digit;
			}
			odds *= 7;
			return Character.getNumericValue(tckNo.charAt(9)) == ((odds - evens) % 10) && Character.getNumericValue(tckNo.charAt(10)) == (sum10 % 10);
		} catch (StringIndexOutOfBoundsException e) {
			throw new TCKNoValidationException(MESSAGE_LENGTH, e);
		}
	}

	public boolean validate(Person person) {
		try {
			if (localValidate(person.getIdentityNumber())) {
				SOAPMessage soapMessage = createCitizenSOAPRequest(person);
				return request(soapMessage, SERVICE_URL_PERSON_VALIDATION);
			}
			return false;
		} catch (EmptyFieldException | NullPointerException e) {
			logger.trace(MESSAGE_EMPTY_FIELD, e);
			return false;
		} catch (SOAPException e) {
			throw new TCKNoValidationException(MESSAGE_UNEXPECTED_RESPONSE, e);
		}
	}

	public boolean validate(IdentityCard identityCard) {
		try {
			if (localValidate(identityCard.getIdentityNumber())) {
				SOAPMessage soapMessage = createIdentityCardSOAPRequest(identityCard);
				return request(soapMessage, SERVICE_URL_IDENTITY_CARD_VALIDATION);
			}
			return false;
		} catch (EmptyFieldException e) {
			logger.trace(e.getMessage(), e);
			return false;
		} catch (SOAPException e) {
			throw new TCKNoValidationException(MESSAGE_UNEXPECTED_RESPONSE, e);
		}
	}

	private boolean request(SOAPMessage soapMessage, String url) {
		logger.trace("Nüfus müdürlüğünden sorgulamaya hazırlanılıyor.");
		var response = new AtomicReference<Boolean>();
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			SOAPMessage soapResponse = soapConnection.call(soapMessage, url);
			String responseBody = soapResponse.getSOAPBody().getTextContent();
			response.setRelease(Boolean.parseBoolean(responseBody));
			soapConnection.close();
		} catch (SOAPException e) {
			throw new TCKNoValidationException(MESSAGE_UNEXPECTED_RESPONSE, e);
		}
		logger.trace("Sorgulama sonucu: {}", response.get());
		return response.get();
	}

	private SOAPBody generateSoapBody(SOAPMessage soapMessage) throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
		envelope.addNamespaceDeclaration("soap12", "http://www.w3.org/2003/05/soap-envelope");
		envelope.addNamespaceDeclaration("tckn", "http://tckimlik.nvi.gov.tr/WS");
		return envelope.getBody();
	}

	private SOAPMessage createCitizenSOAPRequest(Person person) throws EmptyFieldException, SOAPException {
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPElement tcKnValidate = generateSoapBody(soapMessage).addChildElement("TCKimlikNoDogrula", "tckn");
		SOAPElement tckNo = tcKnValidate.addChildElement("TCKimlikNo", "tckn");
		SOAPElement name = tcKnValidate.addChildElement("Ad", "tckn");
		SOAPElement surname = tcKnValidate.addChildElement("Soyad", "tckn");
		SOAPElement birthYear = tcKnValidate.addChildElement("DogumYili", "tckn");

		try {
			tckNo.addTextNode(person.getIdentityNumber());
			name.addTextNode(person.getFirstName());
			surname.addTextNode(person.getLastName());
			birthYear.addTextNode(person.getBirthYear().toString());
		} catch (NullPointerException e) {
			throw new EmptyFieldException(MESSAGE_EMPTY_FIELD, e);
		}

		soapMessage.saveChanges();
		return soapMessage;
	}

	private SOAPMessage createIdentityCardSOAPRequest(IdentityCard identityCard) throws EmptyFieldException, SOAPException {
		SOAPMessage soapMessage = messageFactory.createMessage();
		try {
			SOAPElement idCardValidate = generateSoapBody(soapMessage).addChildElement("KisiVeCuzdanDogrula", "tckn");
			idCardValidate.addChildElement("TCKimlikNo", "tckn").addTextNode(identityCard.getIdentityNumber());
			idCardValidate.addChildElement("Ad", "tckn").addTextNode(identityCard.getFirstName());
			if (identityCard.isSurnameNotSpecified()) {
				idCardValidate.addChildElement("SoyadYok", "tckn").addTextNode("true");
			} else {
				idCardValidate.addChildElement("Soyad", "tckn").addTextNode(identityCard.getLastName());
			}
			if (identityCard.isBirthDayNotSpecified()) {
				idCardValidate.addChildElement("DogumGunYok", "tckn").addTextNode("true");
			} else {
				idCardValidate.addChildElement("DogumGun", "tckn").addTextNode(identityCard.getBirthDay().toString());
			}
			if (identityCard.isBirthMonthNotSpecified()) {
				idCardValidate.addChildElement("DogumAyYok", "tckn").addTextNode("true");
			} else {
				idCardValidate.addChildElement("DogumAy", "tckn").addTextNode(identityCard.getBirthMonth().toString());
			}
			idCardValidate.addChildElement("DogumYil", "tckn").addTextNode(identityCard.getBirthYear().toString());

			if (identityCard.validateIdCardNumber()) {
				idCardValidate.addChildElement("CuzdanSeri", "tckn").addTextNode(identityCard.getIdCardSerial());
				idCardValidate.addChildElement("CuzdanNo", "tckn").addTextNode(identityCard.getIdCardNumber().toString());
			}

			if (identityCard.validateTckCardSerialNumber()) {
				idCardValidate.addChildElement("TCKKSeriNo", "tckn").addTextNode(identityCard.getTckCardSerialNumber());
			}
		} catch (NullPointerException e) {
			throw new EmptyFieldException(MESSAGE_EMPTY_FIELD, e);
		}
		soapMessage.saveChanges();
		return soapMessage;
	}

}
