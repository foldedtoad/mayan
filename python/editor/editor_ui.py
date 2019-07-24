#!/usr/bin/env python
#
import time
import struct
import sys
sys.dont_write_bytecode = True

import json
import Tkinter as tk
from Tkinter import *
from PIL import ImageTk,Image

glyphs = None
filesList = None

#===========================================
# Define Editor_Listbox class
#===========================================

class Editor_Listbox(tk.Frame):

	def __init__(self, master=None):
		tk.Frame.__init__(self, master)
		self.pack()
		self.master = master
		return


	def createWidgets(self):
		global glyphs
		global filesList

		self.label1 = tk.Label(self, text="Click on an image name below",
								font=("Helvetica", 11), pady=10)
		self.label1.pack()

		self.scrollbar = Scrollbar(self.master)
		self.scrollbar.pack(side=RIGHT, fill=Y)

		self.listbox = Listbox(self.master, yscrollcommand=self.scrollbar.set)

		for glyph in glyphs:
			name = glyph
			self.listbox.insert(END, '{}'.format(name))

		self.sortListbox()

		self.listbox.focus_set()
		self.listbox.select_set(0)
		self.scrollbar.config(command=self.listbox.yview)
		self.listbox.pack(fill=BOTH, padx=20)

		self.label2 = tk.Label(self, text='Press [ESC] key to quit.',
								font=("Helvetica", 8))
		self.label2.pack(side='bottom')

		self.listbox.bind('<ButtonRelease-1>', self.listbox_item_clicked)
		self.master.bind('<Return>', self.listbox_item_clicked)
		self.master.bind('<Escape>', self.doShutdown)
		return


	def sortListbox(self):
		worklist = list(self.listbox.get(0, tk.END))
		worklist.sort()
		self.listbox.delete(0, tk.END)
		for item in worklist:
			self.listbox.insert(tk.END, item)


	def listbox_item_clicked(self, *ignore):
		imageName = self.listbox.get(self.listbox.curselection()[0])
		Editor_Dialog(self).createDialog(imageName)
		return


	def doShutdown(self, key):
		self.master.quit()
		return


#===========================================
# Define Editor_Dialog class
#===========================================

class Editor_Dialog(tk.Frame):

	def __init__(self, parent):
		self.master = parent.master
		self.dialog = tk.Toplevel(parent)
		return


	def createDialog(self, glyphName):
		global glyphs

		filename = glyphName + '.png'
		glyph = glyphs[glyphName]

		len   = glyph['len']
		data  = glyph['data']
		mayan = glyph['mayan']
		latin = glyph['latin']

		print "name: {0}, length: {1:>6}, data: {2:40.40}..., {3}, {4}".format(
			glyphName, len, data, mayan, latin)

		self.dialog.minsize(300, 300)
		self.dialog.protocol("WM_DELETE_WINDOW", self.deleteDialog)

		self.dialog_label = tk.Label(self.dialog, text=glyphName)
		self.dialog_label.pack(side=tk.TOP)

		self.canvas = Canvas(self.dialog, width = 200, height = 200)
		self.canvas.pack()
		self.img = ImageTk.PhotoImage(Image.open("./images/" + filename))  
		self.canvas.create_image(30, 30, anchor=NW, image = self.img) 

		#'''
		self.mayan_label = tk.Label(self.dialog, text="Mayan")
		self.mayan_label.pack(side=tk.LEFT, padx=10)

		self.mayan_edit = tk.Entry(self.dialog)
		self.mayan_edit.insert(END, mayan)
		self.mayan_edit.pack(side=tk.RIGHT, expand=tk.YES, fill=tk.X)

		self.latin_label = tk.Label(self.dialog, text="Latin")
		self.latin_label.pack(side=tk.LEFT)

		self.latin_edit = tk.Entry(self.dialog)
		self.latin_edit.insert(END, latin)
		self.latin_edit.pack(side=tk.RIGHT, expand=tk.YES, fill=tk.X)
		#'''

		self.buttonSave = tk.Button(self.dialog, text='Save', command=self.saveDialogClose)
		self.buttonSave.pack(side=tk.LEFT, padx=10, pady=10, fill='x', expand=True)
		self.buttonSave.bind('<Return>', self.returnPressedSave)

		self.buttonExit = tk.Button(self.dialog, text='Exit', command=self.deleteDialog)
		self.buttonExit.pack(side=tk.RIGHT, padx=10, pady=10, fill='x', expand=True)
		self.buttonExit.bind('<Return>', self.returnPressedDelete)
		return


	def returnPressedSave(self, *ignore):
		self.saveDialogClose()
		return


	def returnPressedDelete(self, *ignore):
		self.deleteDialog()
		return


	def saveDialogClose(self):
		print 'save dialog'
		self.dialog.destroy()
		return


	def deleteDialog(self):
		print 'delete dialog'
		self.dialog.destroy()
		return


#===========================================
# Create new UI instance 
#===========================================

def runEditorUI(_filesList, _glyphs):

	global glyphs

	glyphs = _glyphs
	filesList = _filesList

	window = tk.Tk()
	window.geometry("320x240+0+0")

	editor_listbox = Editor_Listbox(master=window)
	editor_listbox.createWidgets()

	window.mainloop()

if __name__ == "__main__":

	filesList = ["mayan_00.png", "mayan_01.png", "mayan_02.png"]
	glyphs = {}

	runEditorUI(filesList, glyphs)
