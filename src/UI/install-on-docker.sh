#!/bin/bash

ORIGINAL_UI_DIR=`pwd`
TEMP_UI_DIR=/tmp/UI
HEADER="\n**** "

echo -e "$HEADER Copying project files to /tmp/UI..."
cp -ru $ORIGINAL_UI_DIR $TEMP_UI_DIR

echo -e "$HEADER Installing node dependencies..."
cd $TEMP_UI_DIR
npm install --unsafe-perm

echo -e "$HEADER Copying dependencies back to project folder..."
cp -ru $TEMP_UI_DIR/node_modules $ORIGINAL_UI_DIR
cp -ru $TEMP_UI_DIR/app/bower_components $ORIGINAL_UI_DIR/app
