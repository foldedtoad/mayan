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
		print 'imageName: ' + imageName
		return


	def doShutdown(self, key):
		self.master.quit()
		return


#===========================================
# Create new UI instance 
#===========================================

def runEditorUI(filesList):

	window = tk.Tk()
	window.geometry("320x240+0+0")

	editor_ui = Editor_UI(master=window)
	editor_ui.createWidgets(filesList)

	window.mainloop()

if __name__ == "__main__":

	filesList = ["mayan_00.png", "mayan_01.png", "mayan_02.png"]
	runEditorUI(filesList)
