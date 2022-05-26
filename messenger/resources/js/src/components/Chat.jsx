import axios from "axios";
import { useEffect, useState } from "react"
import $ from "jquery";
import { decode, encode } from "base64-arraybuffer";

export default function Chat(props) {

    const [derivedKey, setDerivedKey] = useState({test:12});
    const [messages, setMessages] = useState([]);
    const [encryptedMessages, setEncryptedMessages] = useState([]);


    //get Derived Key
    useEffect(async () => {
        await setDerivedKey(await axios.get(`/api/publickey/${props.user}`).then(async (data) => {
            let devid = await deriveKey(JSON.parse(data.data[0].public_key), JSON.parse(localStorage.getItem("private_key")));
            return devid; 
        }));

        refreshMessages(derivedKey);
    }, []);

    //get encrypted messages
    const refreshMessages = async function(){
        console.log("here");
        let data = await axios(`/api/chat/${props.user}/${window.token}`)
        await setEncryptedMessages(data.data);
        
        setTimeout(() => refreshMessages(), 10000);
    }

    //decrypt encrypted Messages
    useEffect(()=>{
        let result = [];
        encryptedMessages.map(async (element) => {
            element.content = await decryptMessage(JSON.parse(element.content), derivedKey);
            result.push(element);
        })
        setMessages(result);
    }, [encryptedMessages]);

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
            iv: iv.buffer
        },
            derivedKey,
            encodedText
        )

        return {
            encryptedData: encode(encryptedData),
            iv: encode(iv.buffer)
        };

    };

    const decryptMessage = async function (messageJSON, derivedKey) {
        try {
            let iv = decode(messageJSON.iv);
            let cipheredText = decode(messageJSON.encryptedData);

            const algorithm = {
                name: "AES-GCM",
                iv: iv,
            };

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

    const sendMessage = async (e) => {
        e.preventDefault();
        axios.post(`/api/chat/${props.user}/${window.token}`, {
            content: JSON.stringify(await encryptMessage($("#message").val(), derivedKey))
        }).then(() => {
            console.log("message envoyé")
        });
    }

    useEffect(()=>{
        console.log(messages);
    }, [messages])

    return (
        <>
            <div id="listMessages">
                {messages.map((el, idx) => {
                    return <p key={idx}>
                        {el.content}
                    </p>
                })}
            </div>
            <form id="sendMessage" onSubmit={(e) => sendMessage(e)}>
                <textarea id="message" cols="30" rows="10"></textarea>
                <input type="submit" value="Envoyer" disabled={!derivedKey} />
            </form>
        </>
    );
}