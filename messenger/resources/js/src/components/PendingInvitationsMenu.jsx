import axios from "axios";
import { useEffect, useState } from "react"

export default function PendingInvitationsMenu(){

    const [pendingInvitations, setPendingInvitations] = useState([]);

    useEffect(()=>{
        refresh();
    }, []);

    const refresh = ()=>{
        axios.get(`/api/getpendinginvitations/${window.token}`).then((data)=>{
            setPendingInvitations(data.data);
    });

    setTimeout(()=>refresh(), 5000);
}

    const acceptInvitationAction = (user)=>{
        axios(`/api/addfriend/${user}/${window.token}`).then(()=>alert("Invitation acceptée")).catch((e)=>alert("Une erreur est survenue..."));
    }

    return(
        <div id="pendingInvitationsMenu">
            {pendingInvitations.map((el, idx)=>
                <div key={idx}><span>{el.login}</span><input type="button" value="Accepter" onClick={()=>acceptInvitationAction(el.login)} /></div>
            )}
        </div>
    )
}