#!/bin/bash
for user in "$@"
    do
    mkdir tmp
    #retrieve all the data needed like core, fingerprint, signature, the dates and stock them into a temporary directory tmp
    cat $user | sed -n '1,7p' > tmp/dpse_data.txt
    cat $user | sed -n '8p' | base64 --decode > tmp/dpse_signature.txt
    cat $user | sed -n '9p' > tmp/dpse_fingerprint.txt
    id=$(cat $user | sed -n '3p');
    #convert the dates into seconds to make them easier to compare.
    #Firstly, I do generate a temporary public key, stored into /tmp which will be deleted at the end of the script.
    openssl x509 -pubkey -noout -in central_education_authority/pubrepo/$(cat tmp/dpse_fingerprint.txt).pem > tmp/public_key.pem 2> /dev/null
    if [ $? -ne 0 ]; then
        echo "$id: cannot find the certificate";
    else
        #Then a quick check of the signature is made (if it's valid).
        openssl dgst -sha256 -verify tmp/public_key.pem -signature tmp/dpse_signature.txt tmp/dpse_data.txt 1> /dev/null
        if [ $? -ne 0 ]; then
            echo "$id: signature does not match";
        else
        #If all previous conditions weren't respected, the date at the end is verified, by veryfiing if the today's date is between the start and the end.
        startDate=$(cat $user | sed -n '6p');
        endDate=$(cat $user | sed -n '7p');
        today=$(date +%s);
        startDate=$(date -d $startDate +%s);
        endDate=$(date -d $endDate +%s);
            if [ $today -le $endDate ] && [ $today -ge $startDate ]; then
                echo "$id: valid"
            else
                echo "$id: expired"
            fi
        fi
    fi
    #Temporary files are removed.
    rm -r tmp/
done












