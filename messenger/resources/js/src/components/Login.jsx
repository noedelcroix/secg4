import { useState } from "react";

import axios from "axios";
import $ from "jquery";
import sha256 from "sha256";

export default function Login(props){
    const [token, setToken] = useState(null);
    const [registering, setRegister] = useState(false);
    window.setToken = setToken;
    window.token = token;

    window.logout = ()=>{
        axios(`/api/logout/${token}`);
        setToken(null);
    }

    const submit = async (e)=>{
        e.preventDefault();

        const username = $("#username").val();
        const password = sha256($("#password").val(), {asString: true});

        await axios.post(`/api/${!registering ? "auth" : "createaccount"}`, {login: username, pswd: password}).then((data)=>{
            console.log(data.data.token);
            setToken(data.data.token);
        }).then(()=>{
            if(registering){
                alert("Enregistrement réussi.");
            }
        }).catch(()=>alert("Une erreur s'est produite. Veuillez réessayer plus tard."));

    }

        return( 
        token==null ?
        <>
        <form onSubmit={(e)=>submit(e)}>
            <input type="text" placeholder="username" name="username" required id="username" />
            <input type="password" placeholder="password" name="password" required id="password" />
            <input type="submit" value="Send" />
        </form>
        <input type="button" onClick={()=>setRegister(!registering)} value={registering ? "Sign in" : "Register"} />
        </>
        :  props.children
        );
}
    
