import logging
import os
import binding
import pyangbind.lib.pybindJSON as pybindJSON
from pydoc import locate


try:
    import binding
except ImportError:
    raise RuntimeError('Unable to import pyang bindings.. have you run make-bundle.sh')
    sys.exit(9)

class Goblin:


    def __init__(self, appname, yangmodule):
        """This method provides a common approach to build and manage pyangbind based config
        with persistence and very primitive blocking.

        The method takes two attributes
        - appname -     an application tag which may in the future be used to provide a
                        namespace.
        - yangmodule -  dot separated yangpath, if we compile a yang module 'foo' with pyangbind
                        and this daemon is interested in the sub-sub-sub-container baa.blah.blaah
                        the yangmodule is written as foo.baa.blah.blaah

        Note: initial design pattern only supports a 1:1 relationship between portion of the yang
        module and owning Golbin service.
        """
        FORMAT = "%(asctime)-15s - %(name)-20s %(levelname)-12s  %(message)s"
        logging.basicConfig(level=logging.DEBUG, format=FORMAT)
        self.log = logging.getLogger(appname)
        self.log.info('Goblin Started: %s' % (self))

        # Load pyangbind schema and get down to our child.
        tmp_yang_path = yangmodule.split('.')
        parent_yang_module = tmp_yang_path.pop(0)
        parent_yang_class = locate('binding.%s' % (parent_yang_module))

        self.__parent = parent_yang_class()
        self.log.debug('Parent YANG Object %s' % (self.__parent))
        self.log.debug('Need to find %s' % (yangmodule))
        # Navigate down  
        self.__yang = self.__parent 
        while len(tmp_yang_path):
            self.__yang = getattr(self.__yang, tmp_yang_path.pop(0))

        self.log.debug('Our YANG Object %s' % (self.__yang))
        self.__appname = appname

        os.chdir('../../confvillain/hoard')
        if os.path.exists('persist/%s.cvd' % (self.__appname)):
            self.log.info('Loading previous persisted data')
        elif os.path.exists('defalt/%s.cvd' % (self.__appname)):
            self.log.info('Loading default ata')
        else:
            # Note: this is a custom version of pyangbind to filter out opdata
            self.log.info('No persist or default data to load... using empty schema')
            running = open('running/%s.cvd' % (self.__appname), 'w')
            running.write(pybindJSON.dumps(self.__yang, filter=False, ignore_opdata=True))
            running.close()
        
        

        self.setup()

    def __del__(self):
        self.log.info('Goblin Finished: %s' % (self))
