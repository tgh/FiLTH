-----------------------------
-- Primary Key constraints --
-----------------------------

ALTER TABLE movie ADD CONSTRAINT movie_pkey PRIMARY KEY(title, year);
ALTER TABLE director ADD CONSTRAINT director_pkey PRIMARY KEY(dirname);
ALTER TABLE actor ADD CONSTRAINT actor_pkey PRIMARY KEY(actname);
ALTER TABLE screenwriter ADD CONSTRAINT screenwriter_pkey PRIMARY KEY(scrname);
ALTER TABLE cinematographer ADD CONSTRAINT cinematographer_pkey PRIMARY KEY(cinname);
ALTER TABLE oscar ADD CONSTRAINT oscar_pkey PRIMARY KEY(title, year, category);
ALTER TABLE list ADD CONSTRAINT list_pkey PRIMARY KEY(listtitle);
ALTER TABLE directed ADD CONSTRAINT directed_pkey PRIMARY KEY(title, year, dirname);
ALTER TABLE actedin ADD CONSTRAINT actedin_pkey PRIMARY KEY(title, year, actname);
ALTER TABLE wrote ADD CONSTRAINT wrote_pkey PRIMARY KEY(title, year, scrname);
ALTER TABLE shot ADD CONSTRAINT shot_pkey PRIMARY KEY(title, year, cinname);
ALTER TABLE listcontains ADD CONSTRAINT listcontains_pkey PRIMARY KEY(title, year, listtitle);

-----------------------------
-- Foreign Key constraints --
-----------------------------

-- oscar table
ALTER TABLE oscar ADD CONSTRAINT oscar_movie_fkey
FOREIGN KEY (title, year) REFERENCES movie(title, year)
ON UPDATE CASCADE ON DELETE SET NULL;

-- directed table
ALTER TABLE directed ADD CONSTRAINT dir_movie_fkey
FOREIGN KEY (title, year) REFERENCES movie(title, year)
ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE directed ADD CONSTRAINT dir_dir_fkey
FOREIGN KEY (dirname) REFERENCES director(dirname)
ON UPDATE CASCADE ON DELETE SET NULL;

-- actedin table
ALTER TABLE actedin ADD CONSTRAINT act_movie_fkey
FOREIGN KEY (title, year) REFERENCES movie(title, year)
ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE actedin ADD CONSTRAINT act_act_fkey
FOREIGN KEY (actname) REFERENCES actor(actname)
ON UPDATE CASCADE ON DELETE SET NULL;

-- wrote table
ALTER TABLE wrote ADD CONSTRAINT wrote_movie_fkey
FOREIGN KEY (title, year) REFERENCES movie(title, year)
ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE wrote ADD CONSTRAINT wrote_act_fkey
FOREIGN KEY (scrname) REFERENCES screenwriter(scrname)
ON UPDATE CASCADE ON DELETE SET NULL;

-- shot table
ALTER TABLE shot ADD CONSTRAINT shot_movie_fkey
FOREIGN KEY (title, year) REFERENCES movie(title, year)
ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE shot ADD CONSTRAINT shot_act_fkey
FOREIGN KEY (cinname) REFERENCES cinematographer(cinname)
ON UPDATE CASCADE ON DELETE SET NULL;

-- listcontains
ALTER TABLE listcontains ADD CONSTRAINT list_movie_fkey
FOREIGN KEY (title, year) REFERENCES movie(title, year)
ON UPDATE CASCADE ON DELETE SET NULL;

ALTER TABLE listcontains ADD CONSTRAINT list_act_fkey
FOREIGN KEY (listtitle) REFERENCES list(listtitle)
ON UPDATE CASCADE ON DELETE SET NULL;

---------------------------
-- Integrity constraints --
---------------------------

-- movie year
ALTER TABLE movie ADD CONSTRAINT year_constraint
CHECK (year >= 1900 AND year <= 2012);

-- movie star rating
ALTER TABLE movie ADD CONSTRAINT star_constraint
CHECK (mystarrating = 'NO STARS' OR
       mystarrating = '&frac12*' OR
       mystarrating = '*' OR
       mystarrating = '*&frac12' OR
       mystarrating = '**' OR
       mystarrating = '**&frac12' OR
       mystarrating = '***' OR
       mystarrating = '***&frac12' OR
       mystarrating = '****' OR
       mystarrating = 'N/A');

-- movie view status
ALTER TABLE movie ADD CONSTRAINT view_constraint
CHECK (viewstatus = 'seen' OR
       viewstatus = 'not seen' OR
       viewstatus = 'want to see');

-- listcontains rank
ALTER TABLE listcontains ADD CONSTRAINT rank_constraint
CHECK (rankinlist > 0);

-- oscar category
ALTER TABLE oscar ADD CONSTRAINT category_constraint
CHECK (category = 'Best Picture' OR
       category = 'Best Actor' OR
       category = 'Best Actress' OR
       category = 'Best Supporting Actor' OR
       category = 'Best Supporting Actress' OR
       category = 'Best Director' OR
       category = 'Best Cinematography' OR
       category = 'Best Adapted Screenplay' OR
       category = 'Best Original Screenplay' OR
       category = 'Best Foreign Language Film');

-- oscar status
ALTER TABLE oscar ADD CONSTRAINT status_constraint
CHECK (status = 'won' OR
       status = 'nominated');
