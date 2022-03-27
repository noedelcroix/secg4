#!/usr/bin/python

from re import sub
import sys
import getopt
import unidecode
import math


def cesarEncode(text, shift):
    alphabet = "abcdefghijklmnopqrstuvwxyz"
    ciphered = ""
    for letter in text:
        ciphered += alphabet[((ord(letter)-97)+shift) % 26]
    return ciphered


def vigenereEncode(text, key):
    ciphered = ""
    whitespaceCount = 0
    for idx, letter in enumerate(text):
        if letter.isupper():
            shift = ord(key[(idx-whitespaceCount) % len(key)].lower())-97
            ascii_encode = ord(letter)+shift
            ciphered += chr(ascii_encode) if ascii_encode <= 90 else chr(ascii_encode-26)
        elif letter.islower():
            shift = ord(key[(idx-whitespaceCount) % len(key)].lower())-97
            ascii_encode = ord(letter)+shift
            ciphered += chr(ascii_encode) if ascii_encode <= 122 else chr(ascii_encode-26)
        else:
            if(letter == " "):
                whitespaceCount += 1
            ciphered += letter
    return ciphered


def cesarDecode(text, shift):
    ciphered = ""
    for letter in text:
        ascii_encode = ord(letter)-shift
        ciphered += chr(ascii_encode) if ascii_encode >= 65 else chr(ascii_encode+26)
    return ciphered


def vigenereDecode(text, key):
    ciphered = ""
    whitespaceCount = 0
    for idx, letter in enumerate(text):
        if letter.isupper():
            shift = ord(key[(idx-whitespaceCount) % len(key)].lower())-97
            ascii_encode = ord(letter)-shift
            ciphered += chr(ascii_encode) if ascii_encode >= 65 else chr(ascii_encode+26)
        elif letter.islower():
            shift = ord(key[(idx-whitespaceCount) % len(key)].lower())-97
            ascii_encode = ord(letter)-shift
            ciphered += chr(ascii_encode) if ascii_encode >= 97 else chr(ascii_encode+26)
        else:
            if(letter == " "):
                whitespaceCount += 1
            ciphered += letter
    return ciphered


def findKeyLength(ciphered, length=3):
    i = 0
    dist = []
    while i < len(ciphered):
        rep = ciphered[i:i+length]
        if len(rep) == length:
            for j in range(i, len(ciphered)):
                rep2 = ciphered[j:j+length]
                if rep == rep2:
                    dist.append(j-i)
                    j = j + length + 1
            i = i + 1
        i += 1
    dist = [i for i in dist if i != 0]
    if(len(dist) != 0):
        key = gcd(dist)
    else:
        key = 0
    if key == 1:
        return findKeyLength(ciphered, length+1)
    else:
        return key


def gcd(dist):
    num1 = dist[0]
    if(len(dist) >= 2):
        num2 = dist[1]
    else:
        num2 = dist[0]
    gcd = math.gcd(num1, num2)
    for i in range(2, len(dist)):
        gcd = math.gcd(gcd, dist[i])
    return gcd


def getCesarSubstring(ciphered, key_length):
    substrings = []
    for i in range(0, key_length):
        substring = ""
        for j in range(i, len(ciphered), key_length):
            substring += ciphered[j]
        substrings.append(substring)
    return substrings


def letterFrequency(string):
    result = [0]*26
    for letter in string:
        result[ord(letter)-97] += 1
    return result


def letterCount(frequency):
    sum = 0
    for value in frequency:
        sum += value
    return sum


def findKey(ciphered, key_length):
    english_frequency = [0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094, 0.06966, 0.00153, 0.00772,
                         0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758, 0.00978,
                         0.02360, 0.00150, 0.01974, 0.00074]
    alphabet = "abcdefghijklmnopqrstuvwxyz"
    key = ""
    substrings = getCesarSubstring(ciphered, key_length)
    for substring in substrings:
        chis = []
        for i in range(1, len(alphabet)):
            frequency = letterFrequency(substring)
            letterCpt = letterCount(frequency)
            sumChi = 0
            for j in range(0, len(frequency)):
                sumChi += ((frequency[j]-(letterCpt*english_frequency[j]))
                           ** 2)/(letterCpt * english_frequency[j])
            chis.append(sumChi)
            substring = cesarEncode(substring, -1)
        key += alphabet[chis.index(min(chis))]
    return key


def usage():
    print("=============================================")
    print("#    Cesar/Vigenere encoder and decoder.    #")
    print("=============================================\n\n")
    print("This tool can encode and decode text with Cesar or Vigenere algorithm. This tool can also try to find the key by analysing frequency letters.\n")
    print("Available commands :\n")
    print("-c, --command [required] : encode, decode or attack")
    print("-t, --text [required or use -i] : the text that will be analysed.")
    print(
        "-i, --input [required or use -t] : the file containing text that will be analysed.")
    print(
        "-a, --algorithm [required with attack command] : specify the algorithm to attack (cesar or vigenere).")
    print("-k, --key [required for encode and decode commands] : the key to decode/encode the text. The tool will use the Cesar encryption if this argument is digits and Vigenere if string.")

    print("-h, --help : show this menu")


def main():
    try:
        opts, args = getopt.getopt(sys.argv[1:], "t:i:a:hk:c:", [
                                   "help", "command", "text", "key", "input", "algorithm"])
    except getopt.GetoptError as err:
        print(err)
        usage()
        sys.exit(2)

    if len(opts) == 0:
        usage()

    for o, a in opts:
        if o in ("-h", "--help"):
            usage()
        elif o in ("-c", "--command"):
            command = a.lower()
        elif o in ("-t", "--text"):
            text = unidecode.unidecode(a.lower())
        elif o in ("-k", "--key"):
            key = unidecode.unidecode(a.lower())
        elif o in ("-i", "--input"):
            text = unidecode.unidecode(open(a, "r").read().lower())
        elif o in ("-a", "--algorithm"):
            algo = unidecode.unidecode(a.lower())

    if("command" in locals()):
        if(command == "decode"):
            if("key" in locals() and "text" in locals()):
                if(key.isdigit()):
                    print("The decoded text is : "+cesarDecode(text, int(key)))
                else:
                    print("The decoded text is : "+vigenereDecode(text, key))
            else:
                print("Specify the key and text parameter.")
        elif(command == "encode"):
            if("key" in locals() and "text" in locals()):
                if(key.isdigit()):
                    print("Ciphered text is : "+cesarEncode(text, int(key)))
                else:
                    print("Ciphered text is : "+vigenereEncode(text, key))
            else:
                print("Specify the key and text parameter.")
        elif(command == "attack"):
            if("algo" in locals() and "text" in locals()):
                if(algo == "cesar"):
                    print("The key is : "+findKey(text, 1))
                elif(algo == "vigenere"):
                    print("The key is : "+findKey(text, findKeyLength(text)))
                else:
                    print("Specify the algorithm parameter.")
            else:
                print("Specify the algo and text parameter.")
        else:
            print("Command not found")


main()
