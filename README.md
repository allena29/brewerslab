# Brewerslab - Home Brew Brewery Control

Historically this project has evolved - initially as an excercise is using Raspberry PI's and hardware. A reaspberry pi was dedicated pretty much to just having physical buttons, flashing LED's and and a LCD screen. Because a Raspberry PI doesn't have a huge number of GPIO pins (6-tri coloured LED's adds up) 8 buttons  and my electronics understanding doesn't scale out to just adding lots of MCP23017's. In the early days a design decision was made ot use multicast so that two raspberry pi's could be split apart and get more GPIO pins (and more importantly more power to play with).

However the governor was responsible for sending a heartbeat to every disconnected process to tell it what mode to work in - but in 2018 when the raspberry pi failed this led to a new thoughts about trying to avoid some of the complexity.

This branch is thinking about introducing NETCONF, YANG, CLI, and possibly streaming telemetry - all buzz words but things that are interesting. Unfortunately a big constraint is that paying for something is out of the question - TailF's CONFD is interesting - but heavy weight for what is needed here and without commercial licenses doesn't tick the CLI box.

I might conclude it's not possible to cobble everything together - but I'll give it a bloody good go first.


## Python Virtual ENV

**TODO: check and re-test**

```
git clone https://github.com/pyenv/pyenv.git ~/.pyenv
git clone https://github.com/pyenv/pyenv-virtualenv.git $(pyenv root)/plugins/pyenv-virtualenv
pyenv install 2.7.13
pyenv virtualenv 2.7.13 brewerslab
```

And added to the bash_profile

```
eval "$(pyenv init -)"
eval "$(pyenv virtualenv-init -)"
export PS1="{\[\033[36m\]\u@\[\033[36m\]\h} \[\033[33;1m\]\w\[\033[m\] \$ "
export PYENV_VIRTUALENV_DISABLE_PROMPT=1
```



# Example Flow

*TODO: properly name the common functionality*

## YANG Model

The following basic yang model can be used to represent functions in a pico-brewery, of course this is just describing how to store data - (noun) rather than having any behaviour/action (verb). 


```
module: brewerslab
  +--rw brewhouse
  |  +--rw temperature
  |     +--rw fermentation
  |     |  +--ro monitor?     boolean
  |     |  +--rw setpoint?    brewerslab:temperature
  |     |  +--rw highpoint?   brewerslab:temperature
  |     |  +--rw lowpoint?    brewerslab:temperature
  |     |  +--rw probe
  |     |  |  +--rw id?   string
  |     |  +--rw results
  |     |     +--ro latest?    brewerslab:temperature
  |     |     +--rw average
  |     |        +--ro minute?   brewerslab:temperature
  |     |        +--ro hourly?   brewerslab:temperature
  |     |        +--ro daily?    brewerslab:temperature
  |     +--rw hardware
  |        +--rw probe* [id]
  |           +--rw id         string
  |           +--rw offsets* [low high]
  |              +--rw low       brewerslab:temperature
  |              +--rw high      brewerslab:temperature
  |              +--rw offset?   brewerslab:temperature
  +--rw ingredients
  |  +--rw fermentable* [ingredient]
  |  |  +--rw ingredient    string
  |  +--rw adjunct* [ingredient]
  |  |  +--rw ingredient    string
  |  +--rw hops* [ingredient]
  |     +--rw ingredient    string
  +--rw recipes
  |  +--rw recipe* [recipename]
  |     +--rw recipename    string
  +--rw brewlog
```



## Data Store

Data stored in the following locations, all paths (/) are relative to the git clone. This file would be /README.md

- `/heap/opdata` json serialised data for each stats Data Provider - this will exclude configuration data unless it's structure is important (i.e. keys/containers).
- `/heap/running` json serialised data for the configuration of each Data Provider, i.e. running configuration.
- `/hoard/persist` as per heap/running but persisted and reloaded when a data provider reloads.
- `/hoard/default` a default configuration under version control which will be used by a process to create an initial running configuration if there is no matching persist configuration saved.
- `bundle/*bundle*` a number of discreet bundled containing data providers, these will be launched from their respective directory. Each bundle has a Makefile which should 

  -  Generate PYANG binding
  -  Run unit tests



