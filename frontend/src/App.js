import React from 'react';

import './App.css';
import {Button, Navbar, Alignment, Toaster} from "@blueprintjs/core";
import history from './history';
import AddProject from './components/AddProject';

class App extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            add: false
        }
    }

    login() {
        this.props.auth.login();
    }

    logout() {
        this.props.auth.logout();
    }

    onHome() {
        history.replace("/home");
    }

    openAdd() {
        if (this.props.auth.isAuthenticated()) this.setState({add: !this.state.add});
        else {
            let t = Toaster.create({canEscapeKeyClear: true, autoFocus: true});
            t.show({
                message: "You must be logged in to request a project.",
                intent: "warning",
                icon: "warning-sign",
                action: {
                    onClick: () => {this.login()},
                    text: "Login"
                }
            });
        }
    }

    closeAdd() {
        this.setState({add: false});
    }

    createProject(data) {
        // TODO: send request to create project
        window.location.reload();
    }

    render() {
        const { isAuthenticated } = this.props.auth;

        if (this.props.location.pathname === "/") history.replace("/home");

        return (
            <div>
                <Navbar>
                    <Navbar.Group align={Alignment.LEFT}>
                        <Navbar.Heading>Projects Storage</Navbar.Heading>
                        <Navbar.Divider/>
                        <Button className="bp3-minimal" icon="home" text="Home" onClick={this.onHome}/>
                        <Button className="bp3-minimal" icon="add" text="New Project" onClick={this.openAdd.bind(this)}/>
                    </Navbar.Group>

                    <Navbar.Group align={Alignment.RIGHT}>
                        { !isAuthenticated() && <Button className="bp3-minimal" icon="user" text="Login" intent="primary" onClick={this.login.bind(this)}/> }
                        { isAuthenticated() && <Button className="bp3-minimal" icon="user" text="Logout" intent="danger" onClick={this.logout.bind(this)}/> }
                    </Navbar.Group>
                </Navbar>
                <AddProject open={this.state.add} close={this.closeAdd.bind(this)} create={this.createProject.bind(this)}/>
            </div>
        );
    }
}

export default App;