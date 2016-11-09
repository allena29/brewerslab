for file in *.html
do
 wkhtmltopdf -T 10 -B 10 -L 20 -R 10 $file pdfs/$file.pdf
done 

cd pdfs
pdfunite index.html.pdf 0* process.html.pdf ../recipes.pdf
