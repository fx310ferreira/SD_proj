CREATE TABLE words (
	id	 BIGSERIAL,
	word VARCHAR(128) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE links (
	id	 BIGSERIAL,
	link	 VARCHAR(512) NOT NULL,
	searches BIGINT NOT NULL DEFAULT 0,
	PRIMARY KEY(id)
);

CREATE TABLE words_links (
	count	 INTEGER,
	links_id BIGINT,
	words_id BIGINT,
	PRIMARY KEY(links_id,words_id)
);

CREATE TABLE links_links (
	links_id	 BIGINT,
	links_id1 BIGINT,
	PRIMARY KEY(links_id,links_id1)
);

ALTER TABLE words ADD UNIQUE (word);
ALTER TABLE links ADD UNIQUE (link);
ALTER TABLE words_links ADD CONSTRAINT words_links_fk1 FOREIGN KEY (links_id) REFERENCES links(id);
ALTER TABLE words_links ADD CONSTRAINT words_links_fk2 FOREIGN KEY (words_id) REFERENCES words(id);
ALTER TABLE links_links ADD CONSTRAINT links_links_fk1 FOREIGN KEY (links_id) REFERENCES links(id);
ALTER TABLE links_links ADD CONSTRAINT links_links_fk2 FOREIGN KEY (links_id1) REFERENCES links(id);
