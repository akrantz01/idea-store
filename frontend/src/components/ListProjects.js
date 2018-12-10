import React from 'react';
import PropTypes from 'prop-types';
import axios from 'axios';
import { Typography, LinearProgress, ExpansionPanel,
    ExpansionPanelSummary, ExpansionPanelDetails, Button } from "@material-ui/core";
import { ExpandMore } from '@material-ui/icons';
import { withStyles } from '@material-ui/core/styles';

const styles = theme => ({
    heading: {
        fontSize: theme.typography.pxToRem(15),
        flexBasis: "33.33%",
        flexShrink: 0
    },
    secondaryHeading: {
        fontSize: theme.typography.pxToRem(15),
        color: theme.palette.text.secondary
    }
});

class ListProjects extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            projects: [],
            isLoading: false,
            error: null,
            expanded: null
        };
    }

    expandPanel = panel => (event, expanded) => {
        this.setState({
            expanded: expanded ? panel : false,
        });
    };

    componentDidMount() {
        this.getProjects();
    }

    getProjects() {
        this.setState({ isLoading: true });

        axios.get("http://localhost:8080/api/ideas")
            .then(result => this.setState({
                projects: result.data,
                isLoading: false
            }))
            .catch(error => this.setState({
                error,
                isLoading: false
            }));
    }

    render() {
        const { classes } = this.props;
        const { expanded } = this.state;
        return (
            <div>
                <Typography variant="h3">Projects</Typography>
                <br/>
                <Button variant="contained" color="primary" onClick={this.getProjects.bind(this)}>Refresh</Button>
                <br/><br/>
                {this.state.isLoading &&
                    <div>
                        <br/>
                        <LinearProgress variant="query"/>
                    </div>
                }
                {this.state.error !== null &&
                    <Typography variant="h5" style={{ color: "red", textAlign: "center" }}>{this.state.error.message}</Typography>
                }
                {this.state.projects.length === 0 && this.state.error === null && !this.state.isLoading &&
                    <Typography variant="h5" style={{ textAlign: "center" }}>No Projects in Database</Typography>
                }
                {this.state.projects.length !== 0 && this.state.error === null && !this.state.isLoading &&
                    <div>
                        {this.state.projects.map((prop) => {
                            return (
                                <ExpansionPanel expanded={expanded === prop.id} onChange={this.expandPanel(prop.id)}>
                                    <ExpansionPanelSummary expandIcon={ExpandMore}>
                                        <Typography className={classes.heading}><b>{prop.title}</b></Typography>
                                        <Typography className={classes.secondaryHeading}>ID: {prop.id}</Typography>
                                    </ExpansionPanelSummary>
                                    <ExpansionPanelDetails>
                                        <Typography>{prop.description}</Typography>
                                    </ExpansionPanelDetails>
                                </ExpansionPanel>
                            )
                        })}
                    </div>
                }
            </div>
        )
    }
}

ListProjects.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles, { withTheme: true })(ListProjects);
