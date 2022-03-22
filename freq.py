#!/usr/bin/python

from re import sub
import sys
import getopt
import math

def cesar_encode(text, shift):
    alphabet = "abcdefghijklmnopqrstuvwxyz"
    ciphered=""
    for letter in text:
        ciphered+=alphabet[((ord(letter)-97)+shift)%26]
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
       ascii_encode=ord(letter)-shift
       ciphered+= chr(ascii_encode) if ascii_encode>=65 else chr(ascii_encode+26)
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
            ciphered+=letter
    return ciphered

def findKeyLength(ciphered,length=3):
    i = 0
    dist = []
    while i < len(ciphered):
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

def find_gcd(x, y):
    x = math.gcd(x,y)
    return x

def gcd(dist):
    num1 = dist[0]
    num2 = dist[1]
    gcd = find_gcd(num1,num2)
    for i in range (2,len(dist)):
        gcd=find_gcd(gcd,dist[i])
    return gcd



ciphered =""
with open('decode.txt','r') as f:
    ciphered = f.read()
    f.close()
ciphered = ciphered.lower()


def get_cesar_substring(ciphered, key_length):
    substrings=[]
    for i in range (0,key_length):
        substring=""
        for j in range(i,len(ciphered),key_length):
            substring+=ciphered[j]
        substrings.append(substring)
    return substrings


def letter_frequency(string):
    result=[0]*26
    for letter in string:
            result[ord(letter)-97]+=1
    return result

def letter_count(frequency):
    sum =0
    for value in frequency:
        sum+=value
    return sum

def find_key (ciphered, key_length) :
    english_frequency = [0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094, 0.06966, 0.00153, 0.00772,
                    0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758, 0.00978,
                    0.02360, 0.00150, 0.01974, 0.00074]
    alphabet = "abcdefghijklmnopqrstuvwxyz"
    key = ""
    substrings = get_cesar_substring(ciphered,key_length)
    for substring in substrings:
        chis=[]
        for i in range(1,len(alphabet)):
            frequency = letter_frequency(substring)
            letterCpt = letter_count(frequency)
            sumChi=0
            for j in range (0,len(frequency)):
                sumChi+=((frequency[j]-(letterCpt*english_frequency[j]))**2)/(letterCpt * english_frequency[j])
            chis.append(sumChi)
            substring = cesar_encode(substring,-1)
        key+=alphabet[chis.index(min(chis))]
    return key



print(find_key(ciphered,findKeyLength(ciphered)))

