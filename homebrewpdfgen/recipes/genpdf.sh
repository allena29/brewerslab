#!/bin/bash
# For a MAC-OS/X with retina disply zoom 1.5 was required
# Before using MAC with a regular display zoom was not required
for file in *.html
do
 wkhtmltopdf --zoom 1.5  -T 10 -B 10 -L 20 -R 10 $file pdfs/$file.pdf
done 

cd pdfs
pdfunite index.html.pdf 0* process.html.pdf ../recipes.pdf
