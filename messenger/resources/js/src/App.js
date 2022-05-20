import React from 'react';
import ReactDOM from 'react-dom';

import ConversationList from "./components/ConversationList";
import Login from "./components/Login";

import "./css/global.css";

function App() {
  return (
    <Login>
      <h1>Login</h1>
      <ConversationList />
    </Login>
  );
}

export default App;

if (document.getElementById('root')) {
  ReactDOM.render(<App />, document.getElementById('root'));
}
