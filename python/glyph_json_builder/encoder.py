
import os
import sys
import argparse
import json
import chardet
from os import listdir
from os.path import isfile, join

def make_glyph(name, data):
	glyph = {"name": name,
		     "len":  len(data),
		     "data": data}
	return json.dumps(glyph)

def main():

	print "encoder start"

	parser = argparse.ArgumentParser()

	if len(sys.argv) != 2:
		print "wrong number of arguments: {0} of 1 args found".format(len(sys.argv) - 1)
		print "usage: encoder.py  <directory-path>"
		sys.exit(1)

	parser.add_argument("path", help="Path")

	args = parser.parse_args()
	path = "{0}".format(args.path)

	filesList = []
	for root, dir, files in os.walk(path):
		for filename in files:
			if '.png' in filename.lower():
				filesList.append(os.path.join('', filename))
	filesList.sort()

	glyphs = {'glyphs': []}

	for filename in filesList:
		with open(filename, mode='rb') as file:
			data = file.read();
			length = len(data)
			content = data.encode('base64')
			print "filename: {0}, length: {1}".format(filename, length)
			name, extension = os.path.splitext(filename)

			glyphs['glyphs'].append({'name':name, 'len': length, 'data': content})

	with open('glyphs.json', 'w') as outfile:
		json.dump(glyphs, outfile, indent=4)

	print "encoder done"

	sys.exit(0)


if __name__ == '__main__':
    main()

