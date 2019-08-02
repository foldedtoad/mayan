# mayan
Mayan apps

This project is splitted into two parts: Editor and MayanNum.

## Editor
The Mayan glyph annotator app, python/editor/editor.py, used to add text to the glyph images.
The images are located in the editor's "images" subdirectory. Only "png" images will be recognized by the editor.
Just drop new .png images into the images directory and then start the editor (./editor.py).

In addition (optional) a sound clip (mayan pronouncation) can be associated with the image.  Just drop the .wav file into the sounds directory.  The .wav filename should match the image filename: example ./image/mayan_01.png --> ./sound/mayan_01.wav.
In the Android MayanNum, double-tap on the glyph image to play the pronouncation sound clip.

After annotating the glyph images and closing the editor, the glyphs.json file will all the glyph info, including the glyph image resources and annotations.  The glyphs.json file is then copied into the Android MayanNum project, so as to replace the glyphs.json file in that Android project, ./android/mayan/mayancal/src/main/assets/glyphs.json.

The editor is written to Python2.7 levels and uses Tk for GUI functions.

## MayanNum

<tbd>
