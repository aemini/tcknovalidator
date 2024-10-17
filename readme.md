![Maven Central Version](https://img.shields.io/maven-central/v/com.aryaemini.nvi/tckno-validator)
[![Security Rating](https://sonarqube.aryaemini.net/api/project_badges/measure?project=com.aryaemini.nvi%3Atckno-validator&metric=security_rating&token=sqb_a33c691e76737d6fc13f59aa2761d29aae568784)](https://sonarqube.aryaemini.net/dashboard?id=com.aryaemini.nvi%3Atckno-validator)
[![Reliability Rating](https://sonarqube.aryaemini.net/api/project_badges/measure?project=com.aryaemini.nvi%3Atckno-validator&metric=reliability_rating&token=sqb_a33c691e76737d6fc13f59aa2761d29aae568784)](https://sonarqube.aryaemini.net/dashboard?id=com.aryaemini.nvi%3Atckno-validator)
[![Coverage](https://sonarqube.aryaemini.net/api/project_badges/measure?project=com.aryaemini.nvi%3Atckno-validator&metric=coverage&token=sqb_a33c691e76737d6fc13f59aa2761d29aae568784)](https://sonarqube.aryaemini.net/dashboard?id=com.aryaemini.nvi%3Atckno-validator)
[![Bugs](https://sonarqube.aryaemini.net/api/project_badges/measure?project=com.aryaemini.nvi%3Atckno-validator&metric=bugs&token=sqb_a33c691e76737d6fc13f59aa2761d29aae568784)](https://sonarqube.aryaemini.net/dashboard?id=com.aryaemini.nvi%3Atckno-validator)
# T.C. Kimlik Numarası Doğrulama Bileşeni

Ad, soyad, doğum yılı ve T.C. kimlik numarası girdilerinin geçerliliğini Nüfus Müdürlüğü'nün servisleri üzerinden doğrulayan bileşen. Kritik servislere abonelik veyahut faturaya yazılacak kimlik numarasını doğrulama maksadıyla kullanılabilir. Nüfus ve Vatantaşlık İşleri Genel Müdürlüğü servisleri üzerinden TC kimlik numarası veya kimlik kartı geçerlilik sonucu üretir.

## 1. pom.xml

    <dependency>
        <groupId>com.aryaemini.nvi</groupId>
        <artifactId>tckno-validator</artifactId>
        <version>1.5.3</version>
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
        .identityNumber("12345678900")
        .firstName("arya")
        .lastName("emini")
        .birthDate(calendar.getTime())
        .tckCardSerialNumber("x00x00000")
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

## 3. Ayarlar

NVİ servislerinin adresinin değişme ihtimaline karşı url'lerin parametrik olması konusunda ricada bulunulması üzerine bu bölüm açıldı.
Kütüphane, herhangi bir konfigürasyon ihtiyacı olmadan çalışmaktadır, herhangi bir url tanımı yapılması gerekmemektedir.

Kütüphane yaşam döngüsüne başlarken öncelikle konfigürasyon dosyasına bakar (`System.getProperty()`). Bu, Spring Framework için `application.properties` veya `application.yaml` dosyasıdır.
Eğer başka bir çatı kullanıyorsanız ayarları sakladığınız dosyaya nitelikleri anahtar değer ikilisi olarak ekleyebilirsiniz.
Konfigürasyon dosyasında aşağıda tablodaki nitelikleri bulamazsa ortam değişkenkeri tablosundaki değerlere bakar.
Burada da herhangi bir tanım yoksa varsayılan değerlerle singleton olarak çalışır.

##### Örnek yaml
```yaml
...
com:
  aryaemini:
    nvi:
      url:
        identity-card: https://tckimlik.nvi.gov.tr/Service/KPSPublicV2.asmx
        person: https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx
...
```
##### Örnek properties
```properties
####################### T.C. Kimlik Kontrolu #######################
com.aryaemini.nvi.url.identity-card: https://tckimlik.nvi.gov.tr/Service/KPSPublicV2.asmx
com.aryaemini.nvi.url.person: https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx
####################### T.C. Kimlik Kontrolu #######################
```

#### Nitelikler

| Nitelik                             | Tip    | Varsayılan Değer                                     |
|:------------------------------------|:-------|:-----------------------------------------------------|
| com.aryaemini.nvi.url.identity-card | String | https://tckimlik.nvi.gov.tr/Service/KPSPublicV2.asmx |
| com.aryaemini.nvi.url.person        | String | https://tckimlik.nvi.gov.tr/Service/KPSPublic.asmx   |

#### Ortam Değişkenleri
| Değişken                                    | Tip    |
|:--------------------------------------------|:-------|
| TCKN_VALIDATOR_IDENTITY_CARD_VALIDATION_URL | String |
| TCKN_VALIDATOR_PERSON_VALIDATION_URL        | String |

## 4. KVKK Uyarısı
> [!IMPORTANT]
> Bu kütüphane, T.C. Kimlik Numarası doğrulama işlemleri için kullanılırken herhangi bir kişisel veri saklamaz veya depolamaz.
> Ancak, kütüphaneyi kullanan kişiler, Türkiye Cumhuriyeti Kanunları çerçevesinde "Veri Sorumlusu" olarak kabul edilebilir ve **6698 sayılı Kişisel Verilerin Korunması Kanunu (KVKK)** gerekliliklerine uymakla yükümlüdür. Lütfen kişisel veri işlemlerinde gerekli yasal düzenlemelere dikkat ediniz.

> [!CAUTION]
> Eğer `com.aryaemini.nvi.*` paketi `debug` (`fine`) veya daha düşük seviyede (`trace` | `finest`) loglanırsa hassas bilgiler de log kanalına iletilir.
> 
> URL değişim özelliği; yalnızca servis adresinin değişmesi ihtimalinde, geliştirme ve onaylanma sürecini beklemeden acil değişim imkanı sağlaması maksadıyla eklenmiştir. URL değişim özelliği veya hata ayıklama modunu kullanırken log kanalına aktarılan veya güvensiz bir URL'ye iletilen bu tür bilgilerin güvenliğinden ve korunmasından tamamen "**VERİ SORUMLUSU**" sıfatıyla kütüphaneyi kullanan kişi veya kurum sorumludur.
> 
> **6698 sayılı Kişisel Verilerin Korunması Kanunu** (KVKK) çerçevesinde, bu bilgilerin korunmasına yönelik gerekli tedbirlerin alınması yasal zorunluluktur. **Hata ayıklama modunu yalnızca güvenli bir ortamda kullanmanızı öneririz.**


## 5. Değişiklikler

### 1.5.3)
NullPointerException yakalamaya çalışırken NullPointerException yaratmışım, onu fixledim. Unit testler ekledim. Kullanımda değişiklik yok.

### 1.5.2)
Servis URL'sinin değiştirilebilmesi için gereken ayarlar yapıldı. Kullanımda değişiklik yok.

### 1.5.1)
Yalnızca kod optimizasyonu yapıldı. Kullanımda bir değişiklik olmadı.

### 1.5.0)
* Java 17 ile çalışır hale getirdim.
* Vatandaş arayüzünü schema.org'daki Person şemasına benzetmeye çalıştım. Tam karşılamasa da daha yakın.
* Eski versiyonlarda arayüze implementasyon şartı vardı. Vatandaş ve kimlik kartı modellerine bir de builder ekledim.

[![Quality gate](https://sonarqube.aryaemini.net/api/project_badges/quality_gate?project=com.aryaemini.nvi%3Atckno-validator&token=sqb_a33c691e76737d6fc13f59aa2761d29aae568784)](https://sonarqube.aryaemini.net/dashboard?id=com.aryaemini.nvi%3Atckno-validator)