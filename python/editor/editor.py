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

glyphs = None
filesList = None

imagesPath = './images/'
jsonFilename = 'glyphs.json'

def printGlyphs():
	if glyphs == None:
		print 'glyphs == None'
		return
	print '=================='
	for glyph in glyphs:
		name  = glyph
		len   = glyphs[glyph]['len']
		data  = glyphs[glyph]['data']
		mayan = glyphs[glyph]['mayan']
		latin = glyphs[glyph]['latin']
		print "name: {0}, length: {1:>6}, data: {2:40.40}..., {3}, {4}".format(
			name, len, data, mayan, latin)
	print '=================='


def loadGlyphs():
	global glyphs
	try:
		with open(jsonFilename) as file:  
			glyphs = json.load(file)
			file.close()
			print 'loaded JSON'
	except:
		print 'create JSON'
		glyphs = { }
		pass
	return


def saveGlyphs():
	global glyphs
	try:
		with open(jsonFilename, 'w') as outfile:
			json.dump(glyphs, outfile, indent=4, sort_keys=True)
		outfile.close()
		print 'glyphs.json saved'
	except:
		print 'failed to save JSON data'
		pass


def addGlyph(filename):
	global glyphs

	with open(imagesPath + filename, mode='rb') as file:
		data = file.read();
		length = len(data)
		content = data.encode('base64')
		name, extension = os.path.splitext(filename)
		glyph = {'len': length, 'data': content, 'mayan': 'mayan text', 'latin': 'latin text'}
		glyphs[name] = glyph
		file.close()


def updateNewGlyphs():
	global glyphs
	global filesList

	print 'updateGlyphs start'
	newFiles = filesList[:]
	for glyph in glyphs:
		for filename in newFiles[:]:
			glyphname = glyph + '.png'
			if glyphname == filename:
				print 'exists: {}'.format(filename)
				newFiles.remove(filename)
	for filename in newFiles:
		print 'add:    {}'.format(filename)
		addGlyph(filename)
	print 'updateGlyphs end'
	return


def buildImageFilesList():
	global filesList
	filesList = []
	for root, dir, files in os.walk(imagesPath):
		for filename in files:
			if '.png' in filename.lower():
				filesList.append(os.path.join('', filename))
	filesList.sort()
	return


def editor():
	print "editor start"

	buildImageFilesList()

	loadGlyphs()

	updateNewGlyphs()

	runEditorUI(filesList, glyphs)

	saveGlyphs()

	print "editor end"


if __name__ == '__main__':

	editor()
	sys.exit(0)
