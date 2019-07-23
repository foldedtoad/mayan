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

imagesPath = './images/'
jsonFilename = 'glyphs.json'

def printGlyphs():
	if glyphs == None:
		print 'glyphs == None'
		return
	print '=================='
	for item in glyphs['glyphs']:
		print "filename: {0}, length: {1:>6}, data: {2:40.40}..., {3}, {4}".format(
			item['name'], str(item['len']), item['data'], item['mayan'], item['latin'])
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
		glyphs = {'glyphs': []}
		pass
	return


def saveGlyphs():
	global glyphs
	try:
		with open(jsonFilename, 'w') as outfile:
			json.dump(glyphs, outfile, indent=4)
		outfile.close()
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
		glyphs['glyphs'].append({'name':name, 'len': length, 'data': content, 'mayan': 'n/a', 'latin': 'n/a'})
		file.close()

def updateGlyphs(filesList):
	global glyphs
	print 'updateGlyphs start'
	newFiles = filesList[:]
	for filename in newFiles[:]:
		index = filename.index(filename)
		for glyph in glyphs['glyphs']:
			glyphname = glyph['name'] + '.png'
			if glyphname == filename:
				print 'exists: {}'.format(filename)
				newFiles.remove(filename)
	for filename in newFiles:
		print 'add:    {}'.format(filename)
		addGlyph(filename)
	print 'updateGlyphs end'
	return


def buildFileList():
	filesList = []
	for root, dir, files in os.walk(imagesPath):
		for filename in files:
			if '.png' in filename.lower():
				filesList.append(os.path.join('.', filename))
	filesList.sort()
	return filesList


def buildGlyphsNamesList():
	global glyphs
	namesList = []
	if glyphs == None:
		return namesList
	for node in glyphs['glyphs']:
		name = node['name'] + '.png'
		namesList.append(name)
	namesList.sort()
	return namesList


if __name__ == '__main__':

	print "editor start"

	filesList = buildFileList()

	loadGlyphs()

	glyphNames = buildGlyphsNamesList()

	updateGlyphs(filesList)

	runEditorUI(filesList)

	saveGlyphs()

	print "editor end"

	sys.exit(0)
