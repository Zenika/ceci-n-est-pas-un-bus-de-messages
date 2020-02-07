CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE public.messages (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	"date" timestamp NOT NULL DEFAULT now(),
	payload jsonb NOT NULL,
	CONSTRAINT messages_pk PRIMARY KEY (id)
);

CREATE OR REPLACE FUNCTION es_notify_messages()
 RETURNS TRIGGER
 LANGUAGE plpgsql
AS $$
DECLARE
  channel text := TG_ARGV[0];
BEGIN
  PERFORM (
     select pg_notify(channel, row_to_json(NEW)::text)
  );
  RETURN NULL;
END;
$$;

CREATE TRIGGER notify_messages
         AFTER INSERT
            ON messages
      FOR EACH ROW
       EXECUTE PROCEDURE es_notify_messages('messages');

