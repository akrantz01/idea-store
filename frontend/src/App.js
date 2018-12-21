import React from 'react';

import './App.css';
import {Button, Navbar} from "@blueprintjs/core";
import {Alignment as ALIGNMENT} from "@blueprintjs/core";
import history from './history';

class App extends React.Component {
    login() {
    this.props.auth.login();
  }

  logout() {
    this.props.auth.logout();
  }

  render() {
    const { isAuthenticated } = this.props.auth;

    if (this.props.location.pathname === "/") history.replace("/home");

    return (
        <div>
            <Navbar>
                <Navbar.Group align={ALIGNMENT.LEFT}>
                    <Navbar.Heading>Projects Storage</Navbar.Heading>
                    <Navbar.Divider/>
                    <Button className="bp3-minimal" icon="home" text="Home" />
                    { !isAuthenticated() && <Button className="bp3-minimal" icon="user" text="Login" onClick={this.login.bind(this)}/>}
                    { isAuthenticated() && <Button className="bp3-minimal" icon="user" text="Logout" onClick={this.logout.bind(this)}/> }
                </Navbar.Group>
            </Navbar>
        </div>
    );
  }
}

export default App;