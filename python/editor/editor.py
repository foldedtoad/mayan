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
imageFilesList = None
soundFilesList = None

imagesPath = './images/'
soundsPath = './sounds/'
jsonFilename = 'glyphs.json'

def printGlyphs():
	if glyphs == None:
		print 'glyphs == None'
		return
	print '=================='
	for glyph in glyphs:
		name  = glyph
		data  = glyphs[glyph]['image']
		data  = glyphs[glyph]['sound']
		mayan = glyphs[glyph]['mayan']
		latin = glyphs[glyph]['latin']
		print "name: {0}, image: {1:40.40}..., image: {2:40.40}..., {3}, {4}".format(
			name, image, sound, mayan, latin)
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


def findSoundFile(imageFilename):
	soundFilename = None
	name, extension = os.path.splitext(imageFilename)
	filename = name + '.wav'
	if os.path.isfile(soundsPath + filename) == True:
		soundFilename = filename
		#print "image {} :: sound: {}".format(imageFilename, soundFilename)
	return soundFilename


def addGlyph(imageFilename):
	global glyphs

	with open(imagesPath + imageFilename, mode='rb') as file:
		data = file.read();
		image = data.encode('base64')
		file.close()

		soundFilename = findSoundFile(imageFilename)
		if soundFilename != None:
			with open(soundsPath + soundFilename, mode='rb') as file:
				data = file.read()
				sound = data.encode('base64')
				file.close()
		else:
			sound = ""

		name, extension = os.path.splitext(imageFilename)
		glyph = {'image': image, 'sound': sound, 'mayan': 'mayan', 'latin': 'latin'}
		glyphs[name] = glyph
		return


def updateNewGlyphs():
	global glyphs
	global imageFilesList

	print 'updateGlyphs start'
	newFiles = imageFilesList[:]
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
	global imageFilesList
	imageFilesList = []
	for root, dir, files in os.walk(imagesPath):
		for filename in files:
			if '.png' in filename.lower():
				imageFilesList.append(os.path.join('', filename))
	imageFilesList.sort()
	return

def buildSoundFilesList():
	global soundFilesList
	soundFilesList = []
	for root, dir, files in os.walk(soundsPath):
		for filename in files:
			if '.wav' in filename.lower():
				soundFilesList.append(os.path.join('', filename))
	soundFilesList.sort()
	return

def editor():
	print "editor start"

	buildImageFilesList()
	buildSoundFilesList()

	loadGlyphs()

	updateNewGlyphs()

	runEditorUI(imageFilesList, soundFilesList, glyphs)

	saveGlyphs()

	print "editor end"


if __name__ == '__main__':

	editor()
	sys.exit(0)
