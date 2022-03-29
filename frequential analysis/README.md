# secg4
## Authors :
- 55990 : DELCROIX Noé
- 56212 : ZELINSKI Piotr
## How to install dependencies :
### On Windows :
Just type ```python -m pip install -r requirements.txt```
### On Linux :
Just type ```pip3 install -r requirements.txt```
## How to run :
### On Windows :
Just type ```python freq.py```
### On Linux :
Just type ```python3 freq.py```
## Command example :
### Help :
```python3 freq.py -h```
### Encode :
- Cesar :
```python3 freq.py -c encode -k 4 -t test```
- Vigenere :
```python3 freq.py -c encode -k pass -t test```
### Decode :
- Cesar :
```python3 freq.py -c decode -k 4 -t xiwx```
- Vigenere :
```python3 freq.py -c decode -k pass -t iekl```
#### Attack :
- Cesar :
```python3 freq.py -i text.txt -c attack -a cesar```
- Vigenere :
```python3 freq.py -i text.txt -c attack -a vigenere```