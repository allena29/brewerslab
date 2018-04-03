#!/bin/sh
export PYTHONPATH=$PYTHONPATH:../../confvillain
export PYBINDPLUGIN=`/usr/bin/env python -c \
'import pyangbind; import os; print ("{}/plugin".format(os.path.dirname(pyangbind.__file__)))'` 
echo ${PYBINDPLUGIN} 
pyang --plugindir $PYBINDPLUGIN -p ../../confvillain -f pybind -o binding.py brewerslab.yang
nose2 -s test -t python -v --with-coverage --coverage-report html
