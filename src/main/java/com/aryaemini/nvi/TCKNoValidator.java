package com.aryaemini.nvi;

import org.apache.log4j.Logger;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import java.io.IOException;
import java.io.InputStream;

public class TCKNoValidator {

	private static final Logger logger = Logger.getLogger(TCKNoValidator.class);

	InputStream inputStream;

	private String tcKno, name, surname;
	private int birthYear;

	public TCKNoValidator(Citizen citizen) throws IOException {
		this.tcKno = citizen.getTckNo();
		this.name = citizen.getName();
		this.surname = citizen.getSurname();
		this.birthYear = citizen.getBirthYear();
	}

	private boolean localValidate(String tckNo) {
		int odds = 0, evens = 0, sum10 = 0;
		String char10, char11;

		odds += Integer.parseInt(tckNo.substring(0, 1));
		odds += Integer.parseInt(tckNo.substring(2, 3));
		odds += Integer.parseInt(tckNo.substring(4, 5));
		odds += Integer.parseInt(tckNo.substring(6, 7));
		odds += Integer.parseInt(tckNo.substring(8, 9));
		odds *= 7;

		evens += Integer.parseInt(tckNo.substring(1, 2));
		evens += Integer.parseInt(tckNo.substring(3, 4));
		evens += Integer.parseInt(tckNo.substring(5, 6));
		evens += Integer.parseInt(tckNo.substring(7, 8));

		char10 = Integer.toString((odds - evens) % 10);

		sum10 += Integer.parseInt(tckNo.substring(0, 1));
		sum10 += Integer.parseInt(tckNo.substring(1, 2));
		sum10 += Integer.parseInt(tckNo.substring(2, 3));
		sum10 += Integer.parseInt(tckNo.substring(3, 4));
		sum10 += Integer.parseInt(tckNo.substring(4, 5));
		sum10 += Integer.parseInt(tckNo.substring(5, 6));
		sum10 += Integer.parseInt(tckNo.substring(6, 7));
		sum10 += Integer.parseInt(tckNo.substring(7, 8));
		sum10 += Integer.parseInt(tckNo.substring(8, 9));
		sum10 += Integer.parseInt(tckNo.substring(9, 10));

		char11 = Integer.toString(sum10 % 10);

		if(!tckNo.substring(10,11).equals(char11) || !! !tckNo.substring(9,10).equals(char10)) {
			logger.error("Geçersiz T.C. kimlik numarası.");
			return false;
		}

		return true;
	}

	public boolean validate() {
		logger.info(this.tcKno + " yerelde sorgulanıyor.");
		if(localValidate(this.tcKno)) {
			logger.info("Yerel doğrulama başarılı. " + this.tcKno + " Nüfus müdürlüğünden sorgulanıyor.");
			return request();
		}
		return false;
	}

	private boolean request() {
		boolean result = false;
		try {
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			String url = "https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx";

			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);
			String sb = soapResponse.getSOAPBody().getTextContent();

			result = Boolean.valueOf(sb);

			soapConnection.close();
		} catch (Exception e) {
			logger.error("", e);
		}

		return result;
	}

	private SOAPMessage createSOAPRequest() throws Exception {
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

		tckNo.addTextNode(this.tcKno);
		name.addTextNode(this.name);
		surname.addTextNode(this.surname);
		birthYear.addTextNode(Integer.toString(this.birthYear));

		soapMessage.saveChanges();
		return soapMessage;
	}

}
