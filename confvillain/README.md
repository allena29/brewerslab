# Conf Villain

**pyangbind** is used to convert yang into python classes.


```
export PYBINDPLUGIN=`/usr/bin/env python -c \
'import pyangbind; import os; print ("{}/plugin".format(os.path.dirname(pyangbind.__file__)))'`
echo $PYBINDPLUGIN
pyang --plugindir $PYBINDPLUGIN -f pybind -o binding.py brewerslab.py
```

We can then see this in action with some simple code. Pyang bind does a reasonable job of enforcing defaults.

```
import pyangbind.lib.pybindJSON as pybindJSON
from binding import brewerslab
import binding
worsdell = brewerslab()
# Because this is a config false node we can't assign directly.
worsdell = brewhouse.fermentation.temperature._set_latest(1.34)

o=open('brewhouse.json','w')
o.write((pybindJSON.dumps(worsdell.brewhouse)))
o.close()

worsdell.brewhouse.fermentation.temperature._set_latest(1.39)

loaded_back = pybindJSON.load('brewhouse.json', binding, 'yc_brewhouse_brewerslab__brewhouse')
print(loaded_back.fermentation.temperature.latest)

```

So this perhaps gives us a way of the temperature daemon holding it's *own* configuration and and operational data if it could save it's config data to disk periodically.

We could imagine then a wrapper netconf/cli/rest/web process which ties together the individual delegated parts of the model.


## CUSTOM PYANGBIND

This currently uses a custom version of pyangbind - the current version has the option to filter data which hasn't been changed but all operational data (config false) would be dumped. I would rather not store operatonal data to disk when serialising with JSON. To be honest right now I'm not entirely sure about the patch (well certainly not with the debug crap in). 
