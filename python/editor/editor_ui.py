#!/usr/bin/env python
#
import time
import struct
import sys
import Tkinter as tk
from Tkinter import *

#===========================================
# Define Editor UI class
#===========================================

class Editor_UI(tk.Frame):

    def __init__(self, master=None):
        tk.Frame.__init__(self, master)
        self.pack()
        self.master = master
        self.glyphId = tk.StringVar()
        self.glyphFilename = tk.StringVar()
        self.glyphMayanText = tk.StringVar()
        self.glyphMayanText = tk.StringVar()
        self.glyphLatinText = tk.StringVar()
        self.status = tk.StringVar()
        self.pack()

        self.buttonOk = Button(master, text="OK", command=self.buttonOkCallback)
        self.buttonOk.pack()

        self.buttonCancel = Button(master, text="Cancel", command=self.buttonCancelCallback)
        self.buttonCancel.pack()
        
        self.glyphId.set('')
        self.glyphFilename.set('')
        self.glyphMayanText.set('')
        self.glyphLatinText.set('')
        self.status.set('')

        self.master.bind('<Escape>', self.doShutdown)
        self.createWidgets()


    def buttonOkCallback(self):
        print "Ok clicked!"
        print "Glyph Id:   " + self.glyphId.get()
        print "Filename:   " + self.glyphFilename.get()
        print "Mayan Text: " + self.glyphMayanText.get()
        print "Latin Text: " + self.glyphLatinText.get()

        self.status.set("Process record...")
        self.after(5000, self.doAfterDelay)


    def buttonCancelCallback(self):
        print "Cancel clicked!"
        self.after(5000, self.doAfterDelay)


    def doShutdown(self, key):
        self.master.quit()


    def createWidgets(self):
        # Create a label for the instructions
        self.labelInst = tk.Label(self, 
                                  text="Please enter Glyph ID below",
                                  font=("Helvetica", 16),
                                  pady=10)
        self.labelInst.pack()

        # Create the widgets for entering a glyph-id
        self.entryGlyphId = tk.Entry(self, 
                                    width=10, 
                                    font=("Helvetica", 24),
                                    textvariable=self.glyphId)
        self.entryGlyphId.pack()
        self.entryGlyphId.focus_set()

        self.entryGlyphFilename = tk.Entry(self, 
                                    width=30, 
                                    font=("Helvetica", 24),
                                    textvariable=self.glyphFilename)
        self.entryGlyphFilename.pack()

        self.entryGlyphMayanText = tk.Entry(self, 
                                    width=30, 
                                    font=("Helvetica", 24),
                                    textvariable=self.glyphMayanText)
        self.entryGlyphMayanText.pack()

        self.entryGlyphLatinText = tk.Entry(self, 
                                    width=60, 
                                    font=("Helvetica", 24),
                                    textvariable=self.glyphLatinText)
        self.entryGlyphLatinText.pack()

        # Create a label for status
        self.labelStatus = tk.Label(self, 
                                    textvariable=self.status,
                                    font=("Helvetica", 16))
        self.labelStatus.pack()


    def doAfterDelay(self):        
        # Clear entry and label
        self.glyphId = ''
        self.glyphFilename = ''
        self.entryGlyphId.delete(0,10)
        self.entryGlyphFilename.delete(0,30)
        self.entryGlyphMayanText.delete(0,30)
        self.entryGlyphLatinText.delete(0,60)
        self.status.set("")


    def onReturnPressed(self, key):
        print "Glyph Id:   " + self.glyphId.get()
        print "Filename:   " + self.glyphFilename.get()
        print "Mayan Text: " + self.glyphMayanText.get()
        print "Latin Text: " + self.glyphLatinText.get()

        self.status.set("User code not recognized")
        self.after(5000, self.doAfterDelay)


#===========================================
# Create new instance of Logon
#===========================================

def main():

    window = tk.Tk()
    window.geometry("640x480+0+0")

    editor_ui = Editor_UI(master=window)

    #window.attributes("-fullscreen", True)

    window.mainloop()


if __name__ == "__main__":

    # Do not litter the world with broken .pyc files.
    sys.dont_write_bytecode = True

    print "Starting Mayan_Builder"

    main()
