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
# Define Editor_Listbox class
#===========================================

class Editor_Listbox(tk.Frame):

	def __init__(self, master=None):
		tk.Frame.__init__(self, master)
		self.pack()
		self.master = master
		return


	def createWidgets(self, filesList):

		self.label1 = tk.Label(self, text="Click on an image name below",
								font=("Helvetica", 11), pady=10)
		self.label1.pack()

		self.scrollbar = Scrollbar(self.master)
		self.scrollbar.pack(side=RIGHT, fill=Y)

		self.listbox = Listbox(self.master, yscrollcommand=self.scrollbar.set)

		for i in range(len(filesList)):
			self.listbox.insert(END, '{}'.format(filesList[i]))

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
		self.dialog = tk.Toplevel(parent)
		return


	def createDialog(self, imageName):
		self.dialog.minsize(300, 100)
		self.dialog.protocol("WM_DELETE_WINDOW", self.deleteDialog)

		self.dialog_label = tk.Label(self.dialog, text=imageName)
		self.dialog_label.pack(side='top')

		self.canvas = Canvas(self.dialog, width = 200, height = 200)
		self.canvas.pack()
		self.img = ImageTk.PhotoImage(Image.open("./images/" + imageName))  
		self.canvas.create_image(30, 30, anchor=NW, image = self.img) 

		self.buttonSave = tk.Button(self.dialog, text='Save', command=self.saveDialogClose)
		self.buttonSave.pack(side='left', padx=10, pady=10, fill='x', expand=True)
		self.buttonSave.bind('<Return>', self.returnPressedSave)

		self.buttonExit = tk.Button(self.dialog, text='Exit', command=self.deleteDialog)
		self.buttonExit.pack(side='right', padx=10, pady=10, fill='x', expand=True)
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

def runEditorUI(filesList):

	window = tk.Tk()
	window.geometry("320x240+0+0")

	editor_listbox = Editor_Listbox(master=window)
	editor_listbox.createWidgets(filesList)

	window.mainloop()

if __name__ == "__main__":

	filesList = ["mayan_00.png", "mayan_01.png", "mayan_02.png"]
	runEditorUI(filesList)
