import { useEffect, useState } from "react";

import axios from "axios";
import $ from "jquery";
import sha256 from "sha256";

export default function Login(props){
    const [token, setToken] = useState(null);
    const [registering, setRegister] = useState(false);
    window.setToken = setToken;
    window.token = token;

    const logout = ()=>{
        axios(`/api/deconnect/${token}`);
        setToken(null);
    }

    useEffect(()=>{
        genKeyPair();
    }, [])

    const genKeyPair = async function () {
        if(localStorage.getItem("public_key") && localStorage.getItem("private_key")) return;
        const keyPair = await window.crypto.subtle.generateKey({
                name: "ECDH",
                namedCurve: "P-256",
            },
            true,
            ["deriveKey"]
        );
        window.crypto.subtle.exportKey("jwk", keyPair.publicKey)
            .then(e => localStorage.setItem("public_key", JSON.stringify(e)));
        window.crypto.subtle.exportKey("jwk", keyPair.privateKey)
            .then(e => localStorage.setItem("private_key", JSON.stringify(e)));
    };

    const submit = async (e)=>{
        e.preventDefault();

        const username = $("#username").val();
        const password = sha256($("#password").val(), {asString: true});

        await axios.post(`/api/${!registering ? "auth" : "createaccount"}`, {login: username, pswd: password}).then((data)=>{
            setToken(data.data.token);
        }).then(()=>{
            if(registering){
                alert("Enregistrement réussi.");
            }
        }).catch(()=>alert("Le nom d'utilisateur n'existe pas ou la connexion avec le serveur n'a pas pu être établie."));

        if(!registering){
        await axios.post(`/api/publickey`, {
            token: window.token,
            key: localStorage.getItem("public_key")
        }).then(()=>console.log("clé envoyée.")).catch((e)=>{})
    }

    }

        return( 
        token==null ?
        <div id="login">
        <form onSubmit={(e)=>submit(e)} id="loginForm">
            <input type="text" placeholder="username" name="username" required id="username" />
            <input type="password" placeholder="password" name="password" required id="password" />
            <input type="submit" value="Send" />
        </form>
        <input type="button" onClick={()=>setRegister(!registering)} value={registering ? "Sign in" : "Register"} />
        </div>
        :  <>
        <input type="button" onClick={()=>logout()} value="⏼" />
        {props.children}
        </>
        );
}
    
