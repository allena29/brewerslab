# brewerslab
Brewerslab - Home Brew Brewery Control



## ipc directory

Allows a number of flags to be set, the presence of this file can be used to trigger
and adjust the behaviour. 


- **no-ferm-control** whilst this flag is present all fermentation control will be disabled.
- **single-temp-probe** if this flag is present all temperature monitoring will be used from the hltProbe. This is unfortunately sometimes needed if the physical temperature probes are unreliable.
- **overrideModeFerm** causes pitmTemperature to fix on fermentation with a fixed target of 17 deg (hardcoded in pitmTemperature). Processes validated are pitmRelay.py, pitmTemperature.py and pitmElastic.py. This can be used in an emergency if the governor abandons duties during a fermentationn.

