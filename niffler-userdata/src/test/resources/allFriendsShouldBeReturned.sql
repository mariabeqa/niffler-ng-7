INSERT INTO public."user" (id, username, currency, firstname, surname, photo, photo_small, full_name)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d629856', 'sasha', 'RUB', null, null,
        null, null,null);

INSERT INTO public."user" (id, username, currency, firstname, surname, photo, photo_small, full_name)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d621928', 'bill', 'RUB', null, null,
        null, null,null);

INSERT INTO public."user" (id, username, currency, firstname, surname, photo, photo_small, full_name)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d624576', 'anna', 'RUB', null, null,
        null, null,null);

INSERT INTO public.friendship (requester_id, addressee_id, status, created_date)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d629856', 'a9165b45-a4aa-47d6-ac50-43611d621928', 'ACCEPTED', CURRENT_DATE);

INSERT INTO public.friendship (requester_id, addressee_id, status, created_date)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d621928', 'a9165b45-a4aa-47d6-ac50-43611d629856', 'ACCEPTED', CURRENT_DATE);

INSERT INTO public.friendship (requester_id, addressee_id, status, created_date)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d629856', 'a9165b45-a4aa-47d6-ac50-43611d624576', 'ACCEPTED', CURRENT_DATE);

INSERT INTO public.friendship (requester_id, addressee_id, status, created_date)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d624576', 'a9165b45-a4aa-47d6-ac50-43611d629856', 'ACCEPTED', CURRENT_DATE);