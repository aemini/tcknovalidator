package com.aryaemini.nvi;

import com.aryaemini.nvi.exception.EmptyFieldException;
import com.aryaemini.nvi.exception.TCKNoValidationException;
import com.aryaemini.nvi.model.Citizen;
import org.apache.log4j.Logger;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class TCKNoValidator {

	private static final Logger logger = Logger.getLogger(TCKNoValidator.class);

	Citizen citizen;

	public void setCitizen(Citizen citizen) {
		this.citizen = citizen;
	}

	private Boolean localValidate(String tckNo) {
		Boolean result;
		result = false;
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
				logger.info("Geçersiz T.C. kimlik numarası.");
				return false;
			}

			result = true;
		} catch (StringIndexOutOfBoundsException e) {
			logger.warn("T.C. kimlik numarası 11 haneli olmalıdır.");
		}

		return result;
	}

	public Boolean validate() throws TCKNoValidationException {
		Boolean result;
		result = false;
		try {
			if(localValidate(this.citizen.getTckNo().toString())) {
				logger.info("T.C. kimlik numarası algoritması geçerli.");
				result = request();
				if (result) {
					logger.info("T.C. kimlik numarası geçerlidir.");
				} else {
					logger.info("T.C. kimlik numarası kişiye ait değildir.");
				}
			}
		} catch (NullPointerException e) {
			logger.warn("Lütfen tüm alanları doldurun.");
		}
		return result;
	}

	private Boolean request() throws TCKNoValidationException {
		logger.debug("Nüfus müdürlüğünden sorgulamaya hazırlanılıyor.");
		Boolean result;
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			String url = "https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx";

			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(this.citizen), url);
			String sb = soapResponse.getSOAPBody().getTextContent();
			result = Boolean.valueOf(sb);
			soapConnection.close();
		} catch (EmptyFieldException e) {
			throw new TCKNoValidationException("Doldurulmamış alanlar bulunuyor. T.C. kimlik numarası doğrulaması yapılmadı.", e);
		} catch (SOAPException e) {
			throw new TCKNoValidationException("Nüfus müdürlüğü'nden beklenmedik yanıt alındı. İşlem tamamlanamadı.", e);
		}

		return result;
	}

	private SOAPMessage createSOAPRequest(Citizen citizen) throws EmptyFieldException, SOAPException {
		logger.debug("Sorgulama isteği oluşturuluyor.");
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
			logger.warn("Doldurulmamış alanlar bulunmaktadır. Lütfen tüm alanları doldurun.");
			throw new EmptyFieldException("Lütfen tüm alanları doldurun", e);
		}

		soapMessage.saveChanges();
		return soapMessage;
	}

}
