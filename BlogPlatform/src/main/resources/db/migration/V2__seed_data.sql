insert into uzytkownicy (username, email, haslo_hash, rola, created_at) values
	('admin', 'admin@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5eGQG1v6Y8WvY6q4Z87Z84qxdjQba1C', 'ADMIN', current_timestamp),
	('jan', 'jan@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5eGQG1v6Y8WvY6q4Z87Z84qxdjQba1C', 'USER', current_timestamp),
	('ola', 'ola@example.com', '$2a$10$7EqJtq98hPqEX7fNZaFWoO5eGQG1v6Y8WvY6q4Z87Z84qxdjQba1C', 'USER', current_timestamp);

insert into posty (tytul, tresc, created_at, updated_at) values
	('Pierwszy post', '<p>Witaj!</p>', current_timestamp, current_timestamp),
	('Drugi post', '<p>Druga tresc</p>', current_timestamp, current_timestamp);

insert into post_autorzy (post_id, uzytkownik_id)
select p.id, u.id
from posty p
join uzytkownicy u on p.tytul = 'Pierwszy post' and u.username = 'jan'
union all
select p.id, u.id
from posty p
join uzytkownicy u on p.tytul = 'Drugi post' and u.username = 'jan'
union all
select p.id, u.id
from posty p
join uzytkownicy u on p.tytul = 'Drugi post' and u.username = 'ola';

insert into komentarze (tresc, created_at, post_id, autor_id)
select 'Super wpis', current_timestamp, p.id, u.id
from posty p
join uzytkownicy u on p.tytul = 'Pierwszy post' and u.username = 'ola';

insert into oceny (wartosc, post_id, uzytkownik_id)
select 5, p.id, u.id
from posty p
join uzytkownicy u on p.tytul = 'Pierwszy post' and u.username = 'ola';

insert into wiadomosci (tresc, sent_at, nadawca_id, odbiorca_id, post_id)
select 'Czesc, gratulacje!', current_timestamp, nadawca.id, odbiorca.id, p.id
from uzytkownicy nadawca
join uzytkownicy odbiorca on odbiorca.username = 'jan'
join posty p on p.tytul = 'Pierwszy post'
where nadawca.username = 'ola';
