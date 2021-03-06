package com.aryaemini.nvi;

import com.aryaemini.nvi.exception.EmptyFieldException;
import com.aryaemini.nvi.exception.TCKNoValidationException;
import com.aryaemini.nvi.interfaces.Citizen;
import com.aryaemini.nvi.interfaces.IdentityCard;
import com.aryaemini.nvi.model.CitizenImpl;
import com.aryaemini.nvi.model.IdentityCardImpl;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.util.logging.Logger;

public class TCKNoValidator {

	private static final Logger logger = Logger.getLogger(TCKNoValidator.class.getName());
	private static TCKNoValidator _this;

	private TCKNoValidator() {
	}

	public static TCKNoValidator getInstance() {
		if(_this == null) {
			_this = new TCKNoValidator();
		}
		return _this;
	}

	private Boolean localValidate(String tckNo) {
		if(tckNo == null || tckNo.length() != 11) {
			logger.fine("T.C. kimlik numarası 11 haneli olmalıdır.");
			return false;
		}
		int odds = 0, evens = 0, sum10 = 0;
		String char10, char11;

		try {
			for (int i = 0; i < 10; i++) {
				if (i % 2 == 0) {
					if (i < 9) odds += Integer.parseInt(tckNo.substring(i, i + 1));
				} else {
					if (i < 8) evens += Integer.parseInt(tckNo.substring(i, i + 1));
				}
				sum10 += Integer.parseInt(tckNo.substring(i, i + 1));
			}

			odds *= 7;
			char10 = Integer.toString((odds - evens) % 10);
			char11 = Integer.toString(sum10 % 10);

			if (!tckNo.substring(10, 11).equals(char11) || !tckNo.substring(9, 10).equals(char10)) {
				logger.fine("Geçersiz T.C. kimlik numarası.");
				return false;
			}

			return true;
		} catch (StringIndexOutOfBoundsException e) {
			logger.fine("T.C. kimlik numarası 11 haneli olmalıdır.");
			throw new TCKNoValidationException("T.C. kimlik numarası 11 haneli olmalıdır.", e);
		}
	}

	public Boolean validate(Citizen citizen) {
		CitizenImpl citizenImpl = new CitizenImpl(citizen);
		try {
			if(localValidate(citizenImpl.getTckNo().toString())) {
				logger.fine("T.C. kimlik numarası algoritması geçerli.");
				SOAPMessage soapMessage = createCitizenSOAPRequest(citizenImpl);
				String url = "https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx";
				return request(soapMessage, url);
			}
			return false;
		} catch (EmptyFieldException e) {
			logger.fine("Doldurulmamış alanlar bulunuyor. T.C. kimlik numarası doğrulaması yapılmadı.");
			return false;
		} catch (NullPointerException e) {
			logger.fine("Doldurulmamış alanlar bulunuyor. T.C. kimlik numarası doğrulaması yapılmadı.");
			return false;
		} catch (SOAPException e) {
			logger.fine("Nüfus müdürlüğü'nden beklenmedik yanıt alındı. İşlem tamamlanamadı.");
			throw new TCKNoValidationException("Nüfus müdürlüğü'nden beklenmedik yanıt alındı. İşlem tamamlanamadı.", e);
		}
	}

	public Boolean validate(IdentityCard identityCard) {
		IdentityCardImpl identityCardImpl = new IdentityCardImpl(identityCard);
		try {
			if (localValidate(identityCardImpl.getTckNo().toString())) {
				logger.fine("T.C. kimlik numarası algoritması geçerli.");
				SOAPMessage soapMessage = createIdentityCardSOAPRequest(identityCardImpl);
				String url = "https://tckimlik.nvi.gov.tr/Service/KPSPublicV2.asmx";
				return request(soapMessage, url);
			}
			return false;
		} catch (EmptyFieldException e) {
			logger.fine("Doldurulmamış alanlar bulunuyor. Nüfus cüzdanı doğrulaması yapılmadı.");
			return false;
		} catch (SOAPException e) {
			logger.fine("Nüfus müdürlüğü'nden beklenmedik yanıt alındı. İşlem tamamlanamadı.");
			throw new TCKNoValidationException("Nüfus müdürlüğü'nden beklenmedik yanıt alındı. İşlem tamamlanamadı.", e);
		}
	}

	private Boolean request(SOAPMessage soapMessage, String url) {
		logger.fine("Nüfus müdürlüğünden sorgulamaya hazırlanılıyor.");
		Boolean result;
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			SOAPMessage response = soapConnection.call(soapMessage, url);
			String responseBody = response.getSOAPBody().getTextContent();
			result = Boolean.valueOf(responseBody);
			soapConnection.close();
		} catch (SOAPException e) {
			throw new TCKNoValidationException("Nüfus müdürlüğü'nden beklenmedik yanıt alındı. İşlem tamamlanamadı.", e);
		}
		return result;
	}

	private SOAPMessage createCitizenSOAPRequest(CitizenImpl citizen) throws EmptyFieldException, SOAPException {
		logger.fine("Sorgulama isteği oluşturuluyor.");
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
			tckNo.addTextNode(citizen.getTckNo().toString());
			name.addTextNode(citizen.getName());
			surname.addTextNode(citizen.getSurname());
			birthYear.addTextNode(citizen.getBirthYear().toString());
		} catch (NullPointerException e) {
			logger.fine("Doldurulmamış alanlar bulunmaktadır. Lütfen tüm alanları doldurun.");
			throw new EmptyFieldException("Lütfen tüm alanları doldurun", e);
		}

		soapMessage.saveChanges();
		return soapMessage;
	}

	private SOAPMessage createIdentityCardSOAPRequest(IdentityCardImpl identityCardImpl) throws EmptyFieldException, SOAPException {
		logger.fine("Sorgulama isteği oluşturuluyor.");
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
			logger.fine("Doldurulmamış alanlar bulunmaktadır. Lütfen tüm alanları doldurun.");
			throw new EmptyFieldException("Lütfen tüm alanları doldurun", e);
		}
		soapMessage.saveChanges();
		return soapMessage;
	}

}
