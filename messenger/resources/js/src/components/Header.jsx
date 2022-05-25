export default function Header(){
    
    return(
        <header>
            <h1>Messenger</h1>
            {window.token ? <input type="button" onClick={()=>window.logout()} value="⏼" /> : null}
        </header>
    );
}