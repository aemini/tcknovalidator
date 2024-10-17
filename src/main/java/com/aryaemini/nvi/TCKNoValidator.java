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
import org.slf4j.MDC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class TCKNoValidator {

	private static final Logger logger = LoggerFactory.getLogger(TCKNoValidator.class);
	private static final String MESSAGE_EMPTY_FIELD = "Doldurulmamış alanlar bulunuyor. T.C. kimlik numarası doğrulaması yapılmadı.";
	private static final String MESSAGE_LENGTH = "T.C. kimlik numarası 11 haneli olmalıdır.";
	private static final String MESSAGE_UNEXPECTED_RESPONSE = "Nüfus müdürlüğü'nden beklenmedik yanıt alındı. İşlem tamamlanamadı.";
	private static TCKNoValidator instance;
	private final String urlIdentityCard;
	private final String urlPerson;
	private final MessageFactory messageFactory;

	private TCKNoValidator() throws SOAPException {
		urlIdentityCard = Optional.ofNullable(System.getProperty("com.aryaemini.nvi.url.identity-card"))
				.orElseGet(() ->Optional.ofNullable(System.getenv("TCKN_VALIDATOR_IDENTITY_CARD_VALIDATION_URL"))
						.orElse("https://tckimlik.nvi.gov.tr/Service/KPSPublicV2.asmx"));
		urlPerson = Optional.ofNullable(System.getProperty("com.aryaemini.nvi.url.person"))
				.orElseGet(() -> Optional.ofNullable(System.getenv("TCKN_VALIDATOR_PERSON_VALIDATION_URL"))
						.orElse("https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx"));
		messageFactory = MessageFactory.newInstance();
	}

	public static synchronized TCKNoValidator getInstance() {
		logger.info("UYARI: Bu kütüphane, T.C. Kimlik Numarası doğrulama işlemleri için kullanılırken herhangi bir kişisel veri saklamaz veya depolamaz. Ancak, kütüphaneyi kullanan kişiler, Türkiye Cumhuriyeti Kanunları çerçevesinde \"VERİ SORUMLUSU\" olarak kabul edilebilir ve 6698 sayılı Kişisel Verilerin Korunması Kanunu (KVKK) gerekliliklerine uymakla yükümlüdür. Lütfen kişisel veri işlemlerinde gerekli yasal düzenlemelere dikkat ediniz.");
		if (logger.isDebugEnabled()) {
			logger.info("!!!DİKKAT!!! Kütüphaneyi şu anda hata ayıklama (debug) modunda kullanıyorsunuz. Bu modda, T.C. Kimlik Numarası ve diğer hassas bilgiler log kanalına iletilebilir. Hata ayıklama modunu kullanırken log kanalına aktarılan bu tür bilgilerin güvenliğinden ve korunmasından tamamen \"VERİ SORUMLUSU\" sıfatıyla kütüphaneyi kullanan kişi veya kurum sorumludur.\n6698 sayılı Kişisel Verilerin Korunması Kanunu (KVKK) çerçevesinde, bu bilgilerin korunmasına yönelik gerekli tedbirlerin alınması yasal zorunluluktur. Hata ayıklama modunu yalnızca güvenli bir ortamda kullanmanızı öneririz.");
		}
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
				return request(soapMessage, urlPerson);
			}
		} catch (EmptyFieldException | NullPointerException e) {
			if (logger.isDebugEnabled()) {
				logger.info(e.getMessage() +
						"\nT.C. Kimlik No : {}" +
						"\nAdı            : {}" +
						"\nSoyadı         : {}" +
						"\nDoğum tarihi   : {}",
						person.getIdentityNumber(),
						person.getFirstName(),
						person.getLastName(),
						person.getBirthYear(),
						e);
			} else {
				logger.info(e.getMessage());
			}
		} catch (SOAPException e) {
			throw new TCKNoValidationException(MESSAGE_UNEXPECTED_RESPONSE, e);
		}
		return false;
	}

	public boolean validate(IdentityCard identityCard) {
		try {
			if (localValidate(identityCard.getIdentityNumber())) {
				SOAPMessage soapMessage = createIdentityCardSOAPRequest(identityCard);
				return request(soapMessage, urlIdentityCard);
			}
			return false;
		} catch (EmptyFieldException | NullPointerException e) {
			if (logger.isDebugEnabled()) {
				final var unspecified = "Belirtilmemiş";
				logger.info(e.getMessage() +
								"\nT.C. Kimlik No : {}" +
								"\nAdı            : {}" +
								"\nSoyadı         : {}" +
								"\nDoğum Yılı     : {}" +
								"\nDoğum Gün      : {}" +
								"\nDoğum Ay       : {}" +
								"\nKimlik Seri No : {}",
						identityCard.getIdentityNumber(),
						identityCard.getFirstName(),
						identityCard.getLastName(),
						identityCard.getBirthYear(),
						identityCard.isBirthDayNotSpecified() ? unspecified : identityCard.getBirthDay(),
						identityCard.isBirthMonthNotSpecified() ? unspecified : identityCard.getBirthMonth(),
						identityCard.validateIdCardNumber() ? (identityCard.getIdCardSerial() + " " + identityCard.getIdCardNumber()) : identityCard.getTckCardSerialNumber(),
						e);
			} else {
				logger.trace(e.getMessage(), e);
			}
			return false;
		} catch (SOAPException e) {
			throw new TCKNoValidationException(MESSAGE_UNEXPECTED_RESPONSE, e);
		}
	}

	private boolean request(SOAPMessage soapRequest, String url) {
		logger.trace("Nüfus müdürlüğünden sorgulamaya hazırlanılıyor.");
		var response = new AtomicReference<Boolean>();
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			SOAPMessage soapResponse = soapConnection.call(soapRequest, url);
			logRequest(url, soapRequest, soapResponse);
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

	private void logRequest(String url, SOAPMessage soapRequest, SOAPMessage soapResponse) throws SOAPException {
		if (logger.isTraceEnabled()) {
			try (var outputStream = new ByteArrayOutputStream()) {
				soapRequest.writeTo(outputStream);
				var requestBody = outputStream.toString(StandardCharsets.UTF_8);
				outputStream.reset();
				soapResponse.writeTo(outputStream);
				var responseBody = outputStream.toString(StandardCharsets.UTF_8);
				MDC.put("requestBody", requestBody);
				MDC.put("responseBody", responseBody);
				logger.trace("Sending soap request to: {}", url);
				MDC.clear();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
