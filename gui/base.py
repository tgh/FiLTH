import wx
ID_ABOUT = 101
ID_EXIT = 110


class MainWindow(wx.Frame):

  def __init__(self, parent, id, title):
    """Initializer."""

    # NO_FULL_REPAINT_ON_RESIZE means text won't be redrawn on window resize
    wx.Frame.__init__(self, parent, wx.ID_ANY, title, size = (700, 450),
        style = wx.DEFAULT_FRAME_STYLE | wx.NO_FULL_REPAINT_ON_RESIZE)
    # TE_MULTILINE means a multiline text box to type in
    self.control = wx.TextCtrl(self, 1, style = wx.TE_MULTILINE)
    self.CreateStatusBar()
    #--------- Setting up the menu.
    filemenu = wx.Menu()
    # the '&' before 'About' means that the 'A' will be keyboard accessible
    #  in the menu, and the second string is what will appear in the status
    #  bar
    filemenu.Append(ID_ABOUT, "&About", "Information about this program")
    filemenu.AppendSeparator()
    filemenu.Append(ID_EXIT, "E&xit", "Terminate the program")
    #--------- Creating the menu.
    menubar = wx.MenuBar()
    menubar.Append(filemenu, "&File")
    self.SetMenuBar(menubar)
    #--------- Setting menu event handlers
    wx.EVT_MENU(self, ID_ABOUT, self.OnAbout)
    wx.EVT_MENU(self, ID_EXIT, self.OnExit)
    self.Show(True)


  #----- START EVENT HANDLERS ----------
  def OnAbout(self, event):
    """Event for when 'About' is clicked in 'File' menu."""

    # create a message dialog box. Note: if the wx.OK parameter were not
    #  there, the default behavior would show a 'Cancel' and 'OK' button.
    d = wx.MessageDialog(self, " A simple editor\nin wxPython ",
        "About Sample Editor", wx.OK)
    # show the dialog box
    d.ShowModal()
    # destroy the dialog box when finished (clicked 'OK')
    d.Destroy()


  def OnExit(self, event):
    """Event for when 'Exit' is clicked in the 'File' menu."""
    
    # close the frame
    self.Close(True)
  #----- END EVENT HANDLERS ----------




app = wx.PySimpleApp()
# id of -1 tells wx to assign an id for the frame for us
frame = MainWindow(None, -1, "Small Editor")
app.MainLoop()
