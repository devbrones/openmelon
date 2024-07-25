# openmelon
Open source python framework for the Melon Headband

## Instructions

1. Clone this repository
2. Install the dependencies ```pip install -r requirements.txt```
3. Either run the LSL server: ```python melonlsl_server.py``` or the OSC client: ```python melonosc.py``` (see below)

## LSL Server
To be fixed in the future™

## OSC Client
The OSC client is a simple script that streams raw EEG data from the Melon Headband to an OSC server.

### Usage
```sh
python melonosc.py

# Optional arguments
python melonosc.py -a # adapter id
python melonosc.py -p # port
python melonosc.py -s # server ip
python melonosc.py -r # retry connection (To be fixed soon™)
python melonosc.py -h # help
python melonosc.py -d # debug
```
or use ```melonosc-analyze.py``` to send a focus score to an OSC server


