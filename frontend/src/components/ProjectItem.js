import React, { Component } from 'react';
import {Card, Elevation, Navbar, Alignment, Tag, Tooltip, Position, Button, Collapse} from "@blueprintjs/core";

class ProjectItem extends Component {
    constructor(props) {
        super(props);

        this.state = {
            open: false
        };
    }

    toggle() {
        this.setState({open: !this.state.open});
    }

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
            <Card interactive={true} elevation={Elevation.ONE} style={style.card} onClick={this.toggle.bind(this)}>
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
                                <Tag>Queued</Tag>
                            </Tooltip>
                        )}

                    </Navbar.Group>

                    { this.props.authenticated && (
                        <Navbar.Group align={Alignment.RIGHT}>
                            <Button icon="edit" minimal={true}/>
                            <Navbar.Divider/>
                            <Button icon="delete" minimal={true} intent="danger"/>
                        </Navbar.Group>
                    )}
                </Navbar>
                <Collapse isOpen={this.state.open}>
                    <div className="bp3-running-text">{this.props.data.description}</div><br/>
                    <div className="bp3-text-muted bp3-text-small">Requested By: {this.props.data.author}</div>
                </Collapse>
            </Card>
        )
    }
}

export default ProjectItem;
