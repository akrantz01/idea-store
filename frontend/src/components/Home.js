import React, { Component } from 'react';
import {Card, Elevation, H3, Button} from "@blueprintjs/core";
import ProjectItem from "./ProjectItem";

class Home extends Component {
    state = {
        refreshing: false,
        projects: []
    };

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
                        added_date: new Date().toDateString(),
                        edited_date: false
                    }
                ]
            });

        }, 1000);
    }

    deleteProject(id) {
        let projects = [];
        for (let p of this.state.projects) {
            if (p.id !== id) {
                projects.push(p);
            }
        }
        this.setState({projects: projects});
    }

    editProject(id, data) {
        let projects = this.state.projects.slice();
        for (let p in projects) {
            if (projects[p].id === id) {
                projects[p].title = data.title;
                projects[p].description = data.description;
                projects[p].edited_date = new Date().toDateString();
            }
        }
        this.setState({projects: projects});
    }

    render() {
        const { isAuthenticated } = this.props.auth;

        const style = {
            heading_card: {
                margin: "10px"
            },
            item: {
                card: {
                    margin: "5px 10px",
                },
                info: {
                    boxShadow: "none"
                }
            }
        };

        return (
            <div className="container">
                <Card elevation={Elevation.THREE} style={style.heading_card}>
                    <H3>Projects</H3>
                    <p>Below is a list of projects that I am either currently working on
                        or will be working on in the near future. It also includes already
                        completed projects with links to the source code and where they
                        are running (if they are). If you would like to submit an idea
                        for a project, please login. Below, you can search for and filter
                        any projects.</p>
                    <Button text="Refresh Projects" intent="primary" icon="refresh" className="bp3-small" loading={this.state.refreshing} onClick={this.refreshProjects.bind(this)} />
                </Card>

                {this.state.projects.map((project, key) => <ProjectItem authenticated={isAuthenticated()} data={project}
                                                                        key={key} onDelete={this.deleteProject.bind(this)} onEdit={this.editProject.bind(this)}/>)}
            </div>
        );
    }
}

export default Home;
