[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.aryaemini.nvi/tckno-validator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aryaemini.nvi/tckno-validator/)
# T.C. Kimlik Numarası Doğrulama Bileşeni

Ad, soyad, doğum yılı ve T.C. kimlik numarası girdilerinin geçerliliğini Nüfus Müdürlüğü'nün servisleri üzerinden doğrulayan bileşen. Kritik servislere abonelik veyahut faturaya yazılacak kimlik numarasını doğrulama maksadıyla kullanılabilir. Nüfus ve Vatantaşlık İşleri Genel Müdürlüğü servisleri üzerinden TC kimlik numarası veya kimlik kartı geçerlilik sonucu üretir.

## 1. pom.xml

    <dependency>
        <groupId>com.aryaemini.nvi</groupId>
        <artifactId>tckno-validator</artifactId>
        <version>1.5.0</version>
    </dependency>

## 2. Kullanım
`com.aryaemini.nvi.interfaces.IdentityCard` veya `com.aryaemini.nvi.interfaces.Person` arayüzlerini implemente eden nesnelerinizi validate() metoduna göndermeniz yeterlidir.

Interface kullanmak istemezseniz paket içerisinde sağlanan `com.aryaemini.nvi.model.Citizen.builder()` veya `com.aryaemini.nvi.model.Identity.builder()` modellerini kullanabilirsiniz.
 
Sorgulama sonucu `boolean` olarak döner. Hata durumunda `com.aryaemini.nvi.exception.TCKNoValidationException` fırlatılır ve false yanıt döndürülür. Üretilen loglar `TRACE` seviyesidir.
  
En basit anlamda kullanım örneği:
```java
TCKNoValidator validator = TCKNoValidator.getInstance();
var calendar = Calendar.getInstance();
calendar.set(Calendar.YEAR, 1970);
calendar.set(Calendar.MONTH, 1);
calendar.set(Calendar.DAY_OF_MONTH, 1); // 1970-01-01

var vatandas = Citizen.builder()
        .identityNumber("12345678900")
        .firstName("arya")
        .lastName("emini")
        .birthDate(calendar.getTime())
        .build();

var kimlikKarti = Identity.builder()
        .identityNumber("12523266658")
        .firstName("arya")
        .lastName("emini")
        .birthDate(calendar.getTime())
        .tckCardSerialNumber("a13d93562")
        .build();
 
Boolean isValidCitizen;
Boolean isValidIdCard;
 
try {
    isValidCitizen = validator.validate(vatandas);
    isValidIdCard = validator.validate(kimlikKarti);
    System.out.println("TC kimlik sorgulama sonucu: " + isValidCitizen);
    System.out.println("Kimlik veya Nüfus Cüzdanı sorgulama sonucu: " + isValidIdCard);
} catch (TCKNoValidationException e) {
    System.out.println("Hata sebebi: " + e.getMessage());
    e.printStackTrace();
}
```

## 4. Değişiklikler

### 1.5.1)
Yalnızca kod optimizasyonu yapıldı. Kullanımda bir değişiklik olmadı.

### 1.5.0)
* Java 17 ile çalışır hale getirdim.
* Vatandaş arayüzünü schema.org'daki Person şemasına benzetmeye çalıştım. Tam karşılamasa da daha yakın.
* Eski versiyonlarda arayüze implementasyon şartı vardı. Vatandaş ve kimlik kartı modellerine bir de builder ekledim.
