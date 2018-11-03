[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.aryaemini.nvi/tckno-validator/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.aryaemini.nvi/tckno-validator/)
# T.C. Kimlik Numarası Doğrulama Bileşeni

Ad, soyad, doğum yılı ve T.C. kimlik numarası girdilerinin geçerliliğini Nüfus Müdürlüğü'nün servisleri üzerinden doğrulayan bileşen. Kritik servislere abonelik veyahut faturaya yazılacak kimlik numarasını doğrulama maksadıyla kullanılabilir. Nüfus ve Vatantaşlık İşleri Genel Müdürlüğü servisleri üzerinden TC kimlik numarası veya kimlik kartı geçerlilik sonucu üretir.

## 1. pom.xml

    <dependency>
        <groupId>com.aryaemini.nvi</groupId>
        <artifactId>tckno-validator</artifactId>
        <version>1.4</version>
    </dependency>

## 2. Kullanım
com.aryaemini.nvi.interfaces.Citizen veya com.aryaemini.nvi.interfaces.IdentityCard arayüzlerine (interface) uyan (implements) nesnelerinizi validate() metoduna göndermeniz yeterlidir. Kimlik kartı nesnesinde kimlikte mevcut olan alanları aslına uygun doldurursanız, boş olan alanların boş olduğunu servise bildirecektir.
 
Sorgulama sonucu boolean olarak döner. Hata durumunda com.aryaemini.nvi.exception.TCKNoValidationException fırlatılır ve false yanıt döndürülür. DEBUG seviyesinde log gönderir.
  
```java
public class Vatandas implements Citizen {

    public Long getTckNo() {
        return tckNo;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

}
```
 
```java
public class KimlikKarti implements IdentityCard {

    public Integer getBirthDay() {
        return birthDay;
    }

    public Integer getBirthMonth() {
        return birthMonth;
    }

    public Integer getIdCardNumber() {
        return idCardNumber;
    }

    public String getIdCardSerial() {
        return idCardSerial;
    }

    public String getTckCardSerialNumber() {
        return tckCardSerialNumber;
    }

    public boolean isSurnameNotSpecified() {
        return (surname == null || surname.length() == 0);
    }

    public boolean isBirthDayNotSpecified() {
        return birthDay == null || birthDay == 0;
    }

    public boolean isBirthMonthNotSpecified() {
        return birthMonth == null || birthMonth == 0;
    }

    public Long getTckNo() {
        return tckNo;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

}
```
 
```java
TCKNoValidator validator = TCKNoValidator.getInstance();
 
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

### 1.4)
* Arayüz (interface) kullanımı ile kullanıcıların kendi nesnelerini direkt gönderebilmeleri sağlandı.
