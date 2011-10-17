""" SQLAlchemy model definitions for the FiLTH Postgres database.

The database schema is defined in sql/init_pg_database.sql

"""

from sqlalchemy import create_engine
from sqlalchemy import Column
from sqlalchemy import SmallInteger
from sqlalchemy import Text
from sqlalchemy import Table
from sqlalchemy import ForeignKey
from sqlalchemy.orm import Query
from sqlalchemy.orm import relationship
from sqlalchemy.orm import scoped_session
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.declarative import declarative_base


#create connection to db, session, declarative Base, etc.
engine = create_engine('postgresql://postgres:0o9i8u7y@localhost/test', echo=True)
Session = scoped_session(sessionmaker(bind=engine))
Base = declarative_base()
Base.metadata.bind = engine
Base.query = Session.query_property(Query)
session = Session()


#------------------------------------------------------------------------------

"""
crew_person

  cid serial NOT NULL,
  l_name text NOT NULL,
  f_name text DEFAULT NULL,
  m_name text DEFAULT NULL

  PRIMARY KEY (cid)
"""
class CrewPerson(Base):
  __tablename__ = 'crew_person'

  def __repr__(self):
    return "<CREW:\tcid:\t{0}\n\tfirst:\t{1}\n\tmiddle:\t{2}\n\tlast:\t{3}\n>".format(
            self.cid, self.f_name, self.m_name, self.l_name)


  #attributes
  cid = Column(SmallInteger, autoincrement=True, primary_key=True)
  l_name = Column(Text, nullable=False)
  f_name = Column(Text, default=None)
  m_name = Column(Text, default=None)

  #movies property defined by relationship() in Movie


#------------------------------------------------------------------------------

# movie <--> crew_person many-to-many relationship
worked_on = Table('worked_on', Base.metadata,
  Column('mid', SmallInteger, ForeignKey('movie.mid'), primary_key=True),
  Column('cid', SmallInteger, ForeignKey('crew_person.cid'), primary_key=True),
  Column('position', Text, primary_key=True)
)


#------------------------------------------------------------------------------

"""
genre

  gid serial NOT NULL,
  gen_name text NOT NULL

  PRIMARY KEY (gid)
"""
class Genre(Base):
  __tablename__ = 'genre'

  def __repr__(self):
    return "<GENRE:\tgid:\t{0}\n\tgenre:\t{1}\n>".format(self.gid, self.gen_name)


  #attributes
  gid = Column(SmallInteger, autoincrement=True, primary_key=True)
  gen_name = Column(Text, nullable=False)

  #movies property defined by relationship() in Movie


#------------------------------------------------------------------------------

# genre <--> movie many-to-many relationship
genre_contains = Table('genre_contains', Base.metadata,
  Column('mid', SmallInteger, ForeignKey('movie.mid'), primary_key=True),
  Column('gid', SmallInteger, ForeignKey('genre.gid'), primary_key=True)
)


#------------------------------------------------------------------------------
"""
oscar

  oid serial NOT NULL,
  ocategory text NOT NULL

  PRIMARY KEY (oid)
"""
class Oscar(Base):
  __tablename__ = 'oscar'

  def __repr__(self):
    return "<OSCAR:\toid:\t{0}\n\tcat:\t{1}\n>".format(self.oid, self.ocategory)


  #attributes
  oid = Column(SmallInteger, autoincrement=True, primary_key=True)
  ocategory = Column(Text, nullable=False)


#------------------------------------------------------------------------------

# oscar <--> movie many-to-many relationship
oscar_given_to = Table('oscar_given_to', Base.metadata,
  Column('mid', SmallInteger, ForeignKey('movie.mid'), primary_key=True),
  Column('oid', SmallInteger, ForeignKey('oscar.oid'), primary_key=True),
  Column('cid', SmallInteger, ForeignKey('crew_person.cid'), default=-1, primary_key=True),
  Column('year', SmallInteger, nullable=False),
  Column('ostatus', SmallInteger, nullable=False),
  Column('sharing_with', SmallInteger, default=None)
)


#------------------------------------------------------------------------------

"""
list

  lid serial NOT NULL,
  list_title text NOT NULL,
  list_author text DEFAULT NULL

  PRIMARY KEY (lid)
"""
class List(Base):
  __tablename__ = 'list'

  lid = Column(SmallInteger, autoincrement=True, primary_key=True)
  list_title = Column(Text, nullable=False)
  list_author = Column(Text, default=None)


#------------------------------------------------------------------------------

