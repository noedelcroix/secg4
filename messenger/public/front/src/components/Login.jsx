export default function Login(props){
    const checkLogin=()=>{
        return window.token==null
    }

    const submit=(e)=>{
        e.preventDefault();

        
    }

    if(checkLogin()){
        return <>
        <form onSubmit={(e)=>submit(e)}>
            <input type="text" placeholder="username" name="username" />
            <input type="password" placeholder="password" name="password" />
            <input type="submit" value="Send"/>
        </form>
        </>;
    }else{
        return props.children;
    }
}