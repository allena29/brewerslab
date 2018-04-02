# Brewerslab - Home Brew Brewery Control

Historically this project has evolved - initially as an excercise is using Raspberry PI's and hardware. A reaspberry pi was dedicated pretty much to just having physical buttons, flashing LED's and and a LCD screen. Because a Raspberry PI doesn't have a huge number of GPIO pins (6-tri coloured LED's adds up) 8 buttons  and my electronics understanding doesn't scale out to just adding lots of MCP23017's. In the early days a design decision was made ot use multicast so that two raspberry pi's could be split apart and get more GPIO pins (and more importantly more power to play with).

However the governor was responsible for sending a heartbeat to every disconnected process to tell it what mode to work in - but in 2018 when the raspberry pi failed this led to a new thoughts about trying to avoid some of the complexity.

This branch is thinking about introducing NETCONF, YANG, CLI, and possibly streaming telemetry - all buzz words but things that are interesting. Unfortunately a big constraint is that paying for something is out of the question - TailF's CONFD is interesting - but heavy weight for what is needed here and without commercial licenses doesn't tick the CLI box.

I might conclude it's not possible to cobble everything together - but I'll give it a bloody good go first.


## Python Virtual ENC

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


## ipc directory

Allows a number of flags to be set, the presence of this file can be used to trigger
and adjust the behaviour. 


- **no-ferm-control** whilst this flag is present all fermentation control will be disabled.
- **single-temp-probe** if this flag is present all temperature monitoring will be used from the hltProbe. This is unfortunately sometimes needed if the physical temperature probes are unreliable.
- **overrideModeFerm** causes pitmTemperature to fix on fermentation with a fixed target of 17 deg (hardcoded in pitmTemperature). Processes validated are pitmRelay.py, pitmTemperature.py and pitmElastic.py. This can be used in an emergency if the governor abandons duties during a fermentation.
pitmMonitor which would normally run on the same node as pitmGovernor.py is also validated.
In all cases processes need to be restarted when using overrideModeFerm.
