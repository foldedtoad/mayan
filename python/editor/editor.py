#!/usr/bin/env python
#
import os
import sys
import argparse
import json
import chardet
from os import listdir
from os.path import isfile, join

glyphs = {'glyphs': []}


def printGlyphs():
	for item in glyphs['glyphs']:
		print "filename: {0}, length: {1:>6}, data: {2:40.40}...".format(item['name'], str(item['len']), item['data'])


def loadGlyphs():
	try:
		with open('glyphs.json') as json_file:  
			glyphs = json.load(json_file)
			json_file.close()
		print 'loaded JSON'
	except:
		print 'create JSON'
		pass


def saveGlyphs():
	try:
		with open('glyphs.json', 'w') as outfile:
			json.dump(glyphs, outfile, indent=4)
		outfile.close()
	except:
		print 'failed to save JSON data'
		pass


def addGlyph(filename):
	with open('./images/' + filename, mode='rb') as file:
		data = file.read();
		length = len(data)
		content = data.encode('base64')
		name, extension = os.path.splitext(filename)

		glyphs['glyphs'].append({'name':name, 'len': length, 'data': content})
		file.close()

if __name__ == '__main__':

	print "core main start"

	loadGlyphs()

	filesList = []
	for root, dir, files in os.walk('./images'):
		for filename in files:
			if '.png' in filename.lower():
				filesList.append(os.path.join('', filename))
	filesList.sort()

	for filename in filesList:
		addGlyph(filename)

	printGlyphs()

	saveGlyphs()

	print "core main done"

	sys.exit(0)


