import axios from "axios"
import { useEffect, useState } from "react"

import Chat from "./Chat";

export default function ConversationList(props){
    const [conversations, setConversations] = useState([]);
    const [chat, selectChat] = useState(null);
    useEffect(()=>refresh(), [])

    const refresh = ()=>{
        axios(`/api/getfriends/${window.token}`).then((data)=>{
            setConversations(data.data);
        }).catch(()=>alert("Une erreur est survenue..."));

        setTimeout(()=>refresh(), 5000);
    }

    const addFriend=()=>{
        let user = encodeURI(prompt("Nom d'utilisateur : "));
        axios(`/api/addfriend/${user}/${window.token}`).then(()=>alert("Demande envoyée")).catch((e)=>alert("Une erreur est survenue..."));
    }

    return (
    !chat?
     <div id="conversationList">
    {conversations.map((conversation, idx)=>
        <p key={idx} onClick={()=>selectChat(conversation.login)} className="conversation">{conversation.login}</p>
    )}
    <input type="button" onClick={()=>addFriend()} value="Add" />
    </div> : <>
    <input type="button" value="Back" onClick={()=>{selectChat(null)}} />
    <Chat user={chat} />
    </>
    );
}