import axios from "axios";
import { useEffect } from "react"

export default function Chat(props){

    useEffect(()=>{
        axios.get(`/api/publickey/${props.user}`).then((data)=>{
            console.log(data)
        })
    }, []);

    return <>
    </>
}