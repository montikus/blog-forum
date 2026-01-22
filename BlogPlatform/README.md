# Multi-Author Blog CMS

Spring Boot 3 (Java 21) application with REST API and server-side UI (Thymeleaf + Bootstrap 5).

## Wymagania
- Java 21
- Maven Wrapper (wbudowany)

## Uruchomienie (PostgreSQL - domyslne)
Upewnij sie, ze PostgreSQL dziala:
- host: `localhost` (gdy aplikacja uruchamiana lokalnie)
- port: `5432`
- baza: `blog`
- uzytkownik: `blog`
- haslo: `blog`

Uruchomienie aplikacji:
```
./mvnw spring-boot:run
```
Po starcie:
- UI: http://localhost:8080
- API: http://localhost:8080/api/v1
- Panel admina: http://localhost:8080/admin
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## Konfiguracja PostgreSQL
Zmien dane w `application.yml`, jesli uruchamiasz baze na innych parametrach.

### Polaczenie z kontenerem `blogcms-db`
Jesli aplikacja dziala lokalnie, a baza w kontenerze, ustaw:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/blog
```

Jesli uruchamiasz aplikacje w Dockerze w tej samej sieci co kontener `blogcms-db`, ustaw:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://blogcms-db:5432/blog
```

## Testy (H2)
Testy korzystaja z H2 in-memory zdefiniowanego w `src/test/resources/application.yml`.

## Konta demo (z migracji Flyway)
Haslo dla wszystkich: `password`
- admin / admin@example.com (ADMIN)
- jan / jan@example.com (USER)
- ola / ola@example.com (USER)

## CSV import/eksport
Import wykonywany z panelu admina lub przez REST.

### Import uzytkownikow
Naglowki (takie same jak w eksporcie CSV):
```
nazwa_uzytkownika,email,haslo,rola
```
Przyklad:
```
jan,jan@example.com,sekret,USER
anna,anna@example.com,sekret,ADMIN
```

### Import postow
Naglowki (takie same jak w eksporcie CSV):
```
id,tytul,tresc,autorzy,srednia_ocena,liczba_komentarzy
```
`autorzy` to lista nazw uzytkownikow rozdzielona `;` (uzytkownicy musza istniec).
Pola `id`, `srednia_ocena` i `liczba_komentarzy` sa ignorowane przy imporcie.
Przyklad:
```
1,Nowy post,<p>Tresc</p>,jan;ola,0,0
```

### Eksport
- CSV: `/api/v1/admin/export/posts/csv`
- CSV uzytkownicy: `/api/v1/admin/export/users/csv` (kolumna `haslo` jest pusta)
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
