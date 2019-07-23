# mayan
Mayan apps

This project is splitted into two parts: Editor and MayanCal.

## Editor
The Mayan glyph annotator app, python/editor/editor.py, used to add text to the glyph images.
The images are located in the editor's "images" subdirectory. Only "png" images will be recognized by the editor.
Just drop new .png images into the images directory and then start the editor (./editor.py).
After annotating the glyph images and closing the editor, the glyphs.json file will all the glyph info, including the glyph image resources and annotations.  The glyphs.json file is then copied into the Android MayanCal project, so as to replace the glyphs.json file in that Android project, ./android/mayan/mayancal/src/main/assets/glyphs.json.

The editor is written to Python2.7 levels and uses Tk for GUI functions.

## MayanCal
<tbd>
