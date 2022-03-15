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

print(cesar_encode("dCode Caesar",4)) 