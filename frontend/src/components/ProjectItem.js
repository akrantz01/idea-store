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
            undoWarning: false,
            edit: false,
            commission: false,
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

    toggleUndoWarning = () => this.setState({undoWarning: !this.state.undoWarning});

    handleTemporaryDelete() {
        this.toggleDeleteWarning();
        this.props.onDelete(this.props.data.id);
    }

    handlePermanentDelete() {
        this.toggleDeleteWarning();
        this.props.onDelete(this.props.data.id, true);
    }

    handleUndo() {
        this.toggleUndoWarning();
        this.props.onUndo(this.props.data.id);
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

    toggleCommissionStatus = () => this.setState({commission: !this.state.commission});

    handleEditRequest = () => {
        // TODO: add API call to request an edit
        console.log("Edit requested");
    };

    handleDeletionRequest = () => {
        // TODO: add API call to request deletion
        console.log("Deletion requested");
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

                            { this.props.data.status === "completed" && <Tag intent="success">Completed</Tag>}
                            { this.props.data.status === "working" && <Tag intent="warning">In Progress</Tag>}
                            { this.props.data.status === "queued" && <Tag intent="primary">Queued</Tag>}
                            { this.props.data.status === "ignored" && <Tag>Ignored</Tag>}

                            { this.props.data.commissioned && this.props.location.pathname === "/requests" && (
                                <Tooltip content="Click me to view the current status">
                                    <Tag className="stacked-tag" intent="danger" interactive={true} onClick={this.toggleCommissionStatus.bind(this)}>Commissioned</Tag>
                                </Tooltip>
                            )}
                            { this.props.data.priority !== 0 && <Tag className="stacked-tag">Priority: {this.props.data.priority}</Tag>}
                            { !this.props.data.public && <Tag className="stacked-tag" intent="danger">Private</Tag> }

                        </Navbar.Group>

                        { this.props.authenticated && !this.props.data.deleted && (JSON.parse(localStorage.getItem("profile")).sub
                            === this.props.data.author_id || this.props.admin()) && (
                            <Navbar.Group align={Alignment.RIGHT}>
                                <Button icon="edit" minimal={true} onClick={this.toggleEdit.bind(this)} disabled={this.props.data.commission_accepted}/>
                                <Navbar.Divider/>
                                <Button icon="delete" minimal={true} intent="danger" onClick={this.toggleDeleteWarning.bind(this)} disabled={this.props.data.commission_accepted}/>
                            </Navbar.Group>
                        )}
                        { this.props.authenticated && this.props.data.deleted && this.props.admin() && (
                            <Navbar.Group align={Alignment.RIGHT}>
                                <Button icon="undo" minimal={true} onClick={this.toggleUndoWarning.bind(this)}/>
                                <Navbar.Divider/>
                                <Button icon="delete" minimal={true} intent="danger" onClick={this.toggleDeleteWarning.bind(this)}/>
                            </Navbar.Group>
                        )}
                    </Navbar>
                    <Collapse isOpen={this.state.description}>
                        <div className="bp3-running-text">{this.props.data.description}</div><br/>

                        { this.props.data.edited_date && (
                            <Tooltip content={`Edited on ${this.props.data.edited_date}`} position={Position.BOTTOM}>
                                <div className="bp3-text-muted bp3-text-small"><b>{(this.props.admin(this.props.data.author_id)) ? "Added" : "Requested"} by
                                    {this.props.data.author}</b> on {this.props.data.added_date}</div>
                            </Tooltip>
                        )}
                        { !this.props.data.edited_date && <div className="bp3-text-muted bp3-text-small"><b>{(this.props.admin(this.props.data.author_id))
                            ? "Added" : "Requested"} by {this.props.data.author}</b> on {this.props.data.added_date}</div>}
                    </Collapse>
                </Card>

                <Alert cancelButtonText="Nevermind" confirmButtonText="Delete" icon="trash" intent="danger"
                       isOpen={this.state.deleteWarning} onCancel={this.toggleDeleteWarning.bind(this)} onConfirm={(this.props.data.deleted)
                    ? this.handlePermanentDelete.bind(this) : this.handleTemporaryDelete.bind(this)}>
                    <p>Are you sure you want to delete this project? It will be gone forever{(this.props.data.deleted) ? "." : " unless the administrator re-opens it."}</p>
                </Alert>

                <Alert cancelButtonText="Nevermind" confirmButtonText="Undo" icon="undo" intent="warning" isOpen={this.state.undoWarning}
                       onClose={this.toggleUndoWarning.bind(this)} onConfirm={this.handleUndo.bind(this)}>
                    <p>Are you sure you want to undo the deletion of a project?</p>
                </Alert>

                <Dialog icon="edit" onClose={this.toggleEdit.bind(this)} isOpen={this.state.edit} title="Edit Project"
                        autoFocus={true} canEscapeKeyClose={true} canOutsideClickClose={true} enforceFocus={true} usePortal={true}>
                    <div className={Classes.DIALOG_BODY}>
                        <FormGroup label="Project Title" labelFor="title">
                            <InputGroup id="title" placeholder={this.props.data.title} value={this.state.editData.title} onChange={this.handleTitleEdit.bind(this)}/>
                        </FormGroup>
                        <FormGroup label="Project Description" labelFor="description">
                            <TextArea className="bp3-fill" value={this.state.editData.description}
                                      placeholder={this.props.data.description} onChange={this.handleDescriptionEdit.bind(this)}/>
                        </FormGroup>
                        { this.props.authenticated && this.props.admin() && !this.props.data.commissioned && (
                            <>
                                <FormGroup label="Status:" labelFor="status" inline={true}>
                                    <HTMLSelect id="status" value={this.state.editData.status} onChange={this.handleStatusEdit.bind(this)}>
                                        <option value="completed">Completed</option>
                                        <option value="working">In Progress</option>
                                        <option value="queued">Queued</option>
                                    </HTMLSelect>
                                </FormGroup>
                                <FormGroup label="Priority:" labelFor="priority" inline={true}>
                                    <NumericInput id="priority" name="ns-priority" minorStepSize={null} majorStepSize={null} min={0}
                                                  max={3} value={this.state.editData.priority} onValueChange={this.handlePriorityEdit.bind(this)}/>
                                </FormGroup>
                            </>
                        )}
                    </div>
                    <div className={Classes.DIALOG_FOOTER}>
                        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
                            <Button onClick={this.toggleEdit.bind(this)} minimal={true} text="Cancel"/>
                            { !this.props.data.commission_accepted && <Button onClick={this.handleEdit.bind(this)} intent="success" text="Change"/> }
                            { this.props.data.commission_accepted && <Button onClick={this.handleEditRequest.bind(this)} intent="success" text="Change"/> }
                        </div>
                    </div>
                </Dialog>

                <Dialog icon="dollar" onClose={this.toggleCommissionStatus.bind(this)} isOpen={this.state.commission} title="Commissioned Project Status"
                        autoFocus={true} canEscapeKeyClose={true} canOutsideClickClose={true} enforceFocus={true} usePortal={true}>
                    <div className={Classes.DIALOG_BODY}>
                        <div className="bp3-text-large">Commission Accepted: {(this.props.data.commission_accepted) ?
                            <span style={{color: "#0f9960"}}>Yes</span> : <span style={{color:"#db3737"}}>No</span>}</div>
                        <br/>
                        {this.props.data.commission_accepted && (
                            <>
                                <div className="bp3-ui-text">Planned Cost: ${this.props.data.commission_cost}</div>
                                <div className="bp3-ui-text">Start Date: {this.props.data.commission_start}</div>
                                <div className="bp3-ui-text">End Date: {this.props.data.commission_end}</div>
                                <br/>
                                <div className="bp3-ui-text">Notes:<br/><span className="bp3-running-text">{this.props.data.commission_notes}</span></div>
                            </>
                        )}
                    </div>
                    <div className={Classes.DIALOG_FOOTER}>
                        <div className={Classes.DIALOG_FOOTER_ACTIONS}>
                            {this.props.data.commission_accepted && (
                                <>
                                    <Button onClick={this.handleDeletionRequest.bind(this)} intent="danger" text="Request Deletion"/>
                                    <Button onClick={this.toggleEdit.bind(this)} intent="warning" text="Request Edit"/>
                                </>
                            )}
                            <Button onClick={this.toggleCommissionStatus.bind(this)} intent="primary" text="Close" />
                        </div>
                    </div>
                </Dialog>
            </div>
        )
    }
}

export default ProjectItem;
