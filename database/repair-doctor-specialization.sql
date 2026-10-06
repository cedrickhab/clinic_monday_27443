BEGIN;

ALTER TABLE public.doctor_specialization
    DROP CONSTRAINT IF EXISTS fkjomy2lb0v98ooerv9bamdmkxo;

ALTER TABLE public.doctor_specialization
    DROP CONSTRAINT IF EXISTS fkt8l0jah40e56255ypxxndvnvg;

COMMIT;
