create database exam;

CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);


CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

INSERT into company (id, name) values (1, 'Gamdev');
INSERT into company (id, name) values (2, 'Money');
INSERT into company (id, name) values (3, 'Fortune');
INSERT into company (id, name) values (4, 'Java');
INSERT into company (id, name) values (5, 'Target');
INSERT into company (id, name) values (6, 'Orange');


INSERT into person (id, name, company_id) values (1, 'Ivan', 1);
INSERT into person (id, name, company_id) values (2, 'Nvan', 2);
INSERT into person (id, name, company_id) values (3, 'Inga', 1);
INSERT into person (id, name, company_id) values (4, 'Chad', 3);
INSERT into person (id, name, company_id) values (5, 'IRacoon', 4);
INSERT into person (id, name, company_id) values (6, 'Porange', 5);
INSERT into person (id, name, company_id) values (7, 'Dalaran', 6);
INSERT into person (id, name, company_id) values (8, 'Iv', 1);
INSERT into person (id, name, company_id) values (9, 'Van', 2);
INSERT into person (id, name, company_id) values (10, 'Harington', 3);
INSERT into person (id, name, company_id) values (11, 'Billy', 4);
INSERT into person (id, name, company_id) values (12, 'Sephra', 5);
INSERT into person (id, name, company_id) values (13, 'Ezra', 6);
INSERT into person (id, name, company_id) values (14, 'Angel', 5);
INSERT into person (id, name, company_id) values (15, 'TestBot', 2);
INSERT into person (id, name, company_id) values (16, 'Klaus', 3);


/*
1. В одном запросе получить

- имена всех person, которые не состоят в компании с id = 5;

- название компании для каждого человека.
*/

SELECT p.name as сотрудник, c.name as компания, p.company_id from company c
inner join person p
on c.id = p.company_id
where c.id != 5;

/*
2. Необходимо выбрать название компании с максимальным количеством человек + количество человек в этой компании
(нужно учесть, что таких компаний может быть несколько).

*/

SELECT count(p.company_id) as n_people,  c.name as название
from person p
join company as c
on c.id = p.company_id
group by c.name
having max(p.company_id) =
(select person.company_id
from person group by person.company_id
limit 1)



