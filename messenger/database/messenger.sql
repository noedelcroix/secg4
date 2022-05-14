BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "friends" (
	"user1"	INTEGER,
	"user2"	INTEGER,
	FOREIGN KEY("user1") REFERENCES "users"("id"),
	FOREIGN KEY("user2") REFERENCES "users"("id"),
	PRIMARY KEY("user1","user2")
);
CREATE TABLE IF NOT EXISTS "users" (
	"id"	INTEGER,
	"login"	TEXT NOT NULL UNIQUE,
	"password"	TEXT NOT NULL,
	"token"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "messages" (
	"id"	INTEGER,
	"sender"	INTEGER NOT NULL,
	"receiver"	INTEGER NOT NULL,
	"content"	TEXT NOT NULL,
	"date"	TEXT NOT NULL,
	FOREIGN KEY("receiver") REFERENCES "users"("id"),
	FOREIGN KEY("sender") REFERENCES "users"("id"),
	PRIMARY KEY("id" AUTOINCREMENT)
);
COMMIT;
