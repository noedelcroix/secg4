import axios from "axios";
import { useEffect, useState } from "react"
import $ from "jquery";
import {decode, encode} from "base64-arraybuffer";
import { result } from "lodash";

export default function Chat(props){

    const [derivedKey, setDerivedKey] = useState(null);
    const [messages, setMessages] = useState([]);


    useEffect(async ()=>{
        await axios.get(`/api/publickey/${props.user}`).then(async (data)=>{
            let devid = await deriveKey(JSON.parse(data.data[0].public_key), JSON.parse(localStorage.getItem("private_key")));
            window.devid = devid;
        });

        refreshMessages();
    }, []);

    const refreshMessages = async ()=>{
        await axios(`/api/chat/${props.user}/${window.token}`).then((data)=>{
            result = [];
            data.data.map(async (element)=>{
                element.content = await decryptMessage(JSON.parse(element.content), window.devid);
                result.push(element);
            })
            return result;
        })
        .then(async(data)=>{
            await setMessages(data);
        }).then(()=>setTimeout(()=>refreshMessages(), 5000));
    }

    const deriveKey = async function (publicKeyJwk, privateKeyJwk) {
        const publicKey = await window.crypto.subtle.importKey(
            "jwk",
            publicKeyJwk, {
                name: "ECDH",
                namedCurve: "P-256",
            },
            true,
            []
        );

        const privateKey = await window.crypto.subtle.importKey(
            "jwk",
            privateKeyJwk, {
                name: "ECDH",
                namedCurve: "P-256",
            },
            true,
            ["deriveKey"]
        );

        return await window.crypto.subtle.deriveKey({
                name: "ECDH",
                public: publicKey
            },
            privateKey, {
                name: "AES-GCM",
                length: 256
            },
            true,
            ["encrypt", "decrypt"]
        )
    };
    
    const encryptMessage = async function (text, derivedKey) {
        const encodedText = new TextEncoder().encode(text);
        let iv = window.crypto.getRandomValues(new Uint8Array(12));
        const encryptedData = await window.crypto.subtle.encrypt({
                name: "AES-GCM",
                iv: iv
            },
            derivedKey,
            encodedText
        )
    
        return {
            encryptedData: encode(encryptedData),
            iv: iv
        };
    
    };
    
    const decryptMessage = async function (messageJSON, derivedKey) {
        try {
            let iv = messageJSON.iv;
            let cipheredText = decode(messageJSON.encryptedData);
    
            const algorithm = {
                name: "AES-GCM",
                iv: iv,
            };

            console.log(cipheredText)
            console.log(algorithm);
            console.log(derivedKey);

            const decryptedData = await window.crypto.subtle.decrypt(
                algorithm,
                derivedKey,
                cipheredText
            )
    
            const str = new TextDecoder().decode(decryptedData);
    
            return str;
        } catch (e) {
            return `error decrypting message: ${e}`;
        }
    };

    const sendMessage = async (e)=>{
        e.preventDefault();
        axios.post(`/api/chat/${props.user}/${window.token}`, {
            content: JSON.stringify(await encryptMessage($("#message").val(), derivedKey))
        }).then(()=>{
            console.log("message envoyé")
        });
    }

    return(
    <>
    <div id="listMessages">
        {messages.map((el, idx)=>{
            return <p key={idx}>
                {el.content}
            </p>
        })}
    </div>
    <form id="sendMessage" onSubmit={(e)=>sendMessage(e)}>
        <textarea id="message" cols="30" rows="10"></textarea>
        <input type="submit" value="Envoyer" />
    </form>
    </>
    );
}