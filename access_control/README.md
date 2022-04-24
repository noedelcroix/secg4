# SECG4 - Access Control

## Build and run
Build the jar files:
```shell
mvn package
```

It should have build `target/server.jar` and `target/client.jar`. You can then
run it
```shell
java -jar target/server.jar
```
And
```shell
java -jar target/client.jar
```

## Commands
### First steps
1. Client gets server public key from server folder at project root.
2. Client generates private and public key.
### REGISTER
For unauthenticated users, register a user wih username and password :
```shell
register username password
```
#### How it works :
##### Client side :
1. Client types username and password.
2. Client encrypts the password with the server public key.
##### Server side :
1. Server decrypts password, salts it and encrypts it with 100 passes.
2. Server checks if username is not yet used and saves the user, the encrypted password and the password salt in userdb.txt file.

### LOGIN
Login to process FATHER messages.
```shell
login username password
```
#### How it works :
##### Client side :
1. Client types username and password.
2. Client encrypts the password and its own public key with the server public key.
3. Client appends its encrypted public key to the message.
##### Server side :
1. Server checks username and password (decrypts password with its private key and make the 100 passes with salt).
2. If logins are valid, server saves the pair User and client public key (decrypted with its private key) in an authenticated users list.

### FATHER :
Send a message if client is authenticated.
```shell
father you killed my father
>>No, I am your father

father 
>>Whatever

(not authenticated) father
>>Not authenticated
```
#### How it works :
#### Client side :
1. After logged in, Client send messages with its username encrypted with its own private key at the end of the message.
#### Server side :
1. For each received father messages, server will select last word of message and try to decrypt with each client public key. If the decrypted message with a certain key equals the username, we consider the user logged in (and eventual know which user sent this message).
```
for each user (username, public key) in authenticated users list :
    if ( decrypt encrypted message with public key ) == username :
        return logged in
```
2. Return "No, I am your father" or "Whatever" if logged in or "Not authenticated" if not logged in.

### HELLO
Return "General Kenobi!" or "Whatever" whatever client is logged in or not.
```shell
hello there
>>General Kenobi!

hello 
>>Whatever
```

## Additional functionalities
### Message trusting (not finished)
NonReplayableMessage is made but server side implementation is comment and client side not already implemented.


## Author
- DELCROIX Noé 55990
- SMOLINSKI Piotr 56212
