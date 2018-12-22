import React, { Component } from 'react';
import {Card, Elevation, H3, H4, H6, Button, MenuItem, Switch, Navbar, Alignment} from "@blueprintjs/core";
import {MultiSelect} from "@blueprintjs/select";
import { filterProject, highlightText } from './Project';
import ProjectItem from "./ProjectItem";

class Home extends Component {
    state = {
        selectedProjects: [],
        displayedProjects: [],
        filters: {
            completed: false,
            working: true,
            queued: true
        }
    };

    componentDidMount() {
        this.props.refresh();
        document.evaluate("//input[@class='bp3-input-ghost']", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.style.width = "200px";
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevState.selectedProjects !== this.state.selectedProjects || prevProps.projects !== this.props.projects || prevState.filters !== this.state.filters) {
            if (this.state.selectedProjects.length === 0) this.setState({displayedProjects: this.filterProjects(this.props.projects.slice())});
            else this.setState({displayedProjects: this.filterProjects(this.state.selectedProjects.slice())});
        }
    }

    filterProjects(projects) {
        return projects.filter((p) => {
            if (this.state.filters.queued && p.status === "queued") return true;
            if (this.state.filters.working && p.status === "working") return true;
            return this.state.filters.completed && p.status === "completed";
        });
    }

    handleFilterChange(filter) {
        switch (filter.target.name) {
            case "cb-completed":
                this.setState({filters: {...this.state.filters, completed: !this.state.filters.completed}});
                break;

            case "cb-working":
                this.setState({filters: {...this.state.filters, working: !this.state.filters.working}});
                break;

            case "cb-queued":
                this.setState({filters: {...this.state.filters, queued: !this.state.filters.queued}});
                break;

            default:
                break;
        }
    }

    renderTag = (project) => project.title;

    handleTagRemove(_tag, index) {
        this.setState({selectedProjects: this.state.selectedProjects.filter((_p, i) => i !== index)});
    }

    handleProjectSelect(project) {
        if (!this.isProjectSelected(project)) this.selectProject(project);
        else this.deselectProject(this.getSelectedProjectIndex(project));
    }

    handleClearTags() {
        this.setState({selectedProjects: []})
    }

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
        const clearTags = this.state.selectedProjects.length > 0 ? <Button icon="cross" minimal={true} onClick={this.handleClearTags.bind(this)}/> : null;

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
                    <Button text="Refresh Projects" intent="primary" icon="refresh" className="bp3-small"
                            loading={this.props.refreshing} onClick={this.props.refresh} />
                </Card>

                <Card elevation={Elevation.TWO} style={style.heading_card}>
                    <H4>Search</H4>
                    <MultiSelect
                        initialContent={undefined}
                        itemPredicate={filterProject}
                        itemRenderer={this.renderProject}
                        items={this.props.projects}
                        noResults={<MenuItem text={"No Results."} disabled={true}/>}
                        onItemSelect={this.handleProjectSelect.bind(this)}
                        popoverProps={{ minimal: true }}
                        resetOnSelect={true}
                        tagRenderer={this.renderTag}
                        tagInputProps={{ tagProps: {intent: "none", minimal: false}, onRemove: this.handleTagRemove.bind(this), rightElement: clearTags }}
                        selectedItems={this.state.selectedProjects}
                        placeholder="Select Projects..."
                    />
                    <br/><br/>
                    <H6>Filters:</H6>
                    <Switch name="cb-completed" inline={true} label="Completed" checked={this.state.filters.completed} onChange={this.handleFilterChange.bind(this)}/>
                    <Switch name="cb-working" inline={true} label="In Progress" checked={this.state.filters.working} onChange={this.handleFilterChange.bind(this)}/>
                    <Switch name="cb-queued" inline={true} label="Queued" checked={this.state.filters.queued} onChange={this.handleFilterChange.bind(this)}/>
                </Card>

                {this.state.displayedProjects.length > 0 && this.state.displayedProjects.map((project, key) =>
                    <ProjectItem authenticated={isAuthenticated()} admin={isAdmin()} data={project} key={key} onDelete={this.props.delete}
                                 onEdit={this.props.update}/>)}

                {this.state.displayedProjects.length === 0 && (
                    <Card elevation={Elevation.ONE} style={style.dne_card}>
                        <Navbar style={{ boxShadow: "none" }}>
                            <Navbar.Group align={Alignment.LEFT}>
                                <Navbar.Heading style={{ fontSize: "18px" }}><b>No Projects in Database</b></Navbar.Heading>
                            </Navbar.Group>

                            <Navbar.Group align={Alignment.RIGHT}>
                                <Button text="Refresh Projects" intent="primary" icon="refresh" className="bp3-small"
                                        loading={this.props.refreshing} onClick={this.props.refresh} />
                            </Navbar.Group>
                        </Navbar>
                    </Card>
                )}
            </div>
        );
    }

    renderProject = (project, { handleClick, modifiers, query }) => {
        if (!modifiers.matchesPredicate) {
            return null;
        }

        return (
            <MenuItem
                active={modifiers.active}
                icon={this.isProjectSelected(project) ? "tick" : "blank"}
                key={project.id}
                label={project.author}
                onClick={handleClick}
                text={highlightText(project.title, query)}
                shouldDismissPopover={false}
            />
        );
    };

    getSelectedProjectIndex = (project) => {return this.state.selectedProjects.indexOf(project)};

    isProjectSelected = (project) => {return this.getSelectedProjectIndex(project) !== -1};

    selectProject = (project) => {this.setState({selectedProjects: [...this.state.selectedProjects, project]})};

    deselectProject = (index) => {this.setState({selectedProjects: this.state.selectedProjects.filter((_p, i) => i !== index)})};

}

export default Home;
