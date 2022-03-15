def cesar_encode(text, shift):
    ciphered=""
    for char in text:
        if char.isupper():
            key = ord(char)+shift
            ciphered+= chr(key) if key<=90 else chr(key-26)
        elif char.islower():
            key = ord(char)+shift
            ciphered+= chr(key) if key<=122 else chr(key-26)
        else:
            ciphered+=char
    return ciphered
