# Authors
1. Piotr Smolinski - 56212
2. Noé Delcroix - 55990

# Exo 1

## Actor: be_education_authority

## cwd: be_education_authority/private/

1. First at all, before creating a self-signed certificate, we have to create an ECDSA private key.

```
openssl ecparam -name prime256v1 -genkey -noout -out be_ea_key.pem
```

## cwd : be_education_authority/public/

2. create the certificate using the previously created private key
```
openssl req -new -x509 -key ../private/be_ea_key.pem -out be_ea_cert.pem -days 3650 -subj "/C=BE/ST=Hainaut/L=Charleroi/O=Belgian Education Authority/CN=Belgian Education Authority DSC01"
```


# Exo 2
## cwd: /
```
cp be_education_authority/public/be_ea_cert.pem central_education_authority/tmp/
```

## Actor: central education authority
## cwd: central_education_authority/tmp/

- Since the education authority accepted the DSC, it'll be stored in public repository with it's signature as its name.
```
cp be_ea_cert.pem ../pubrepo/$(openssl x509 -in be_ea_cert.pem -fingerprint -noout | cut -d= -f2 | tr -d ':').pem
```


# Exo 3
## Actor: HE2B School
# cwd: he2b_school/tmp/
- Create a dpse file containing its personal data.
```
echo $'SMOLINSKI\nPIOTR\n56212\nBE\nHaute Ecole Bruxelles-Brabant\n2021-09-15\n2022-09-14'>56212.txt
```
```
echo $'DELCROIX\nNOE\n55990\nBE\nHaute Ecole Bruxelles-Brabant\n2021-09-15\n2022-09-14'>55990.txt
```


# Exo 4

## cwd: /
- He2b school sends the generated file to the belgian education authority which will be treated.
```
cp he2b_school/tmp/56212.txt be_education_authority/tmp/
```

```
cp he2b_school/tmp/55990.txt be_education_authority/tmp/
```

## Actor: belgian authority

## cwd: /be_education_authority/tmp
1. To generate the signature of the .txt file we'll be using sha256, which will be coded in 64bits base and redirected to the text file.
```
 openssl dgst -sha256 -sign ../private/be_ea_key.pem 56212.txt | base64 > 56212.signature.txt
```
```
 openssl dgst -sha256 -sign ../private/be_ea_key.pem 55990.txt | base64 > 55990.signature.txt
```
2. Also to respect the given structure, we have to retrieve the signature from the x.509 certificate to allow later identification of the signature, it will be also redirected into a text file..
```
openssl x509 -in ../public/be_ea_cert.pem -noout -fingerprint -sha1 | tr -d ’:’  | cut -d= -f2 > 56212.dscfingerprint.txt
```
```
openssl x509 -in ../public/be_ea_cert.pem -noout -fingerprint -sha1 | tr -d ’:’  | cut -d= -f2 > 55990.dscfingerprint.txt
```


# Exo 5
## cwd: /
## Actor: belgian authority

Belgian education authority sends back the generated signature and fingerprint
```
cp be_education_authority/tmp/56212.{signature,dscfingerprint}.txt he2b_school/tmp/
```
```
cp be_education_authority/tmp/55990.{signature,dscfingerprint}.txt he2b_school/tmp/
```
## cwd: /he2b_school/tmp
## Actor: he2b authority
He2b appends the received signature and fingerprint into the generated dpse filed and moves into dpse directory.
```
cp 56212.txt ../dpse/56212.dpse.txt
cat 56212.{signature,dscfingerprint}.txt >> ../dpse/56212.dpse.txt
```
```
cp 55990.txt ../dpse/55990.dpse.txt
cat 55990.{signature,dscfingerprint}.txt >> ../dpse/55990.dpse.txt
```

## cwd: /he2b_school/dpse
## Actor: he2b authority
Generate qrcodes.
```
sudo apt install qrencode
mkdir qr_dpse
```
```
cat 56212.dpse.txt | qrencode -o qr_dpse/56212.png
```
```
cat 55990.dpse.txt | qrencode -o qr_dpse/55990.png
```

# Exo 6
## cwd: /he2b_school/

1. First at all, we have to retrieve the dsc fingerprint
```
sudo apt install zbar-tools
```
```
zbarimg dpse/qr_dpse/56212.png --raw -q | sed -n '10p' > tmp/56212_dpse_dscfingerprint.txt
```
```
zbarimg dpse/qr_dpse/55990.png --raw -q | sed -n '10p' > tmp/55990_dpse_dscfingerprint.txt
```
2. Then, the signature is encoded in 64 base has to be retrieved and decoded to verify it using openssl.
```
cat dpse/56212.dpse.txt | sed -n '8p;9p' | base64 --decode > tmp/56212_dpse_signature.txt
```
```
cat dpse/55990.dpse.txt | sed -n '8p;9p' | base64 --decode > tmp/55990_dpse_signature.txt
```
3. Retrieved fingerprint is used to find the certificate and generate the public key, so let's go on:
```
openssl x509 -pubkey -noout -in ../central_education_authority/pubrepo/$(cat tmp/56212_dpse_dscfingerprint.txt).pem > tmp/56212_dpse_dsc_public_key.pem
```
```
openssl x509 -pubkey -noout -in ../central_education_authority/pubrepo/$(cat tmp/55990_dpse_dscfingerprint.txt).pem > tmp/55990_dpse_dsc_public_key.pem
```
4. Nextly, to compare the signature with the raw data, we have to retrieve the first 7 lines from the dpse file.
```
cat dpse/56212.dpse.txt | sed -n '1,7p'> tmp/56212_dpse_data.txt
```
```
cat dpse/55990.dpse.txt | sed -n '1,7p'> tmp/55990_dpse_data.txt
```
5. With the generated public key we can proceed to verify the signature of the dpse file with the stored data.
```
openssl dgst -sha256 -verify tmp/56212_dpse_dsc_public_key.pem -signature tmp/56212_dpse_signature.txt tmp/56212_dpse_data.txt
```
```
openssl dgst -sha256 -verify tmp/55990_dpse_dsc_public_key.pem -signature tmp/55990_dpse_signature.txt tmp/55990_dpse_data.txt
```

# Exo 7

## cwd: secg4-homework3-tests-21/
First of all, I've written a script that verifies the validity of every DPSE given file in the .tar, here are the results:
```
178878: expired
192521: signature does not match
285097: valid
380488: signature does not match
940091: valid
```
To reuse the script use the specified cwd, and use verification.sh script like this:
```
./verification dpse_tests/[file or *]
```