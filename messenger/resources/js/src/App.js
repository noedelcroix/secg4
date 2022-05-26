import React from 'react';
import ReactDOM from 'react-dom';

import ConversationList from "./components/ConversationList";
import Login from "./components/Login";
import Header from "./components/Header";

import "./css/global.sass";
import PendingInvitationsMenu from './components/PendingInvitationsMenu';

function App() {
  return (
    <>
    <Header />
    <Login>
      <PendingInvitationsMenu />
      <ConversationList />
    </Login>
    </>
  );
}

export default App;

if (document.getElementById('root')) {
  ReactDOM.render(<App />, document.getElementById('root'));
}