The data is serialised by [pyangbind](https://github.com/robshakir/pyangbind)'s IETF JSON method - pyangbind itself is customised to allow data to be split as above. The files themselves are augmented with additional data, however these augmentations are never presented to pyangbind.



## Data Provider (e.g. brewerslab/bundle/TemperatureProviderDS18B20) - extends Villain.Goblin

Data providers are stored in and executed from a sub-directory of /bundle. For simplicity a data provider will own a single part fo the YANG modle. Data providers inherit from a common class which manages carries out basic administrative tasks of initialising configuration, loading, saving, accessing and setting it. 

On startup if a data provider has implemented a `setup(self)` method this will be started.

A data provider is given a standard python logging object.

```
class TemperatureProviderDS18B20(Villain.Goblin):

    def setup(self):
        pass

	def start(self):
		self.log.info('Standard Application Code would go here')
if __name__ == '__main__':
    try:
    	  # appname (generic etc TemperatureProvider, GravityProvider
    	  # yang module name
    	  # xpath of the datamodel that this utility is responsible for.
        daemon = TemperatureProviderDS18B20('TemperatureProvider', 'brewerslab', '/brewhouse/temperature')
        daemon.start()
    except KeyboardInterrupt:
        pass
```



We can run a data provider as follows...

```
{adam@mosaic} ~/brewerslab/bundle/brewerslab $ PYTHONPATH="$PYTHONPATH:../../confvillain" python TemperatureProviderDS18B20.py
2018-04-05 00:49:07,076 - TemperatureProvider  INFO          Goblin Init: <__main__.TemperatureProviderDs18B20 instance at 0x1026ba368>
2018-04-05 00:49:07,093 - TemperatureProvider  DEBUG         Found top-level yang module object <binding.brewerslab object at 0x10310f530>
2018-04-05 00:49:07,093 - TemperatureProvider  INFO          No persist or default data to load... using empty schema
2018-04-05 00:49:07,093 - TemperatureProvider  INFO          No existing opdata... providing empty schema
2018-04-05 00:49:07,094 - TemperatureProvider  INFO          Goblin Setup <__main__.TemperatureProviderDs18B20 instance at 0x1026ba368>
2018-04-05 00:49:07,094 - TemperatureProvider  INFO          Goblin Started <__main__.TemperatureProviderDs18B20 instance at 0x1026ba368>

... application code ....
Traceback (most recent call last):
  File "TemperatureProviderDS18B20.py", line 159, in <module>
    MONSTER.start()
  File "TemperatureProviderDS18B20.py", line 152, in start
    self.getResult()
  File "TemperatureProviderDS18B20.py", line 124, in getResult
    self.log.debug('getResult - %s' % (self._get_probes_to_monitor()))
  File "TemperatureProviderDS18B20.py", line 102, in _get_probes_to_monitor
    for probe in os.listdir(self.one_wire_temp_result_directory):
OSError: [Errno 2] No such file or directory: '/sys/bus/w1/devices/'
... application code ...

2018-04-05 00:49:07,096 - TemperatureProvider  INFO          Goblin Finished: <__main__.TemperatureProviderDs18B20 instance at 0x1026ba368>

```

This then generates the following files.

```
/heap/opdata/TemperatureProvider.cvd
{"__namespace": "brewerslab", "fermentation": {"monitor": false, "results": {"average": {"hourly": "0", "minute": "0", "daily": "0"}, "latest": "0"}}}

/heap/running/TemperatureProvider.cvd
{"__namespace": "brewerslab", "fermentation": {"setpoint": "0", "probe": {"id": ""}, "lowpoint": "0", "highpoint": "0"}}
```


## RESTFUL Server (Ghoul)

A RESTFUL server is implemented using [gunicorn](http://gunicorn.org/) and [Falcon](https://falconframework.org/#sectionCommunity). This server will be central is running on the a server which has access to the /heap and /hoard directories. When a client wishes to receive data they should make a GET call. It is expected that the server will also allow clients to write configuration (Authentication/Authorization TBD.. running on HTTPS etc).

The server can be started from the confvillain directory

```
gunicorn --reload ghoul.app
```

An example of fetching opdata for the Temperature Provider is shown below.

```
http localhost:8000/v1/datastore/opdata/TemperatureProvider
HTTP/1.1 200 OK
Connection: close
Date: Wed, 04 Apr 2018 23:58:51 GMT
Server: gunicorn/19.7.1
content-length: 150
content-type: application/json; charset=UTF-8

{
    "__namespace": "brewerslab",
    "fermentation": {
        "monitor": false,
        "results": {
            "average": {
                "daily": "0",
                "hourly": "0",
                "minute": "0"
            },
            "latest": "0"
        }
    }
}
```


## CLI (Bandit)

A basic CLI client is planned this uses [cmd2](https://github.com/python-cmd2/cmd2) which removes much of the grunt work in provided a CLI. The experience of the command-line should behave like a minimal JunOS interface.

The CLI client must have minimial dependencies and is expected to make secure and authenticated REST calls to the REST server 

```
python Bandit.py
wild@localhost> show brewhouse temperature fermentation
monitor   results
wild@localhost> show brewhouse temperature fermentation
{
    "monitor": false,
    "results": {
        "average": {
            "daily": "0",
            "hourly": "0",
            "minute": "0"
        },
        "latest": "0"
    }
}

[ok][Thu Apr  5 01:01:47 2018]
wild@localhost> show brewhouse temperature fermentation monitor
false

[ok][Thu Apr  5 01:01:52 2018]
wild@localhost> conf
Entering configuration mode private

[ok][Thu Apr  5 01:01:56 2018]
[edit]
robber@localhost%
robber@localhost>
```



## Streaming Telemetry

TBD - the legacy solution uses a raw json encoded data on a UDP Multicast socket - this is incredibly useful to have clients receive the data whenever they are interested. Currently there is a websocket bridge and elasticsearch publisher. In the legacy solution other processes (like relay, ssr's) listen to the UDP multicast stream.

## NETCONF

TBD
 
## WEB SERVER

TBD

## ANDROID APP

In the past I've created android apps for brewing related things, the sticking point has been there hasn't been a clear data interface. At one time GoogleApp Engine provided the data scheme, but then this became a wrapper around MySQL (this can be seend in the legacy command/gData.py). There is too much complexity in that area. 

Given there isn't going to be flashing lights/physical buttons having an android app will be useful to control the steps of the brew process.

## RECIPE Generator

A lot of historical/ancedotal learning has gone into calculating recipe parameters and tuning for my equipment. This is in brewerslabEngine.py of commander/metroui.

This should be carried across to the new world. 

