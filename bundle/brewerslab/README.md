# A Basic Bundle

- A single yang model
- run `make` this uses pyangbind to convert the schema to python and runs any unit tests
- PYTHONPATH pointing to ../../confvillain

*assumption code will run from the bundle directory* and the libraries/common code/yang will be accessible via `../../confvillain`


## Running the Demon Goblin!

The daemon (named Goblin) looks somethings like this

```
class TemperatureProviderDS18B20(Villain.Goblin):

    def setup(self):
        pass

if __name__ == '__main__':
    try:
        daemon = TemperatureProviderDS18B20('TemperatureDS18B20', 'brewerslab.brewhouse.temperature')
        daemon.start()
    except KeyboardInterrupt:
        pass
```

This extends Villain.Goblin and must implement the following methods.

- **setup** called by the parent class
- **start** 


It is anticipated that these methods will implement a loop the looping code should do something like

- check if we need to wait for someone to do some config
- check if we need to reload our config


The Goblin has some basic framework for loading pyangbind based config and that should let us read/write data.
If we have written some data that's important to be flushed to disk we should call something like *data change*

To consider going forward - scalability of just always flushing everything to a tmpfs disk - performance of 
serialising JSON could be a consideration... for something like the temperature results we can get sub second updated
with the crude multicast stuff we have in today... Do we move away from that to simplify things and just have
one node exposing tmpfs over NFS... or do we hook some kind of 'streaming telemety' in place of the multicast stuff.

In the short term everything is probably hosed on one pi anyway that's time critical - and other stuff is more cosmetic.


```python
# Run
PYTHONPATH="$PYTHONPATH:../../confvillain" python TemperatureProviderDS18B20.py 
3PYTHONPATH="$PYTHONPATH:../../confvillain" python TemperatureProviderDS18B20.py ; cat ../../confvillain/heap/running/TemperatureDS18B20.cvd
```



### Next things to do


Imeplement something on the *server* which arbitrates and collects data together from remote nodes.

We don't want the CLI thing to be any more heavy weight than having JSON payloads shipped to it which gives the schema.
We probably should add to pyangbind to give a mode which shows *only* oper data

That way we can refresh operdata and config data separately and flush them separately to the heap/running directory.

We then have a deamon on the server which brings to gether the data when requested to send to whatever is interested.

We could imagine an API like..

- /get/oper/path
- /set/config/path
- /get/config/path
- /discover - providing a mapping of how things are split.
