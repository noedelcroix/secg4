import axios from "axios";
import { useEffect, useState } from "react"

export default function PendingInvitationsMenu(){

    const [pendingInvitations, setPendingInvitations] = useState([]);
    const [onlineFriends, setOnlineFriends] = useState([]);

    useEffect(()=>{
        refresh();
    }, []);

    const refresh = ()=>{
        axios.get(`/api/getpendinginvitations/${window.token}`).then((data)=>{
            setPendingInvitations(data.data);
    });

    axios.get(`/api/getonlinefriends/${window.token}`).then((data)=>{
        setOnlineFriends(data.data);
});

    if(window.token){
    setTimeout(()=>refresh(), 5000);
    }
}

    const acceptInvitationAction = (user)=>{
        axios(`/api/addfriend/${user}/${window.token}`).then(()=>alert("Invitation acceptée")).catch((e)=>{if(window.token) alert("Une erreur est survenue...");});
    }

    return(
        <div id="pendingInvitationsMenu">
            <h2>Pending Invitations</h2>
            {pendingInvitations.length > 0 ? pendingInvitations.map((el, idx)=>
                <div key={idx}><span>{el.login}</span><input type="button" value="Accepter" onClick={()=>acceptInvitationAction(el.login)} /></div>
            ) : <div><span>(No pending invitations)</span></div>}
            <h2>Online Friends</h2>
            {onlineFriends.map((el, idx)=>
                <div key={idx}><span>{el.login}</span></div>
            )}
        </div>
    )
}