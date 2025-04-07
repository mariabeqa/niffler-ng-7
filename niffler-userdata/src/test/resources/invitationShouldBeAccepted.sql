INSERT INTO public."user" (id, username, currency, firstname, surname, photo, photo_small, full_name)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d629812', 'duck', 'RUB', null, null,
        null,
        null,
        null);

INSERT INTO public."user" (id, username, currency, firstname, surname, photo, photo_small, full_name)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d623089', 'snake', 'RUB', null, null,
        null,
        null,
        null);


INSERT INTO public.friendship (requester_id, addressee_id, status, created_date)
VALUES ('a9165b45-a4aa-47d6-ac50-43611d629812', 'a9165b45-a4aa-47d6-ac50-43611d623089', 'PENDING', CURRENT_DATE);