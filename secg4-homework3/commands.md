# Exo 1

## Actor: be_education_authority

## cwd: be_education_authority/private/

1. create the private key
```
# openssl ecparam -name prime256v1 -genkey -noout -out private-key.pem
```
## cwd : be_education_authority/public/
2. create the certificate using the previously created private key
```
# openssl req -new -x509 -key ../private/be_ea_key.pem -out be_ea_cert.pem -days 3650 
```


# Exo 2
setup:
# cwd: /
```
# cp be_education_authority/public/be_ea_cert.pem central_education_authority/tmp/

```

## Actor: central education authority
## cwd: central_education_authority/tmp/
rename the certificate received from the belgian education authority to 'fingerprint'.pem 
and moves(copies) it to the pubrepo/ directory
```
# cp be_ea_cert.pem ../pubrepo/$(openssl x509 -in be_ea_cert.pem -fingerprint -noout | cut -d= -f2 | tr -d ':').pem
```


# Exo 3
## Actor:
```
# cwd: he2b_school/

echo $'Smolinski\nPiotr\n56212\nBE\nHaute Ecole Bruxelles-Brabant\n2021-09-15\n2022-09-14'>56212.txt

```


# Exo 4
setup:
```
# cwd: /
cp he2b_school/tmp/12345.txt be_education_authority/tmp/
```

## Actor: belgian authority
```
# cwd: /be_education_authority/tmp

signature en 64 bits

openssl dgst -sha256 -sign ../private/be_ea_key.pem -out 56212.signature.txt 56212.txt & base64 56212.signature.txt > 56212.signature.txt

fingerprint

openssl x509 -in ../public/be_ea_cert.pem -noout -fingerprint -sha1 | tr -d ’:’  | cut -d= -f2 > 56212.fingerprint.txt


```


# Exo 5
setup:
```
# cwd: /
cp be_education_authority/tmp/56212.{signature,fingerprint}.txt he2b_school/tmp/
# cwd: /he2b_school/tmp
cat 56212.{signature,fingerprint}.txt >> 56212.txt 

cp 56212.txt ../dpse/

generating qr code

```

## Actor:


# Exo 6
```
First at all, we have to retrieve the dsc fingerprint

# cwd: /he2b_school/
# cat dpse/56212.dpse.txt | sed -n '10p' > tmp/56212_dpse_dscfingerprint.txt

Then, the signature encoded in 64 base has to be retrieved and decoded to verify it using openssl.
  
# cat dpse/56212.dpse.txt | sed -n '8p;9p' > tmp/56212_dpse_signature.txt

Retrieved fingerprint is used to find the certificate and generate the public key, so let's go on:

# openssl x509 -pubkey -noout -in ../central_education_authority/pubrepo/$(cat tmp/56212_dpse_dscfingerprint.txt).pem > tmp/56212_dpse_dsc_public_key.pem

With the generated public key we can proceed to verify the signature of the dpse file with the stored data.

# openssl dgst -sha1 -verify tmp/56212_dpse_dsc_public_key.pem -signature tmp/56212_dpse_signature.txt tmp/56212_dpse_data.txt

```


# Exo 7

