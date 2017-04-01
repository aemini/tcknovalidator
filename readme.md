#T.C. Kimlik Numarası Doğrulama Bileşeni

Ad, soyad, doğum yılı ve T.C. kimlik numarası girdilerinin geçerliliğini Nüfus Müdürlüğü'nün servisleri üzerinden doğrulayan bileşen.

##1. pom.xml

    <dependency>
        <groupId>com.aryaemini.nvi</groupId>
        <artifactId>tckno-validator</artifactId>
        <version>${tcknoValidator.version}</version>
    </dependency>


##2. Gereklilikler
* classpath altında log4j.properties konfigürasyonu bulunmalıdır.

##3. Kullanım
```javaclass
Citizen citizen = new Citizen(12345678901L, "John", "Doe", 1970);
//OR
Citizen citizen = new Citizen();
citizen.setTckNo("12345678901");
citizen.setName("John");
citizen.setSurname("Doe");
citizen.setBirthYear("1970");

TCKNoValidator validator = new TCKNoValidator();
validator.setCitizen(citizen);

Boolean isValid;

try {
    isValid = validator.validate();
} catch (TCKNoValidationException e) {
    //e.printStackTrace();
}
```

##4. Değişiklikler
###1.1)
* Kodu düzenledim.
* Hata bildirimi için 2 istisna sınıfı ekledim.
* İşbu dokümanı oluşturdum.