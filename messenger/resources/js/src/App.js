import React from 'react';
import ReactDOM from 'react-dom';

import ConversationList from "./components/ConversationList";
import Login from "./components/Login";
import Header from "./components/Header";

import "./css/global.scss";

function App() {
  return (
    <>
    <Header />
    <Login>
      <ConversationList />
    </Login>
    </>
  );
}

export default App;

if (document.getElementById('root')) {
  ReactDOM.render(<App />, document.getElementById('root'));
}
