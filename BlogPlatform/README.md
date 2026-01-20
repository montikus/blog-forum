# Multi-Author Blog CMS

Spring Boot 3 (Java 21) application with REST API and server-side UI (Thymeleaf + Bootstrap 5).

## Wymagania
- Java 21
- Maven Wrapper (wbudowany)

## Uruchomienie lokalne (H2)
```
./mvnw spring-boot:run
```
Po starcie:
- UI: http://localhost:8080
- API: http://localhost:8080/api/v1
- Panel admina: http://localhost:8080/admin

## Profil produkcyjny (PostgreSQL)
Ustaw w `application.yml` dane do bazy, a nastepnie:
```
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Konta demo (z migracji Flyway)
Haslo dla wszystkich: `password`
- admin / admin@example.com (ADMIN)
- jan / jan@example.com (USER)
- ola / ola@example.com (USER)

## CSV import/eksport
Import wykonywany z panelu admina lub przez REST.

### Import uzytkownikow
Naglowki:
```
nazwa_uzytkownika,email,haslo,rola
```
Przyklad:
```
jan,jan@example.com,sekret,USER
anna,anna@example.com,sekret,ADMIN
```

### Import postow
Naglowki:
```
tytul,tresc,autorzy
```
`autorzy` to lista nazw uzytkownikow rozdzielona `;` (uzytkownicy musza istniec).
Przyklad:
```
Nowy post,<p>Tresc</p>,jan;ola
```

### Eksport
- CSV: `/api/v1/admin/export/posts/csv`
- PDF (lista): `/api/v1/admin/export/posts/pdf`
- PDF (pojedynczy): `/api/v1/admin/export/posts/{idPosta}/pdf`

## Dokumentacja API (Spring REST Docs)
Generowana w trakcie `verify`:
```
./mvnw -q verify
```
Wynik: `target/generated-docs/index.html`

## Testy i pokrycie
```
./mvnw -q verify
```
JaCoCo wymaga minimum 70% pokrycia instrukcji.
