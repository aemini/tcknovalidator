package com.aryaemini.nvi;

import com.aryaemini.nvi.exception.EmptyFieldException;
import com.aryaemini.nvi.exception.TCKNoValidationException;
import com.aryaemini.nvi.interfaces.IdentityCard;
import com.aryaemini.nvi.interfaces.Person;
import com.aryaemini.nvi.model.IdentityCardImpl;
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

public class TCKNoValidator {

	private static final Logger logger = LoggerFactory.getLogger(TCKNoValidator.class);
	private static final String MESSAGE_EMPTY_FIELD = "Doldurulmamış alanlar bulunuyor. T.C. kimlik numarası doğrulaması yapılmadı.";
	private static final String MESSAGE_LENGTH = "T.C. kimlik numarası 11 haneli olmalıdır.";
	private static final String MESSAGE_UNEXPECTED_RESPONSE = "Nüfus müdürlüğü'nden beklenmedik yanıt alındı. İşlem tamamlanamadı.";
	private static final String SERVICE_URL_PERSON_VALUDATION = "https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx";
	private static TCKNoValidator instance;

	private TCKNoValidator() {
	}

	public static synchronized TCKNoValidator getInstance() {
		if(Objects.isNull(instance)) {
			instance = new TCKNoValidator();
		}
		return instance;
	}

	private boolean localValidate(String tckNo) {
		if(Objects.isNull(tckNo) || tckNo.length() != 11) {
			logger.trace(MESSAGE_LENGTH);
			return false;
		}
		int odds = 0;
		int evens = 0;
		int sum10 = 0;
		String char10;
		String char11;

		try {
			for (int i = 0; i < 10; i++) {
				int digit = Integer.parseInt(tckNo.substring(i, i + 1));
				if (i % 2 == 0) {
					odds += digit;
				} else if (i < 8) {
					evens += digit;
				}
				sum10 += digit;
			}

			odds *= 7;
			char10 = Integer.toString((odds - evens) % 10);
			char11 = Integer.toString(sum10 % 10);

			if (!tckNo.substring(10, 11).equals(char11) || !tckNo.substring(9, 10).equals(char10)) {
				logger.trace("Geçersiz T.C. kimlik numarası.");
				return false;
			}

			return true;
		} catch (StringIndexOutOfBoundsException e) {
			logger.trace(MESSAGE_LENGTH);
			throw new TCKNoValidationException(MESSAGE_LENGTH, e);
		}
	}

	public boolean validate(Person person) {
		try {
			if (localValidate(person.getIdentityNumber())) {
				logger.trace("T.C. kimlik numarası algoritması geçerli.");
				SOAPMessage soapMessage = createCitizenSOAPRequest(person);
				return request(soapMessage, SERVICE_URL_PERSON_VALUDATION);
			}
			return false;
		} catch (EmptyFieldException | NullPointerException e) {
			logger.trace(MESSAGE_EMPTY_FIELD, e);
			return false;
		} catch (SOAPException e) {
			logger.trace(MESSAGE_UNEXPECTED_RESPONSE, e);
			throw new TCKNoValidationException(MESSAGE_UNEXPECTED_RESPONSE, e);
		}
	}

	public boolean validate(IdentityCard identityCard) {
		IdentityCardImpl identityCardImpl = new IdentityCardImpl(identityCard);
		try {
			if (localValidate(identityCardImpl.getTckNo().toString())) {
				logger.trace("T.C. kimlik numarası algoritması geçerli.");
				SOAPMessage soapMessage = createIdentityCardSOAPRequest(identityCardImpl);
				String url = "https://tckimlik.nvi.gov.tr/Service/KPSPublicV2.asmx";
				return request(soapMessage, url);
			}
			return false;
		} catch (EmptyFieldException e) {
			logger.trace(MESSAGE_EMPTY_FIELD);
			return false;
		} catch (SOAPException e) {
			logger.trace(MESSAGE_UNEXPECTED_RESPONSE);
			throw new TCKNoValidationException(MESSAGE_UNEXPECTED_RESPONSE, e);
		}
	}

