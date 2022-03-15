#!/usr/bin/python

import sys
import getopt
import unidecode

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
            ciphered+=letter
    return ciphered

def vigenere_encode(text, key):
    ciphered=""
    whitespaceCount=0
    for idx, letter in enumerate(text):
        if letter.isupper():
            shift=ord(key[(idx-whitespaceCount)%len(key)].lower())-97
            ascii_encode=ord(letter)+shift
            ciphered+= chr(ascii_encode) if ascii_encode<=90 else chr(ascii_encode-26)
        elif letter.islower():
            shift=ord(key[(idx-whitespaceCount)%len(key)].lower())-97
            ascii_encode=ord(letter)+shift
            ciphered+= chr(ascii_encode) if ascii_encode<=122 else chr(ascii_encode-26)
        else:
            if(letter==" "): whitespaceCount+=1
            ciphered+=letter
    return ciphered


def cesar_decode(text, shift):
    ciphered=""
    whitespaceCount=0
    for letter in text:
        if letter.isupper():
            ascii_encode=ord(letter)-shift
            ciphered+= chr(ascii_encode) if ascii_encode>=65 else chr(ascii_encode+26)
        elif letter.islower():
            ascii_encode=ord(letter)-shift
            ciphered+= chr(ascii_encode) if ascii_encode>=97 else chr(ascii_encode+26)
        else:
            if(letter==" "): whitespaceCount+=1
            ciphered+=letter
    return ciphered

def vigenere_decode(text, key):
    ciphered=""
    for idx, letter in enumerate(text):
        if letter.isupper():
            shift=ord(key[(idx-whitespaceCount)%len(key)].lower())-97
            ascii_encode=ord(letter)-shift
            ciphered+= chr(ascii_encode) if ascii_encode>=65 else chr(ascii_encode+26)
        elif letter.islower():
            shift=ord(key[(idx-whitespaceCount)%len(key)].lower())-97
            ascii_encode=ord(letter)-shift
            ciphered+= chr(ascii_encode) if ascii_encode>=97 else chr(ascii_encode+26)
        else:
            ciphered+=letter
    return ciphered

def attack():
    pass

def usage():
    print("=============================================")
    print("#    Cesar/Vigenere encoder and decoder.    #")
    print("=============================================\n\n")
    print("This tool can encode and decode text with Cesar or Vigenere algorithm. This tool can also try to find the key by analysing frequency letters.\n")
    print("Available commands :\n")
    print("-c, --command [required] : encode, decode or attack")
    print("-t, --text [required] : the text that will be analysed.")
    print("-k, --key [required for encode and decode commands] : the key to decode/encode the text. The tool will use the Cesar encryption if this argument is digits and Vigenere if string.")
    print("-h, --help : show this menu")

def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], "hc:t:k:", ["help", "command", "text", "key"])
    except getopt.GetoptError as err:
        print(err)
        usage()
        sys.exit(2)

    if len(opts)==0:
        usage()

    for o, a in opts:
        if o in ("-h", "--help"):
            usage()
        elif o in ("-c", "--command"):
            command=a
        elif o in ("-t", "--text"):
            text=unidecode.unidecode(a)
        elif o in ("-k", "--key"):
            key=unidecode.unidecode(a)
    
    if("command" in locals()):
        if(command=="decode"):
            if(key.isdigit()):
                print(cesar_decode(text, int(key)))
            else:
                print(vigenere_decode(text, key))
        elif(command=="encode"):
            if(key.isdigit()):
                print(cesar_encode(text, int(key)))
            else:
                print(vigenere_encode(text, key))
        elif(command=="attack"):
            print(attack())
        else:
            print("Command not found")

main()