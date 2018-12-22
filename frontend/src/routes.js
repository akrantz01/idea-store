import React, {Component} from 'react';
import { Route, BrowserRouter } from 'react-router-dom';
import App from './App';
import Home from './components/Home';
import Callback from './components/Callback';
import Auth from './components/Auth';
import history from './history';

const auth = new Auth();

class Routes extends Component {
    state = {
        projects: [],
        refreshing: false
    };

    createProject(title, description, author, author_id) {
        // TODO: add API request
        this.setState({projects: [...this.state.projects, {
            id: Math.floor((Math.random()*500)+500),
            title,
            description,
            author,
            author_id,
            status: "queued",
            status_date: new Date().toDateString(),
            added_date: new Date().toDateString(),
            edited_date: false
        }]})
    }

    updateProject(index, title="", description="") {
        // TODO: add API request
        this.setState({projects: this.state.projects.map((project) => {
            if (project.id === index) {
                project.title = (title !== "") ? title : project.title;
                project.description = (description !== "") ? description : project.description;
            }
            return project;
        })});
    }

    deleteProject(index) {
        // TODO: add API request
        this.setState({projects: this.state.projects.filter((p) => p.id !== index)});
    }

    refreshProjects() {
        this.setState({refreshing: true});

        // TODO: replace with API call
        setTimeout(() => {
            this.setState({
                refreshing: false,
                projects: [
                    {
                        id: 1,
                        status_date: new Date().toDateString(),
                        status: "completed",
                        title: "Completed Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Test User",
                        author_id: "",
                        added_date: new Date().toDateString(),
                        edited_date: false
                    },
                    {
                        id: 2,
                        status_date: new Date().toDateString(),
                        status: "working",
                        title: "In Progress Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Test User",
                        author_id: "",
                        added_date: new Date().toDateString(),
                        edited_date: false
                    },
                    {
                        id: 3,
                        status_date: new Date().toDateString(),
                        status: "queued",
                        title: "Queued Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Test User",
                        author_id: "",
                        added_date: new Date().toDateString(),
                        edited_date: false
                    }
                ]
            });

        }, 1000);
    }

    render() {
        return (
            <BrowserRouter history={history} component={App}>
                <div>
                    <Route path="/" render={(props) => <App auth={auth} createProject={this.createProject.bind(this)} {...props} />} />
                    <Route path="/home" render={(props) => <Home auth={auth} refreshing={this.state.refreshing} projects={this.state.projects}
                                                                 refresh={this.refreshProjects.bind(this)} update={this.updateProject.bind(this)}
                                                                 delete={this.deleteProject.bind(this)} {...props} />} />
                    <Route path="/callback" render={(props) => <Callback {...props} />} />
                </div>
            </BrowserRouter>
        )
    }
}

export default Routes
