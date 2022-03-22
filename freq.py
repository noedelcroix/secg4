#!/usr/bin/python

import sys
import getopt
import unidecode
import math
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
    for letter in text:
        if letter.isupper():
            ascii_encode=ord(letter)-shift
            ciphered+= chr(ascii_encode) if ascii_encode>=65 else chr(ascii_encode+26)
        elif letter.islower():
            ascii_encode=ord(letter)-shift
            ciphered+= chr(ascii_encode) if ascii_encode>=97 else chr(ascii_encode+26)
        else:
            ciphered+=letter
    return ciphered

def vigenere_decode(text, key):
    ciphered=""
    whitespaceCount=0
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
            if(letter==" "): whitespaceCount+=1
            ciphered+=letter
    return ciphered

def getFrequency(string):
    result=[{chr(i+97): 0} for i in range(26)]
    print(result)
    for letter in string.lower():
        if(letter != " "):
            result[ord(letter)-97][letter]+=1

    return result

def gcd(dist):
    num1 = dist[0]
    num2 = dist[1]
    gcd = math.gcd(num1,num2)
    for i in range (2,len(dist)):
        gcd=math.gcd(gcd,dist[i])
    return gcd

def findKeyLength(ciphered,length=3):
    dist = []
    for i in range(len(ciphered)):
        rep = ciphered[i:i+length]
        if len(rep)==length: 
            for j in range (i,len(ciphered)): 
                rep2 = ciphered[j:j+length]
                if rep==rep2:
                    dist.append(j-i)
                    j = j + length + 1
            i = i +1
        i+=1
    dist = [i for i in dist if i != 0]
    key = gcd(dist)
    if key == 1:
        return findKeyLength(ciphered,length+1)
    else:
        return key

def findKeyLengthByNoe(ciphered):
    pass

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

#main()

print(findKeyLengthByNoe())