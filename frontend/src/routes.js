import React, {Component} from 'react';
import { Route, BrowserRouter } from 'react-router-dom';
import App from './App';
import Home from './components/Home';
import Requests from './components/Requests';
import Callback from './components/Callback';
import Auth from './components/Auth';
import history from './history';
import axios from 'axios';

const auth = new Auth();

class Routes extends Component {
    state = {
        projects: [],
        refreshing: false,
        adminView: false,
        adminLogout: false
    };

    createProject(title, description, author, authorId, p, commissioned) {
        // TODO: use actual endpoint for creation
        axios.post("http://localhost:8080/api/projects", {
            id: -1,
            title: title,
            description: description,
            author: author,
            authorId: authorId,
            status: "queued",
            priority: -1,
            public: p,
            deleted: false,
            commissioned: commissioned,
            commissionAccepted: false,
            commissionNotes: "",
            commissionCost: 0.0,
            commissionStart: "",
            commissionEnd: "",
            addedDate: new Date().toDateString(),
            editedDate: false
        }).then(result => {
            if (result.data.hasOwnProperty("data")) this.setState({projects: [...this.state.projects, result.data.data]});
        }).catch(err => console.error(err));
    }

    updateProject(index, updated) {
        if (updated.title === "") updated.title = null;
        if (updated.description === "") updated.description = null;

        // TODO: use actual endpoint for updating
        axios.put(`http://localhost:8080/api/projects/${index}`, updated)
            .then(result => {
                if (result.data.hasOwnProperty("data")) {
                    this.setState({projects: this.state.projects.map((project) => {
                        if (project.id === result.data.data.id) {
                            for (let key in result.data.data) {
                                if (project.hasOwnProperty(key)) project[key] = result.data.data[key];
                            }
                        }
                        return project;
                    })});
                }
            }).catch(err => console.error(err));
    }

    deleteProject(index, permanent=false) {
        if (permanent) {
            // TODO: use actual endpoint for creation
            axios.delete(`http://localhost:8080/api/projects/${index}`)
                .then(() => {
                    this.setState({projects: this.state.projects.filter((p) => {
                        return p.id !== index;
                    })});
                }).catch(err => console.error(err));
        } else {
            // TODO: use actual endpoint for creation
            axios.put(`http://localhost:8080/api/projects/${index}`, {deleted: true})
                .then(result => {
                    if (result.data.hasOwnProperty("data")) {
                        this.setState({projects: this.state.projects.map((project) => {
                            if (project.id === result.data.data.id) project.deleted = true;
                            return project;
                        })});
                    }
                }).catch(err => console.error(err));
        }
    }

    undoDeleteProject(index) {
        // TODO: use actual endpoint for creation
        axios.put(`http://localhost:8080/api/projects/${index}`, {deleted: false})
            .then(result => {
                if (result.data.hasOwnProperty("data")) {
                    this.setState({projects: this.state.projects.map((project) => {
                        if (project.id === index) project.deleted = false;
                        return project;
                    })})
                }
            }).catch(err => console.error(err));
    }

    refreshProjects() {
        this.setState({refreshing: true});

        // TODO: use actual endpoint for creation
        axios.get("http://localhost:8080/api/projects")
            .then(result => {
                if (result.data.hasOwnProperty("data")) this.setState({refreshing: false, projects: result.data.data});
            }).catch(err => console.error(err));
    }

    toggleAdminView = () => this.setState({adminView: !this.state.adminView});

    toggleAdminLogout = () => this.setState({adminLogout: !this.state.adminLogout});

    render() {
        return (
            <BrowserRouter history={history} component={App}>
                <div>
                    <Route path="/" render={(props) => <App auth={auth} createProject={this.createProject.bind(this)} toggle={this.toggleAdminView.bind(this)}
                                                            enabled={this.state.adminView} adminLogout={this.toggleAdminLogout.bind(this)} {...props} />} />
                    <Route path="/home" render={(props) => <Home auth={auth} refreshing={this.state.refreshing} projects={this.state.projects}
                                                                 refresh={this.refreshProjects.bind(this)} update={this.updateProject.bind(this)}
                                                                 delete={this.deleteProject.bind(this)} undo={this.undoDeleteProject.bind(this)}
                                                                 adminView={this.state.adminView} loggedOut={this.state.adminLogout} {...props} />} />
                    <Route path="/requests" render={(props) => <Requests auth={auth} projects={this.state.projects} refreshing={this.state.refreshing}
                                                                         refresh={this.refreshProjects.bind(this)} update={this.updateProject.bind(this)}
                                                                         delete={this.deleteProject.bind(this)} undo={this.undoDeleteProject.bind(this)}
                                                                         create={this.createProject.bind(this)} adminView={this.state.adminView} {...props}/>} />
                    <Route path="/callback" render={(props) => <Callback {...props} />} />
                </div>
            </BrowserRouter>
        )
    }
}

export default Routes
