import React, { Component } from 'react';
import {Card, Elevation, Navbar, Alignment,
    Tag, Tooltip, Position, Button, Collapse,
    Alert, Dialog, Classes, FormGroup,
    InputGroup, TextArea, HTMLSelect, NumericInput} from "@blueprintjs/core";

class ProjectItem extends Component {
    constructor(props) {
        super(props);

        this.state = {
            description: false,
            deleteWarning: false,
            edit: false,
            editData: {
                title: "",
                description: "",
                status: this.props.data.status,
                priority: this.props.data.priority
            }
        };
    }

    toggleDescription(event) {
        if (event.target.tagName === "DIV" || event.target.tagName === "B") {
            this.setState({description: !this.state.description});
        }
    }

    toggleDeleteWarning = () => this.setState({deleteWarning: !this.state.deleteWarning});

    handleDelete() {
        this.toggleDeleteWarning();
        this.props.onDelete(this.props.data.id);
    }

    toggleEdit = () => this.setState({edit: !this.state.edit});

    handleEdit() {
        this.toggleEdit();
        this.props.onEdit(this.props.data.id, this.state.editData.title, this.state.editData.description, this.state.editData.status, this.state.editData.priority);
        this.setState({editData: {...this.state.editData, title: "", description: ""}});
    }

    handleTitleEdit = (event) => this.setState({editData: {...this.state.editData, title: event.target.value}});

    handleDescriptionEdit = (event) => this.setState({editData: {...this.state.editData, description: event.target.value}});

    handleStatusEdit = (event) => this.setState({editData: {...this.state.editData, status: event.target.value}});

    handlePriorityEdit = (priority) => {
        if (priority > 3) priority = 3;
        else if (priority < 0) priority = 0;
        if (isNaN(priority)) priority = 0;
        this.setState({editData: {...this.state.editData, priority}});
    };

    render() {
        const style = {
            card: {
                margin: "5px 10px"
            },
            info: {
                boxShadow: "none"
            }
        };

        return (
            <div>
                <Card interactive={true} elevation={Elevation.ONE} style={style.card} onClick={this.toggleDescription.bind(this)}>
                    <Navbar style={style.info}>
                        <Navbar.Group align={Alignment.LEFT}>
                            <Navbar.Heading style={{ fontSize: "18px" }}><b>{this.props.data.title}</b></Navbar.Heading>

                            { this.props.data.status === "completed" && (
                                <Tooltip content={`Completed on ${this.props.data.status_date}`} position={Position.TOP}>
                                    <Tag intent="success">Completed</Tag>
                                </Tooltip>
                            )}
                            { this.props.data.status === "working" && (
                                <Tooltip content={`Working on since ${this.props.data.status_date}`} position={Position.TOP}>
                                    <Tag intent="warning">In Progress</Tag>
                                </Tooltip>
                            )}
                            { this.props.data.status === "queued" && (
                                <Tooltip content={`Queued on ${this.props.data.status_date}`} position={Position.TOP}>
                                    <Tag intent="primary">Queued</Tag>
                                </Tooltip>
                            )}
                            { this.props.data.status === "ignored" && (
                                <Tooltip content={`Ignored on ${this.props.data.status_date}`} position={Position.TOP}>
                                    <Tag>Ignored</Tag>
                                </Tooltip>
                            )}

                            { this.props.data.priority !== 0 && <Tag className="priority-tag">Priority: {this.props.data.priority}</Tag>}

                        </Navbar.Group>

                        { this.props.authenticated && (JSON.parse(localStorage.getItem("profile")).sub === this.props.data.author_id || this.props.admin()) && (
                            <Navbar.Group align={Alignment.RIGHT}>
                                <Button icon="edit" minimal={true} onClick={this.toggleEdit.bind(this)}/>
                                <Navbar.Divider/>
                                <Button icon="delete" minimal={true} intent="danger" onClick={this.toggleDeleteWarning.bind(this)}/>
                            </Navbar.Group>
                        )}
                    </Navbar>
                    <Collapse isOpen={this.state.description}>
                        <div className="bp3-running-text">{this.props.data.description}</div><br/>

                        { this.props.data.edited_date && (
                            <Tooltip content={`Edited on ${this.props.data.edited_date}`} position={Position.BOTTOM}>
                                <div className="bp3-text-muted bp3-text-small"><b>{(this.props.admin(this.props.data.author_id)) ? "Added" : "Requested"} by {this.props.data.author}</b> on {this.props.data.added_date}</div>
                            </Tooltip>
                        )}
                        { !this.props.data.edited_date && <div className="bp3-text-muted bp3-text-small"><b>{(this.props.admin(this.props.data.author_id)) ? "Added" : "Requested"} by {this.props.data.author}</b> on {this.props.data.added_date}</div>}
                    </Collapse>
                </Card>

                <Alert cancelButtonText="Nevermind" confirmButtonText="Delete" icon="trash" intent="danger"
                       isOpen={this.state.deleteWarning} onCancel={this.toggleDeleteWarning.bind(this)} onConfirm={this.handleDelete.bind(this)}>
                    <p>Are you sure you want to delete this project? It will be gone forever unless the administrator re-opens it.</p>
                </Alert>

                <Dialog icon="edit" onClose={this.toggleEdit.bind(this)} isOpen={this.state.edit} title="Edit Project" autoFocus={true} canEscapeKeyClose={true} canOutsideClickClose={true} enforceFocus={true} usePortal={true}>
                    <div className={Classes.DIALOG_BODY}>
                        <FormGroup label="Project Title" labelFor="title">
                            <InputGroup id="title" placeholder={this.props.data.title} value={this.state.editData.title} onChange={this.handleTitleEdit.bind(this)}/>
                        </FormGroup>
                        <FormGroup label="Project Description" labelFor="description">
                            <TextArea className="bp3-fill" value={this.state.editData.description} placeholder={this.props.data.description} onChange={this.handleDescriptionEdit.bind(this)}/>
                        </FormGroup>
                        { this.props.authenticated && this.props.admin() && (
                            <>
                                <FormGroup label="Status:" labelFor="status" inline={true}>
                                    <HTMLSelect id="status" value={this.state.editData.status} onChange={this.handleStatusEdit.bind(this)}>
                                        <option value="completed">Completed</option>
                                        <option value="working">In Progress</option>
                                        <option value="queued">Queued</option>
                                    </HTMLSelect>
                                </FormGroup>
                                <FormGroup label="Priority:" labelFor="priority" inline={true}>
                                    <NumericInput id="priority" name="ns-priority" minorStepSize={null} majorStepSize={null} min={0} max={3} value={this.state.editData.priority} onValueChange={this.handlePriorityEdit.bind(this)}/>
                                </FormGroup>
                            </>
                        )}
                    </div>
                    <div className={Classes.DIALOG_FOOTER}>
                        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
                            <Button onClick={this.toggleEdit.bind(this)} minimal={true} text="Cancel"/>
                            <Button onClick={this.handleEdit.bind(this)} intent="success" text="Change"/>
                        </div>
                    </div>
                </Dialog>
            </div>
        )
    }
}

export default ProjectItem;
