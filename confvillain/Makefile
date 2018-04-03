.PHONY: unittest

PYBIND := $(shell /usr/bin/env python -c 'import pyangbind; import os; print ("{}/plugin".format(os.path.dirname(pyangbind.__file__)))')

all:	pybindtest unittest

pybindtest:
	export PYTHONPATH=$(PYTHONPATH):./
	pyang --plugindir $(PYBIND) --use-xpathhelper -f pybind -o binding.py test/test.yang

unittest:
	nose2 -s test -t python -v --with-coverage --coverage-report html
