# Exo 1
## Actor: ??
Generating the X.509 certificate needs to firstly generate a private key, on the basis of the generated key, we're generating a public one which one is generated directly with the certificate. To extract the key we'll be using the 3rd command. 
```
- openssl genrsa -out private.key 1024
- openssl req -new -x509 -key private.key -out publickey.cer -days 3650
- openssl x509 -pubkey -noout -in be_ea_cert.pem  > be_ea_key.pem

to get the sha fingerprint 61:BE:F7:0E:B3:E7:E8:7F:2F:E9:91:49:59:31:AC:0E:9F:77:AB:F1
openssl x509 -in be_ea_cert.pem -noout -fingerprint -sha1 

```
# Exo 2
setup:
```
# cwd: /
cp [???]/be_ea_cert.pem central_education_authority/tmp/
```

## Actor:
```
# cwd: /
```


# Exo 3
## Actor:
```
# cwd: /
```


# Exo 4
setup:
```
# cwd: /
cp he2b_school/tmp/12345.txt be_education_authority/tmp/
```

## Actor:
```
# cwd: /
```


# Exo 5
setup:
```
# cwd: /
cp be_education_authority/tmp/12345.{signature,dscfingerprint}.txt he2b_school/tmp/
```

## Actor:


# Exo 6
```
# cwd: /
```


# Exo 7

