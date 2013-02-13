#/usr/bin/env python


class QuitException(Exception):
  def __init__(self, mesg):
    self.mesg = mesg
