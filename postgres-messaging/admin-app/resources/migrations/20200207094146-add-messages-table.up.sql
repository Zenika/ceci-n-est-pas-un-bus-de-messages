CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE public.messages (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	"date" timestamp NOT NULL DEFAULT now(),
	payload jsonb NOT NULL,
	CONSTRAINT messages_pk PRIMARY KEY (id)
);