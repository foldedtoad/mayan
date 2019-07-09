#!/usr/bin/env python
#
import os
import sys
sys.dont_write_bytecode = True
import argparse
import json
import chardet
from os import listdir
from os.path import isfile, join
from editor_ui import runEditorUI

glyphs = {'glyphs': []}

imagesPath = './images/'
jsonFilename = 'glyphs.json'

def printGlyphs():
	for item in glyphs['glyphs']:
		print "filename: {0}, length: {1:>6}, data: {2:40.40}..., {3}, {4}".format(
			item['name'], str(item['len']), item['data'], item['mayan'], item['latin'])


def loadGlyphs():
	try:
		with open(jsonFilename) as file:  
			glyphs = json.load(file)
			file.close()
		print 'loaded JSON'
	except:
		print 'create JSON'
		pass


def saveGlyphs():
	try:
		with open(jsonFilename, 'w') as outfile:
			json.dump(glyphs, outfile, indent=4)
		outfile.close()
	except:
		print 'failed to save JSON data'
		pass


def addGlyph(filename):
	with open(imagesPath + filename, mode='rb') as file:
		data = file.read();
		length = len(data)
		content = data.encode('base64')
		name, extension = os.path.splitext(filename)

		glyphs['glyphs'].append({'name':name, 'len': length, 'data': content, 'mayan': 'n/a', 'latin': 'n/a'})
		file.close()


def buildFileList():
	filesList = []
	for root, dir, files in os.walk(imagesPath):
		for filename in files:
			if '.png' in filename.lower():
				filesList.append(os.path.join('', filename))
	filesList.sort()
	return filesList


if __name__ == '__main__':

	print "editor start"

	loadGlyphs()

	filesList = buildFileList()

	for filename in filesList:
		addGlyph(filename)

	printGlyphs()

	runEditorUI(filesList)

	saveGlyphs()

	print "editor end"

	sys.exit(0)
