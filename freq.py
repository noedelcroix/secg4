def cesar_encode(text, shift):
    ciphered=""
    for letter in text:
        if letter.isupper():
            ascii_encode=ord(letter)+shift
            ciphered+= chr(ascii_encode) if ascii_encode<=90 else chr(ascii_encode-26)
        elif letter.islower():
            ascii_encode=ord(letter)+shift
            ciphered+= chr(ascii_encode) if ascii_encode<=122 else chr(ascii_encode-26)
        else:
            ciphered+=char
    return ciphered

def vigenere_encode(text, key):
    ciphered=""
    for idx, letter in enumerate(text):
        if letter.isupper():
            shift=ord(key[idx%len(key)].lower())-97
            ascii_encode=ord(letter)+shift
            ciphered+= chr(ascii_encode) if ascii_encode<=90 else chr(ascii_encode-26)
        elif letter.islower():
            shift=ord(key[idx%len(key)].lower())-97
            ascii_encode=ord(letter)+shift
            ciphered+= chr(ascii_encode) if ascii_encode<=122 else chr(ascii_encode-26)
        else:
            ciphered+=letter
    return ciphered

print(cesar_encode("azertyuiop", 0))
print(vigenere_encode("azertyuiop", "a"))