# list <--> movie many-to-to many relationship
list_contains = Table('list_contains', Base.metadata,
  Column('mid', SmallInteger, ForeignKey('movie.mid'), primary_key=True),
  Column('lid', SmallInteger, ForeignKey('list.lid'), primary_key=True),
  Column('rank', SmallInteger, default=None)
)


#------------------------------------------------------------------------------

"""
tyler

  tid serial NOT NULL,
  tcategory text NOT NULL

  PRIMARY KEY (tid)
"""
class Tyler(Base):
  __tablename__ = 'tyler'

  def __repr__(self):
    return "<TYLER:\ttid:\t{0}\n\tcat:\t{1}\n>".format(self.tid, self.tcategory)


  tid = Column(SmallInteger, autoincrement=True, primary_key=True)
  tcategory = Column(Text, nullable=False)


#------------------------------------------------------------------------------

# tyler <--> movie many-to-many relationship
tyler_given_to = Table('tyler_given_to', Base.metadata,
  Column('mid', SmallInteger, ForeignKey('movie.mid'), primary_key=True),
  Column('tid', SmallInteger, ForeignKey('tyler.tid'), primary_key=True),
  Column('cid', SmallInteger, ForeignKey('crew_person.cid'), default=-1, primary_key=True),
  Column('tstatus', SmallInteger, nullable=False),
  Column('scene_title', Text, default=None)
)


#------------------------------------------------------------------------------

class MovieMgr():
  """Contains non-instance methods for Movie activity."""

  @staticmethod
  def starRatingToString(rating):
    """Converts the given integer to its corresponding string star rating."""

    if   -2 == rating:
      return "not seen"
    elif -1 == rating:
      return "N/A"
    elif  0 == rating:
      return "NO STARS"
    elif  1 == rating:
      return "1/2*"
    elif  2 == rating:
      return "*"
    elif  3 == rating:
      return "*1/2"
    elif  4 == rating:
      return "**"
    elif  5 == rating:
      return "**1/2"
    elif  6 == rating:
      return "***"
    elif  7 == rating:
      return "***1/2"
    elif  8 == rating:
      return "****"

  @staticmethod
  def mpaaToString(rating):
    """Converts the given integer to its corresponding string MPAA rating."""

    if   0 == rating:
      return "NR"
    elif  1 == rating:
      return "G"
    elif  2 == rating:
      return "PG"
    elif  3 == rating:
      return "PG-13"
    elif  4 == rating:
      return "R"
    elif  5 == rating:
      return "X"
    elif  6 == rating:
      return "NC-17"


"""
movie

  mid serial NOT NULL,
  title text NOT NULL,
  year smallint NOT NULL,
  star_rating smallint DEFAULT NULL,
  mpaa smallint DEFAULT NULL,
  country text DEFAULT NULL,
  comments text DEFAULT NULL

  PRIMARY KEY (mid)
"""
class Movie(Base):
  __tablename__ = 'movie'

  def __repr__(self):
    return "<MOVIE:\tmid:\t{0}\n\ttitle:\t{1}\n\tyear:\t{2}\n\tstars:\t{3}\n\tmpaa:\t{4}\n\torigin:\t{5}\n\tnotes:\t{6}\n>".format(
            self.mid, self.title, self.year,
            MovieMgr.starRatingToString(self.star_rating),
            MovieMgr.mpaaToString(self.mpaa), self.country, self.comments)
      

  #attributes
  mid = Column(SmallInteger, autoincrement=True, primary_key=True)
  title = Column(Text, nullable=False)
  year = Column(SmallInteger, nullable=False)
  star_rating = Column(SmallInteger, default=None)
  mpaa = Column(SmallInteger, default=None)
  country = Column(Text, ForeignKey('country.country_name'), default=None)
  comments = Column(Text, default=None)

  #relationships
  crew_persons = relationship(CrewPerson, backref='movies', secondary=worked_on)
  genres = relationship(Genre, backref='movies', secondary=genre_contains)
  oscars = relationship(Oscar, secondary=oscar_given_to)
  lists  = relationship(List, backref='movies', secondary=list_contains)
  tylers = relationship(Tyler, secondary=tyler_given_to)






#------------------------------------------------------------------------------

"""
country

  country_name text NOT NULL

  PRIMARY KEY (country_name)
"""
class Country(Base):
  __tablename__ = 'country'

  def __repr__(self):
    return "<COUNTRY: {0}>".format(self.country_name)


  #attributes
  country_name = Column(Text, primary_key=True)

  #relationships
  movies = relationship(Movie)


#------------------------------------------------------------------------------
