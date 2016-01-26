Generic utility to parse social media data for network analyses. Includes:

- Twitter miner: collect live Twitter data via Streaming API, and network-relevant user meta-data
- Gnip parser: same as Twitter miner, except using Gnip as datasource
- Instagram miner: collect pseudo-live Instagram data, and network-relevant user meta-data

This is custom software that will collect live Twitter and Instagram data based on a set of keywords. Optionally, if you buy data from Gnip, there are tools included that can also parse that data. Primarily, the purpose of the data collection was to allow for large scale network analyses of the Twitter and Instagram networks. As such, some data is included which might seem unnecessary, while other data that seem important might be ignored.

There are also some custom utilities built for specific purposes.

As this software was written for a specific purpose, usage requires following a pre-existing outline.

- originally written using Java7, it now uses Java8 (backward compatibility is not being tested)
- data is stored in a MySQL database, but as there are no MySQL-specific scripts, other DB's will also serve 
- Twitter and Instagram accounts
- certain Java libraries (although they are included in the package)