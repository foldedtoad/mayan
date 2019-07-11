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

		self.scrollbar = Scrollbar(self.master)
		self.scrollbar.pack(side=RIGHT, fill=Y)
		self.listbox = Listbox(self.master, yscrollcommand=self.scrollbar.set)

		for i in range(len(filesList)):
			self.listbox.insert(END, '{}'.format(filesList[i]))

		self.listbox.pack(fill=BOTH, padx=20)
		self.scrollbar.config(command=self.listbox.yview)
		self.listbox.bind('<ButtonRelease-1>', self.listbox_item_clicked)

		self.label1 = tk.StringVar()
		self.label1 = tk.Label(self, 
								text="Please select an image name from below",
								font=("Helvetica", 9),
								pady=10)
		self.label1.pack()

		self.master.bind('<Escape>', self.doShutdown)
		return


	def listbox_item_clicked(self, *ignore):
		imageName = self.listbox.get(self.listbox.curselection()[0])
		Editor_Dialog(self).createDialog(imageName)
		#dialog.createDialog(imageName)
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

		self.buttonExit = tk.Button(self.dialog, text='Exit', command=self.deleteDialog)
		self.buttonExit.pack(side='right', padx=10, pady=10, fill='x', expand=True)
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
