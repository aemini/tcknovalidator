# T.C. Kimlik Numarası Doğrulama Bileşeni

Ad, soyad, doğum yılı ve T.C. kimlik numarası girdilerinin geçerliliğini Nüfus Müdürlüğü'nün servisleri üzerinden doğrulayan bileşen.

## 1. pom.xml

    <dependency>
        <groupId>com.aryaemini.nvi</groupId>
        <artifactId>tckno-validator</artifactId>
        <version>1.2</version>
    </dependency>


## 2. Gereklilikler
* classpath altında log4j.properties konfigürasyonu bulunmalıdır.

## 3. Kullanım
```java
Citizen citizen = new Citizen(12345678901L, "John", "Doe", 1970);
//OR
Citizen citizen = new Citizen();
citizen.setTckNo("12345678901");
citizen.setName("John");
citizen.setSurname("Doe");
citizen.setBirthYear("1970");
 

IdentityCard identityCard = new IdentityCard();
identityCard.setTckNo("12345678901");
identityCard.setName("John");
identityCard.setSurname("Doe"); //null or empty string if not specified
identityCard.setBirthDay(1); //0 if not specified
identityCard.setBirthMonth(1); //0 if not specified
identityCard.setBirthYear(1970);
//
identityCard.setIdCardSerial("a00");
identityCard.setIdCardNumber(111111);
//OR
identityCard.setTckCardSerialNumber("serial");
 
TCKNoValidator validator = new TCKNoValidator();
 
Boolean isValidCitizen;
Boolean isValidIdCard;
 
try {
    isValidCitizen = validator.validate(citizen);
    isValidIdCard = validator.validate(identityCard);
} catch (TCKNoValidationException e) {
    //e.printStackTrace();
}
```

## 4. Değişiklikler
### 1.1)
* Kodu düzenledim.
* Hata bildirimi için 2 istisna sınıfı ekledim.
* İşbu dokümanı oluşturdum.

### 1.2)
* Nüfus cüzdanı ve T.C. Kimlik Kartı doğrulama eklendi