import inspect
import logging
import json
import os
import binding
import pyangbind.lib.pybindJSON as pybindJSON
from pyangbind.lib.serialise import pybindJSONDecoder
from pyangbind.lib.xpathhelper import YANGPathHelper
from pydoc import locate


try:
    import binding
except ImportError:
    raise RuntimeError('Unable to import pyang bindings.. have you run make-bundle.sh')
    sys.exit(9)


class Goblin:

    def __init__(self, appname, yangmodule, yangpath, open_stored_config=True):
        """This method provides a common approach to build and manage pyangbind based config
        with persistence and very primitive blocking.

        The method takes two attributes
        - appname -     an application tag which may in the future be used to provide a
                        namespace. (e.g. TemperatureDS18B20)
        - yangmodule -  name of the yang module as considered to be the top-level in pyangbind
                        auto-generated code. (e.g. brewerslab)
        - yangpath -    An XPATH expression providing a single instance of the data model that this
                        goblin is responsible for. (e.g. /brewhouse/temperature)

        Note: initial design pattern only supports a 1:1 relationship between portion of the yang
        module and owning Golbin service.
        """
        self.__path_helper = YANGPathHelper()

        FORMAT = "%(asctime)-15s - %(name)-20s %(levelname)-12s  %(message)s"
        logging.basicConfig(level=logging.DEBUG, format=FORMAT)
        self.log = logging.getLogger(appname)
        self.log.info('Goblin Init: %s' % (self))

        # Load pyangbind schema and get down to our child.
        self._yang_obj = None
        for (name, obj) in inspect.getmembers(binding):
            if name == yangmodule:
                self._yang_obj = obj(path_helper=self.__path_helper)

        if not self._yang_obj:
            raise RuntimeError('Unable to find class % in pyangbinding' % (yangmodule))
        self.log.debug('Found top-level yang module object %s' % (repr(self._yang_obj)))

        # Navigate down
        self._ourpath = yangpath
        try:
            self._yang = self.__path_helper.get(yangpath)[0]
        except:
            raise RuntimeError('Unable to navigate to %s' % (yangpath))

        self.__appname = appname

        if open_stored_config:

            working_directory = os.getcwd()
            config_directory = '../../hoard'
            cache_directory = '../../heap'

            if os.path.exists('%s/persist/%s.cvd' % (config_directory, self.__appname)):
                self.log.info('Loading previous persisted data')
                o = open('%s/persist/%s.cvd' % (config_directory, self.__appname))
                json_str = o.read()
                o.close()
                self._yang = self.loader(self._yang, json_str)

            elif os.path.exists('%s/default/%s.cvd' % (config_directory, self.__appname)):
                self.log.info('Loading default ata')
            else:
                # Note: this is a custom version of pyangbind to filter out opdata
                self.log.info('No persist or default data to load... using empty schema')
                running = open('%s/running/%s.cvd' % (cache_directory, self.__appname), 'w')
                running.write(self.dumper(self._yang))
                running.close()

        if hasattr(self, 'setup') and callable(self.setup):
            self.log.info('Goblin Setup %s' % (self))
            self.setup()

        self.log.info('Goblin Started %s' % (self))

    def dumper(self, yang):
        """        
        This method stores the in-memory object structure as an IETF JSON object.
        All config based nodes are dumped, but op-data is not.

        The yang object must be provided to this method.

        This makes use of a customised version of pyangbind distributed as part
        of this repository.
        """
        formal_json = json.loads(pybindJSON.dumps(yang, filter=False, ignore_opdata=True, mode='ietf'))
        filtered = {}
        for key in formal_json:
            newkey = key.split(':')[-1]
            filtered[newkey] = formal_json[key]
            filtered['__namespace'] = key.split(':')[0]
        return json.dumps(filtered)

    def loader(self, yang, json_str):
        """
        This method loads a IETF Json string representing the in-memory object structure
        and replaces the in-memory data.
        """

        try:
            filtered = json.loads(json_str)
            if '__namespace' in filtered:
                namespace = filtered['__namespace']
                del filtered['__namespace']
                formal_json = {}
                for key in filtered:
                    formal_json['%s:%s' % (namespace, key)] = filtered[key]
                json_obj = formal_json
            else:
                json_obj = filtered
        except ValueError as err:
            raise ValueError('Invalid JSON payload provided\n' + err.message)
        return pybindJSONDecoder.load_ietf_json(json_obj, None, None, yang)

    def get_config(self, path):
        """
        This method takes in an XPATH(like?) expresion and returns data objects.
        """
        self.log.debug('GET: %s => %s' % (path, self.__path_helper.get('%s%s' % (self._ourpath, path))))
        return self.__path_helper.get('%s%s' % (self._ourpath, path))

    def __del__(self):
        self.log.info('Goblin Finished: %s' % (self))
