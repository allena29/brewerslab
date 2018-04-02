export PYBINDPLUGIN=`/usr/bin/env python -c \
'import pyangbind; import os; print ("{}/plugin".format(os.path.dirname(pyangbind.__file__)))'` 
echo ${PYBINDPLUGIN} 
pyang --plugindir $PYBINDPLUGIN -f pybind -o binding.py brewerslab.yang
