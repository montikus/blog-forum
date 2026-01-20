create table uzytkownicy (
	id bigserial primary key,
	username varchar(100) not null,
	email varchar(255) not null,
	haslo_hash varchar(255) not null,
	rola varchar(20) not null,
	created_at timestamp not null default current_timestamp,
	constraint uzytkownicy_username_uk unique (username),
	constraint uzytkownicy_email_uk unique (email)
);

create table posty (
	id bigserial primary key,
	tytul varchar(200) not null,
	tresc text not null,
	created_at timestamp not null default current_timestamp,
	updated_at timestamp not null default current_timestamp
);

create table post_autorzy (
	post_id bigint not null,
	uzytkownik_id bigint not null,
	primary key (post_id, uzytkownik_id),
	constraint post_autorzy_post_fk foreign key (post_id) references posty(id) on delete cascade,
	constraint post_autorzy_uzytkownik_fk foreign key (uzytkownik_id) references uzytkownicy(id) on delete cascade
);

create table komentarze (
	id bigserial primary key,
	tresc text not null,
	created_at timestamp not null default current_timestamp,
	post_id bigint not null,
	autor_id bigint not null,
	constraint komentarze_post_fk foreign key (post_id) references posty(id) on delete cascade,
	constraint komentarze_autor_fk foreign key (autor_id) references uzytkownicy(id) on delete cascade
);

create table oceny (
	id bigserial primary key,
	wartosc integer not null,
	post_id bigint not null,
	uzytkownik_id bigint not null,
	constraint oceny_post_fk foreign key (post_id) references posty(id) on delete cascade,
	constraint oceny_uzytkownik_fk foreign key (uzytkownik_id) references uzytkownicy(id) on delete cascade,
	constraint oceny_post_uzytkownik_uk unique (post_id, uzytkownik_id),
	constraint oceny_wartosc_chk check (wartosc between 1 and 5)
);

create table wiadomosci (
	id bigserial primary key,
	tresc text not null,
	sent_at timestamp not null default current_timestamp,
	nadawca_id bigint not null,
	odbiorca_id bigint not null,
	post_id bigint,
	constraint wiadomosci_nadawca_fk foreign key (nadawca_id) references uzytkownicy(id),
	constraint wiadomosci_odbiorca_fk foreign key (odbiorca_id) references uzytkownicy(id),
	constraint wiadomosci_post_fk foreign key (post_id) references posty(id) on delete set null
);

create index idx_komentarze_post_id on komentarze(post_id);
create index idx_oceny_post_id on oceny(post_id);
create index idx_wiadomosci_odbiorca_id on wiadomosci(odbiorca_id);
create index idx_wiadomosci_nadawca_id on wiadomosci(nadawca_id);
