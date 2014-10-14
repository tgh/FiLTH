""" SQLAlchemy model definitions for the FiLTH Postgres database.

The database schema is defined in sql/init_pg_database.sql

"""
from sqlalchemy import create_engine
from sqlalchemy import Column
from sqlalchemy import SmallInteger
from sqlalchemy import Text
from sqlalchemy import Unicode
from sqlalchemy import Table
from sqlalchemy import ForeignKey
from sqlalchemy.orm import Query
from sqlalchemy.orm import relationship
from sqlalchemy.orm import scoped_session
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.schema import UniqueConstraint


#create connection to db, session, declarative Base, etc.
engine = create_engine('postgresql://xxx:xxx@localhost/filth', echo=False)
Session = scoped_session(sessionmaker(bind=engine, autoflush=True))
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
  known_as = Column(Text, ForeignKey('position.position_title'), default=None)

  #movies property defined by relationship() in Movie


#------------------------------------------------------------------------------

# movie <--> crew_person many-to-many relationship
worked_on = Table('worked_on', Base.metadata,
  Column('mid', SmallInteger, ForeignKey('movie.mid'), primary_key=True),
  Column('cid', SmallInteger, ForeignKey('crew_person.cid'), primary_key=True),
  Column('position', Text, ForeignKey('position.position_title'), primary_key=True)
)


#------------------------------------------------------------------------------

"""
position

  position_title text NOT NULL

  PRIMARY KEY (position_title)
"""
class Position(Base):
  __tablename__ = 'position'

  def __repr__(self):
    return "<POSITION: {0}>".format(self.position_title)


  #attributes
  position_title = Column(Text, primary_key=True)

  #relationships
  crew_persons = relationship(CrewPerson)


#------------------------------------------------------------------------------

"""
tag

  tid serial NOT NULL,
  tag_name text NOT NULL

  PRIMARY KEY (tid)
"""
class Tag(Base):
  __tablename__ = 'tag'

  def __repr__(self):
    return "<TAG:\ttid:\t{0}\n\tag:\t{1}\n>".format(self.tid, self.tag_name)


  #attributes
  tid = Column(SmallInteger, autoincrement=True, primary_key=True)
  tag_name = Column(Text, nullable=False)

  #movies property defined by relationship() in Movie


#------------------------------------------------------------------------------

# tag <--> movie many-to-many relationship
tag_given_to = Table('tag_given_to', Base.metadata,
  Column('mid', SmallInteger, ForeignKey('movie.mid'), primary_key=True),
  Column('tid', SmallInteger, ForeignKey('tag.tid'), primary_key=True)
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
  pass


#------------------------------------------------------------------------------


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
            self.mid, self.title, self.year, self.star_rating, self.mpaa, self.country, self.comments)
      

  #attributes
  mid = Column(SmallInteger, autoincrement=True, primary_key=True)
  title = Column(Text, nullable=False)
  year = Column(SmallInteger, nullable=False)
  star_rating = Column(Text, ForeignKey('star_rating.rating'), default=None)
  mpaa = Column(Text, ForeignKey('mpaa.rating'), default=None)
  country = Column(Text, ForeignKey('country.country_name'), default=None)
  comments = Column(Text, default=None)

  #relationships
  crew_persons = relationship(CrewPerson, backref='movies', secondary=worked_on)
  tags = relationship(Tag, backref='movies', secondary=tag_given_to)
  oscars = relationship(Oscar, secondary=oscar_given_to)
  lists  = relationship(List, backref='movies', secondary=list_contains)
  tylers = relationship(Tyler, secondary=tyler_given_to)

  #unique constraint
  UniqueConstraint('title', 'year', name='movie_unique_constraint')


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


"""
star_rating

  rating text NOT NULL

  PRIMARY KEY (rating)
"""
class StarRating(Base):
  __tablename__ = 'star_rating'

  def __repr__(self):
    return "<STAR_RATING: {0}>".format(self.rating)


  #attributes
  rating = Column(Text, primary_key=True)

  #relationships
  movies = relationship(Movie)


#------------------------------------------------------------------------------


"""
mpaa

  rating text NOT NULL

  PRIMARY KEY (rating)
"""
class Mpaa(Base):
  __tablename__ = 'mpaa'

  def __repr__(self):
    return "<MPAA: {0}>".format(self.rating)


  #attributes
  rating = Column(Text, primary_key=True)

  #relationships
  movies = relationship(Movie)


#------------------------------------------------------------------------------