	private boolean request(SOAPMessage soapMessage, String url) {
		logger.trace("Nüfus müdürlüğünden sorgulamaya hazırlanılıyor.");
		boolean result;
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			SOAPMessage response = soapConnection.call(soapMessage, url);
			String responseBody = response.getSOAPBody().getTextContent();
			result = Boolean.parseBoolean(responseBody);
			soapConnection.close();
		} catch (SOAPException e) {
			throw new TCKNoValidationException(MESSAGE_UNEXPECTED_RESPONSE, e);
		}
		return result;
	}

	private SOAPMessage createCitizenSOAPRequest(Person person) throws EmptyFieldException, SOAPException {
		logger.trace("Sorgulama isteği oluşturuluyor.");
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		SOAPEnvelope envelope = soapPart.getEnvelope();

		envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
		envelope.addNamespaceDeclaration("soap12", "http://www.w3.org/2003/05/soap-envelope");
		envelope.addNamespaceDeclaration("tckn", "http://tckimlik.nvi.gov.tr/WS");

		SOAPBody soapBody = envelope.getBody();

		SOAPElement tcKnValidate = soapBody.addChildElement("TCKimlikNoDogrula", "tckn");
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
			logger.trace(MESSAGE_EMPTY_FIELD);
			throw new EmptyFieldException("Lütfen tüm alanları doldurun", e);
		}

		soapMessage.saveChanges();
		return soapMessage;
	}

	private SOAPMessage createIdentityCardSOAPRequest(IdentityCardImpl identityCardImpl) throws EmptyFieldException, SOAPException {
		logger.trace("Sorgulama isteği oluşturuluyor.");
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		SOAPPart soapPart = soapMessage.getSOAPPart();

		SOAPEnvelope envelope = soapPart.getEnvelope();

		envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
		envelope.addNamespaceDeclaration("soap12", "http://www.w3.org/2003/05/soap-envelope");
		envelope.addNamespaceDeclaration("tckn", "http://tckimlik.nvi.gov.tr/WS");

		SOAPBody soapBody = envelope.getBody();

		try {
			SOAPElement idCardValidate = soapBody.addChildElement("KisiVeCuzdanDogrula", "tckn");
			idCardValidate.addChildElement("TCKimlikNo", "tckn").addTextNode(identityCardImpl.getTckNo().toString());
			idCardValidate.addChildElement("Ad", "tckn").addTextNode(identityCardImpl.getName());
			if (identityCardImpl.isSurnameNotSpecified()) {
				idCardValidate.addChildElement("SoyadYok", "tckn").addTextNode("true");
			} else {
				idCardValidate.addChildElement("Soyad", "tckn").addTextNode(identityCardImpl.getSurname());
			}
			if (identityCardImpl.isBirthDayNotSpecified()) {
				idCardValidate.addChildElement("DogumGunYok", "tckn").addTextNode("true");
			} else {
				idCardValidate.addChildElement("DogumGun", "tckn").addTextNode(identityCardImpl.getBirthDay().toString());
			}
			if (identityCardImpl.isBirthMonthNotSpecified()) {
				idCardValidate.addChildElement("DogumAyYok", "tckn").addTextNode("true");
			} else {
				idCardValidate.addChildElement("DogumAy", "tckn").addTextNode(identityCardImpl.getBirthMonth().toString());
			}
			idCardValidate.addChildElement("DogumYil", "tckn").addTextNode(identityCardImpl.getBirthYear().toString());

			if (identityCardImpl.validateIdCardNumber()) {
				idCardValidate.addChildElement("CuzdanSeri", "tckn").addTextNode(identityCardImpl.getIdCardSerial());
				idCardValidate.addChildElement("CuzdanNo", "tckn").addTextNode(identityCardImpl.getIdCardNumber().toString());
			}

			if (identityCardImpl.validateTckCardSerialNumber()) {
				idCardValidate.addChildElement("TCKKSeriNo", "tckn").addTextNode(identityCardImpl.getTckCardSerialNumber());
			}
		} catch (NullPointerException e) {
			logger.trace(MESSAGE_EMPTY_FIELD);
			throw new EmptyFieldException("Lütfen tüm alanları doldurun", e);
		}
		soapMessage.saveChanges();
		return soapMessage;
	}

}
