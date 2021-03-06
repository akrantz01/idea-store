import React, { Component } from 'react';
import {Button, Dialog, Classes, FormGroup, InputGroup, TextArea, Switch} from "@blueprintjs/core";

class AddProject extends Component {
    constructor(props) {
        super(props);

        this.state = {
            title: "",
            description: "",
            profile: "",
            public: true,
            commission: false
        }
    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        if (prevProps.open !== this.props.open) {
            if (this.props.isAuthenticated()) this.setState({profile: JSON.parse(localStorage.getItem("profile")).name});
            else this.setState({profile: ""});
        }
    }

    handleUpdate(event) {
        switch (event.target.id) {
            case "title":
                this.setState({title: event.target.value});
                break;

            case "description":
                this.setState({description: event.target.value});
                break;

            case "public":
                this.setState({public: !this.state.public});
                break;

            case "commission":
                this.setState({commission: !this.state.commission});
                break;

            default:
                break;
        }
    }

    submit() {
        let profile = JSON.parse(localStorage.getItem("profile"));
        this.props.create(this.state.title, this.state.description, profile.name, profile.sub, this.state.public, this.state.commission);
        this.props.close();
    }

    render() {
        return (
            <Dialog icon="add" title="Add Project" isOpen={this.props.open} onClose={this.props.close}>
                <div className={Classes.DIALOG_BODY}>
                    <FormGroup label="Project Title" labelFor="title">
                        <InputGroup id="title" placeholder="Title..." value={this.state.title} onChange={this.handleUpdate.bind(this)}/>
                    </FormGroup>
                    <FormGroup label="Project Description" labelFor="description">
                        <TextArea id="description" className="bp3-fill" value={this.state.description} placeholder="Description..." onChange={this.handleUpdate.bind(this)}/>
                    </FormGroup>
                    <FormGroup label="Public" labelFor="public" inline={true}>
                        <Switch id="public" checked={this.state.public} onChange={this.handleUpdate.bind(this)}/>
                    </FormGroup>
                    <FormGroup label="Commission" labelFor="commission" inline={true}>
                        <Switch id="commission" checked={this.state.commission} onChange={this.handleUpdate.bind(this)}/>
                    </FormGroup>
                    <FormGroup label="Author" labelFor="author">
                        <InputGroup id="author" value={this.state.profile} disabled={true}/>
                    </FormGroup>
                </div>
                <div className={Classes.DIALOG_FOOTER}>
                    <div className={Classes.DIALOG_FOOTER_ACTIONS}>
                        <Button onClick={this.props.close} minimal={true} text="Cancel"/>
                        <Button onClick={this.submit.bind(this)} intent="success" text="Create"/>
                    </div>
                </div>
            </Dialog>
        )
    }
}

export default AddProject;
