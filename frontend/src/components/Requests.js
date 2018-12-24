import React, { Component } from 'react';
import {Alignment, Button, Card, Elevation, H3, Navbar} from "@blueprintjs/core";
import ProjectItem from "./ProjectItem";
import AddProject from "./AddProject";

class Requests extends Component {
    constructor(props) {
        super(props);

        this.state = {
            add: false
        }
    }

    componentDidMount() {
        this.props.refresh();
    }

    filterProjects = () => {
        const { sub } = JSON.parse(localStorage.getItem("profile"));
        return this.props.projects.filter((p) => (sub === p.author_id && !p.deleted));
    };

    toggleAddProject = () => this.setState({add: !this.state.add});

    render() {
        const { isAuthenticated, isAdmin } = this.props.auth;
        const style = {
            heading_card: {
                margin: "10px"
            },
            dne_card: {
                margin: "5px 10px"
            }
        };

        return (
            <div className="container">
                <Card elevation={Elevation.THREE} style={style.heading_card}>
                    <H3>{(isAdmin()) ? "Added" : "Requested"} Projects</H3>
                    <p>Below is a list of projects that you have {(isAdmin()) ? "added" : "requested"} to
                        be created. From here you can view the current statuses and priorities of each, as
                        well as make a commissioned project. If you are curious about the status of a
                        commissioned project, click on the tag called <i>Commissioned</i>. <b>Please note:</b>
                        commissioned projects must follow <i>these guidelines</i> in order to be
                        considered and carried out.</p>
                    <Button text="Commission Project" intent="success" icon="dollar" className="bp3-small" style={{ marginRight: "10px"}} onClick={this.toggleAddProject.bind(this)}/>
                    <Button text="Request Project" intent="primary" icon="plus" className="bp3-small" style={{marginRight: "10px"}} onClick={this.toggleAddProject.bind(this)}/>
                    <Button text="Refresh Projects" icon="refresh" className="bp3-small"
                            loading={this.props.refreshing} onClick={this.props.refresh} />
                </Card>

                { this.filterProjects().length > 0 && this.filterProjects().map((project, key) =>
                    <ProjectItem authenticated={isAuthenticated()} admin={isAdmin} data={project}
                                 key={key} onDelete={this.props.delete} onEdit={this.props.update}
                                 onUndo={this.props.undo} location={this.props.location}/>
                )}

                { this.filterProjects().length === 0 && (
                    <Card elevation={Elevation.ONE} style={style.dne_card}>
                        <Navbar style={{ boxShadow: "none" }}>
                            <Navbar.Group align={Alignment.LEFT}>
                                <Navbar.Heading style={{ fontSize: "18px" }}><b>You have not {(isAdmin()) ? "added" : "requested/commissioned"} any projects</b></Navbar.Heading>
                            </Navbar.Group>

                            <Navbar.Group align={Alignment.RIGHT}>
                                <Button text="Refresh Projects" icon="refresh" className="bp3-small"
                                        loading={this.props.refreshing} onClick={this.props.refresh} />
                            </Navbar.Group>
                        </Navbar>
                    </Card>
                )}
                <AddProject open={this.state.add} close={this.toggleAddProject.bind(this)} create={this.props.create} isAuthenticated={isAuthenticated}/>
            </div>
        )
    }
}

export default Requests;
