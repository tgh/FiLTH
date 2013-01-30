#/usr/bin/env python


class MovieTagger(object):

  def __init__(self, tags):
    self.setTags(tags)
    self._initTagMap()
    

  def setTags(self, tags):
    self.tags = tags


  def _initTagMap(self):
    for tag in self.tags:
      self.tagMap[int(tag.tid)] = str(tag.tag_name)
