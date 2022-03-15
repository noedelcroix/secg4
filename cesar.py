def cesar_encode(text, shift):
    ciphered=""
    for char in text:
        if char.isupper():
            key = ord(char)+shift
            ciphered+= chr(key) if key<=90 else chr((65+shift)-1)
        elif char.islower():
            key = ord(char)+shift
            ciphered+= chr(key) if key<=122 else chr((97+shift)-1)
        else:
            ciphered+=char
    return ciphered

def vigenere_encode(text, pasword):
    ciphered=""
    for idx, char in enumerate(text):
        if char.isupper():
            key = ord(char)+ord(key[idx])
            ciphered+= chr(key) if key<=90 else chr((65+shift)-1)
        elif char.islower():
            key = ord(char)+ord(key[idx])
            ciphered+= chr(key) if key<=122 else chr((97+shift)-1)
        else:
            ciphered+=char
    return ciphered

print(vigenere_encode("Moi C Noe", "azerty"))