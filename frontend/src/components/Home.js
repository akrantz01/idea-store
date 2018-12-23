import React, { Component } from 'react';
import {Card, Elevation, H3, H4, H6, Button,
    MenuItem, Switch, Navbar, Alignment, Tab,
    Tabs, FormGroup, Tooltip, Position, NumericInput} from "@blueprintjs/core";
import {MultiSelect} from "@blueprintjs/select";
import {DateRangeInput} from "@blueprintjs/datetime";
import { filterProject, highlightText } from './Project';
import ProjectItem from "./ProjectItem";

class Home extends Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedProjects: [],
            displayedProjects: [],
            filters: {
                completed: false,
                working: true,
                queued: true,
                ignored: false,
                priority: 0,
                private: false,
                createRange: [null, null],
                editRange: [null, null]
            },
            currentTab: "default"
        };
    }

    componentDidMount() {
        this.props.refresh();
        document.evaluate("//input[@class='bp3-input-ghost']", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.style.width = "200px";
        document.getElementById("priority").style.width = "50px";
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevState.selectedProjects !== this.state.selectedProjects || prevProps.projects !== this.props.projects || prevState.filters !== this.state.filters) {
            if (this.state.selectedProjects.length === 0) this.setState({displayedProjects: this.filterProjects(this.props.projects.slice())});
            else this.setState({displayedProjects: this.filterProjects(this.state.selectedProjects.slice())});
        }

        if (prevProps.loggedOut !== this.props.loggedOut) {
            this.setState({
                currentTab: "default",
                filters: {
                    ...this.state.filters,
                    private: false,
                    editRange: [null, null]
                }
            });
        }
    }

    filterProjects(projects) {
        return projects.filter((p) => {
            if (this.state.filters.createRange[0] !== null && this.state.filters.createRange[1] !== null) {
                if (!(this.state.filters.createRange[0] <= new Date(p.added_date) && new Date(p.added_date) <= this.state.filters.createRange[1])) return false;
            }

            if (this.props.auth.isAdmin() && this.state.filters.editRange[0] !== null && this.state.filters.editRange[1] !== null) {
                if (p.edited_date === false) return false;
                if (!(this.state.filters.editRange[0] <= new Date(p.edited_date) && new Date(p.edited_date) <= this.state.filters.editRange[1])) return false;
            }

            if (this.state.filters.priority > p.priority) return false;
            if (!this.state.filters.private && !p.public) return false;

            if (this.state.filters.ignored && p.status === "ignored") return true;
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

            case "cb-ignored":
                this.setState({filters: {...this.state.filters, ignored: !this.state.filters.ignored}});
                break;

            case "cb-private":
                this.setState({filters: {...this.state.filters, private: !this.state.filters.private}});
                break;

            default:
                break;
        }
    }

    renderTag = (project) => project.title;

    handleTagRemove = (_tag, index) => this.setState({selectedProjects: this.state.selectedProjects.filter((_p, i) => i !== index)});

    handleProjectSelect(project) {
        if (!this.isProjectSelected(project)) this.selectProject(project);
        else this.deselectProject(this.getSelectedProjectIndex(project));
    }

    handleClearTags = () => this.setState({selectedProjects: []});

    handleTabChange = (currentTab) => this.setState({currentTab});

    handlePriorityChange = (priority) => {
        if (priority > 3) priority = 3;
        else if (priority < 0) priority = 0;
        if (isNaN(priority)) priority = 0;
        this.setState({filters: {...this.state.filters, priority}});
    };

    handleCreateDateRangeChange = (createRange) => this.setState({filters: {...this.state.filters, createRange}});

    handleCreateDateRangeClear = () => this.setState({filters: {...this.state.filters, createRange: [null, null]}});

    handleEditDateRangeChange = (editRange) => this.setState({filters: {...this.state.filters, editRange}});

    handleEditDateRangeClear = () => this.setState({filters: {...this.state.filters, editRange: [null, null]}});

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
                { (!this.props.adminView || !isAdmin()) && (
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
                )}

                <Card elevation={(isAdmin() && this.props.adminView) ? Elevation.THREE : Elevation.TWO} style={style.heading_card}>

                    { isAdmin() && this.props.adminView && (
                        <>
                            <Tabs id="admin-view" onChange={this.handleTabChange} selectedTabId={this.state.currentTab}>
                                <Tab id="default" title="All Projects"/>
                                <Tab id="new" title="Newly Created"/>
                                <Tab id="deleted" title="Deleted Projects"/>
                            </Tabs>
                            <br/>
                        </>
                    )}

                    { (isAdmin() && this.props.adminView) ? <H6>Search</H6> : <H4>Search</H4>}
                    <MultiSelect
                        initialContent={undefined}
                        itemPredicate={filterProject}
                        itemRenderer={this.renderProject}
                        items={this.state.displayedProjects}
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
                    <Tooltip content="Projects that I have already completed" intent="success" position={Position.BOTTOM}>
                        <Switch name="cb-completed" inline={true} label="Completed" checked={this.state.filters.completed} onChange={this.handleFilterChange.bind(this)}/>
                    </Tooltip>
                    <Tooltip content="Projects that I am currently working on" intent="warning" position={Position.BOTTOM}>
                        <Switch name="cb-working" inline={true} label="In Progress" checked={this.state.filters.working} onChange={this.handleFilterChange.bind(this)}/>
                    </Tooltip>
                    <Tooltip content="Projects that I plan to work on sometime in the future" intent="primary" position={Position.BOTTOM}>
                        <Switch name="cb-queued" inline={true} label="Queued" checked={this.state.filters.queued} onChange={this.handleFilterChange.bind(this)}/>
                    </Tooltip>
                    <Tooltip content="Projects that I don't currently plan on working on" position={Position.BOTTOM}>
                        <Switch name="cb-ignored" inline={true} label="Ignored" checked={this.state.filters.ignored} onChange={this.handleFilterChange.bind(this)}/>
                    </Tooltip>
                    { isAdmin() && this.props.adminView && (
                        <Tooltip content={"Privately request projects"} intent="danger" position={Position.BOTTOM}>
                            <Switch name="cb-private" inline={true} label="Private" checked={this.state.filters.private} onChange={this.handleFilterChange.bind(this)}/>
                        </Tooltip>
                    )}
                    <br/>

                    <Tooltip content={"What I am most to least likely to work on with 0 being the lowest and 3 being the highest"} position={Position.TOP}>
                        <FormGroup label="Minimum Priority:" labelFor="priority" inline={true}>
                            <NumericInput id="priority" name="ns-priority" minorStepSize={null} majorStepSize={null} min={0}
                                          max={3} value={this.state.filters.priority} onValueChange={this.handlePriorityChange.bind(this)}/>
                        </FormGroup>
                    </Tooltip>

                    <FormGroup label="Date Created:" labelFor="create-daterange" inline={true}>
                        <DateRangeInput id="create-daterange" formatDate={date => date.toLocaleDateString()} parseDate={str => new Date(str)}
                                        allowSingleDayRange={true} value={this.state.filters.createRange} onChange={this.handleCreateDateRangeChange.bind(this)}/>
                        <Button icon="cross" minimal={true} onClick={this.handleCreateDateRangeClear.bind(this)}/>
                    </FormGroup>

                    { isAdmin() && this.props.adminView && (
                        <>
                            <FormGroup label="Date Edited:" labelFor="edit-daterange" inline={true}>
                                <DateRangeInput id="edit-daterange" formatDate={date => date.toLocaleDateString()} parseDate={str => new Date(str)}
                                                allowSingleDayRange={true} value={this.state.filters.editRange} onChange={this.handleEditDateRangeChange.bind(this)}/>
                                <Button icon="cross" minimal={true} onClick={this.handleEditDateRangeClear.bind(this)}/>
                            </FormGroup>
                        </>
                    )}

                    { isAdmin() && this.props.adminView && (
                        <>
                            <br/>
                            <Button text="Refresh Projects" intent="primary" icon="refresh" className="bp3-small"
                                    loading={this.props.refreshing} onClick={this.props.refresh} />
                        </>
                    )}
                </Card>

                { (!isAdmin() || this.state.currentTab === "default") && (
                    <>
                        {this.state.displayedProjects.length > 0 && this.state.displayedProjects.map((project, key) =>
                                <ProjectItem authenticated={isAuthenticated()} admin={isAdmin} data={project} key={key} onDelete={this.props.delete}
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
                    </>
                )}

                { isAdmin() && this.props.adminView && this.state.currentTab === "private" && (
                    <p>Private Projects</p>
                )}

                { isAdmin() && this.props.adminView && this.state.currentTab === "priority" && (
                    <p>Projects Without Priority</p>
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
