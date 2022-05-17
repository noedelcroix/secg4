import axios from "axios"
import { useEffect, useState } from "react"

export default function ConversationList(props){
    const [conversations, setConversations] = useState([]);
    useEffect(()=>refresh(), [])

    const refresh = ()=>{
        axios(`/api/getfriends/${window.token}`).then((data)=>{
            setConversations(data.data);
        }).catch(()=>alert("Une erreur est survenue..."));

        setTimeout(()=>refresh(), 5000);
    }

    const addFriend=()=>{
        let user = prompt("Nom d'utilisateur : ");
        axios(`/api/addfriend/${user}/${window.token}`).then(()=>alert("Demande envoyée")).catch((e)=>alert("Une erreur est survenue..."));
    }

    return <>
    {conversations.map((conversation, idx)=>
        <p key={idx}>{conversation.login}</p>
    )}
    <input type="button" onClick={()=>addFriend()} value="Add" />
    </>
}