#!/usr/bin/env python
#
import time
import struct
import sys
sys.dont_write_bytecode = True

import Tkinter as tk
from Tkinter import *
from PIL import ImageTk,Image  

#===========================================
# Define Editor UI class
#===========================================

class Editor_UI(tk.Frame):

    def __init__(self, master=None):
        tk.Frame.__init__(self, master)
        self.pack()
        self.master = master

        scrollbar = Scrollbar(self.master)
        scrollbar.pack(side=RIGHT, fill=Y)

        self.canvas = Canvas(self.master, width = 200, height = 200)
        self.canvas.pack()
        self.img = ImageTk.PhotoImage(Image.open("./images/mayan_00.png"))  
        self.canvas.create_image(30, 30, anchor=NW, image=self.img) 

        self.glyphFilename  = tk.StringVar()
        self.glyphMayanText = tk.StringVar()
        self.glyphMayanText = tk.StringVar()
        self.glyphLatinText = tk.StringVar()
        self.status = tk.StringVar()
        self.pack()

        self.buttonOk = Button(master, text="OK", command=self.buttonOkCallback)
        self.buttonOk.pack()

        self.buttonCancel = Button(master, text="Cancel", command=self.buttonCancelCallback)
        self.buttonCancel.pack()
        
        self.glyphFilename.set('')
        self.glyphMayanText.set('')
        self.glyphLatinText.set('')
        self.status.set('')

        self.master.bind('<Escape>', self.doShutdown)
        self.createWidgets()


    def buttonOkCallback(self):
        print "Ok clicked!"
        print "Filename:   " + self.glyphFilename.get()
        print "Mayan Text: " + self.glyphMayanText.get()
        print "Latin Text: " + self.glyphLatinText.get()

        self.status.set("Process record...")
        self.after(1000, self.doAfterDelay)


    def buttonCancelCallback(self):
        print "Cancel clicked!"
        self.after(1000, self.doAfterDelay)


    def doShutdown(self, key):
        self.master.quit()


    def createWidgets(self):
        # Create a label for the instructions
        self.labelInst = tk.Label(self, 
                                  text="Please enter Glyph filename below",
                                  font=("Helvetica", 16),
                                  pady=10)
        self.labelInst.pack()

        self.entryGlyphFilename = tk.Entry(self, 
                                    width=30, 
                                    font=("Helvetica", 24),
                                    textvariable=self.glyphFilename)
        self.entryGlyphFilename.pack()
        self.entryGlyphFilename.focus_set()

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
        self.glyphFilename = ''
        self.entryGlyphFilename.delete(0,30)
        self.entryGlyphMayanText.delete(0,30)
        self.entryGlyphLatinText.delete(0,60)
        self.status.set("")


    def onReturnPressed(self, key):
        print "Filename:   " + self.glyphFilename.get()
        print "Mayan Text: " + self.glyphMayanText.get()
        print "Latin Text: " + self.glyphLatinText.get()

        self.status.set("User code not recognized")
        self.after(1000, self.doAfterDelay)

#===========================================
# Create new UI instance 
#===========================================

def runEditorUI():

    window = tk.Tk()
    window.geometry("640x480+0+0")

    '''
    scrollbar = Scrollbar(window)
    scrollbar.pack(side=RIGHT, fill=Y)

    canvas = Canvas(window, width = 200, height = 200)
    canvas.pack()
    img = ImageTk.PhotoImage(Image.open("./images/mayan_00.png"))  
    canvas.create_image(30, 30, anchor=NW, image=img) 
    '''

    editor_ui = Editor_UI(master=window)

    window.mainloop()


if __name__ == "__main__":

    runEditorUI()
