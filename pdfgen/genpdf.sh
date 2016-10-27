if [ "$1" = "" ]
then
  echo "Provide a name work on"
  exit 2
fi
wkhtmltopdf -T 10 -B 10 -L 10 -R 10  $1.html $1.pdf
