import React, {Component} from 'react';
import { Route, BrowserRouter } from 'react-router-dom';
import App from './App';
import Home from './components/Home';
import Requests from './components/Requests';
import Callback from './components/Callback';
import Auth from './components/Auth';
import history from './history';

const auth = new Auth();

class Routes extends Component {
    state = {
        projects: [],
        refreshing: false,
        adminView: false,
        adminLogout: false
    };

    createProject(title, description, author, author_id, p, commission) {
        // TODO: add API request
        this.setState({projects: [...this.state.projects, {
            id: Math.floor((Math.random()*500)+500),
            title,
            description,
            author,
            author_id,
            status: "queued",
            priority: -1,
            public: p,
            commissioned: commission,
            commission_accepted: false,
            commission_notes: "",
            commission_cost: 0,
            commission_start: "",
            commission_end: "",
            status_date: new Date().toDateString(),
            added_date: new Date().toDateString(),
            edited_date: false
        }]})
    }

    updateProject(index, updated) {
        // TODO: add API request
        this.setState({projects: this.state.projects.map((project) => {
            if (project.id === index) {
                for (let key in updated) {
                    if (project.hasOwnProperty(key)) project[key] = updated[key];
                }
                project.edited_date = new Date().toLocaleDateString();
            }
            return project;
        })});
    }

    deleteProject(index, permanent=false) {
        // TODO: add API request
        this.setState({projects: this.state.projects.filter((p) => {
            if (p.id === index && !permanent) p.deleted = true;
            else if (p.id === index && permanent) return false;
            return true;
        })});
    }

    undoDeleteProject(index) {
        // TODO: add API request
        this.setState({projects: this.state.projects.filter((p) => {
            if (p.id === index) p.deleted = false;
            return true;
        })});
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
                        title: "Completed Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Test User",
                        author_id: "an id",
                        status: "completed",
                        priority: 3,
                        public: true,
                        deleted: false,
                        commissioned: false,
                        commission_accepted: false,
                        commission_notes: "",
                        commission_cost: 0,
                        commission_start: "",
                        commission_end: "",
                        added_date: new Date().toLocaleDateString(),
                        edited_date: false
                    },
                    {
                        id: 2,
                        title: "In Progress Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Test User",
                        author_id: "an id",
                        status: "working",
                        priority: 2,
                        public: true,
                        deleted: false,
                        commissioned: false,
                        commission_accepted: false,
                        commission_notes: "",
                        commission_cost: 0,
                        commission_start: "",
                        commission_end: "",
                        added_date: new Date().toLocaleDateString(),
                        edited_date: false
                    },
                    {
                        id: 3,
                        title: "Queued Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Test User",
                        author_id: "an id",
                        status: "queued",
                        priority: 1,
                        public: true,
                        deleted: false,
                        commissioned: false,
                        commission_accepted: false,
                        commission_notes: "",
                        commission_cost: 0,
                        commission_start: "",
                        commission_end: "",
                        added_date: new Date().toLocaleDateString(),
                        edited_date: false
                    },
                    {
                        id: 4,
                        title: "Ignored Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Test User",
                        author_id: "an id",
                        status: "ignored",
                        priority: 0,
                        public: true,
                        deleted: false,
                        commissioned: false,
                        commission_accepted: false,
                        commission_notes: "",
                        commission_cost: 0,
                        commission_start: "",
                        commission_end: "",
                        added_date: new Date().toLocaleDateString(),
                        edited_date: false
                    },
                    {
                        id: 5,
                        title: "Private Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Test User",
                        author_id: "an id",
                        status: "working",
                        priority: 2,
                        public: false,
                        deleted: false,
                        commissioned: false,
                        commission_accepted: false,
                        commission_notes: "",
                        commission_cost: 0,
                        commission_start: "",
                        commission_end: "",
                        added_date: new Date().toLocaleDateString(),
                        edited_date: false
                    },
                    {
                        id: 6,
                        title: "New Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Test User",
                        author_id: "an id",
                        status: "queued",
                        priority: -1,
                        public: true,
                        deleted: false,
                        commissioned: false,
                        commission_accepted: false,
                        commission_notes: "",
                        commission_cost: 0,
                        commission_start: "",
                        commission_end: "",
                        added_date: new Date().toLocaleDateString(),
                        edited_date: false
                    },
                    {
                        id: 7,
                        title: "Deleted Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Test User",
                        author_id: "an id",
                        status: "working",
                        priority: 2,
                        public: true,
                        deleted: true,
                        commissioned: false,
                        commission_accepted: false,
                        commission_notes: "",
                        commission_cost: 0,
                        commission_start: "",
                        commission_end: "",
                        added_date: new Date().toLocaleDateString(),
                        edited_date: false
                    },
                    {
                        id: 8,
                        title: "Requested Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Alex Krantz",
                        author_id: "google-oauth2|102493818408140086770",
                        status: "working",
                        priority: 2,
                        public: true,
                        deleted: false,
                        commissioned: false,
                        commission_accepted: false,
                        commission_notes: "",
                        commission_cost: 0,
                        commission_start: "",
                        commission_end: "",
                        added_date: new Date().toLocaleDateString(),
                        edited_date: false
                    },
                    {
                        id: 9,
                        title: "Commissioned Project",
                        description: "This is a test project. It is solely for testing purposes. A user should never see it.",
                        author: "Alex Krantz",
                        author_id: "google-oauth2|102493818408140086770",
                        status: "working",
                        priority: 2,
                        public: true,
                        deleted: false,
                        commissioned: true,
                        commission_accepted: false,
                        commission_notes: "",
                        commission_cost: 0,
                        commission_start: "",
                        commission_end: "",
                        added_date: new Date().toLocaleDateString(),
                        edited_date: false,
                    }
                ]
            });

        }, 1000);
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
