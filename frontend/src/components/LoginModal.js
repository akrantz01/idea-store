import React from 'react';
import PropTypes from 'prop-types';
import { Avatar, Button, CssBaseline, FormControl,
    FormControlLabel, Checkbox, Input, InputLabel,
    Typography } from '@material-ui/core';
import { Lock } from '@material-ui/icons';
import { withStyles } from "@material-ui/core/styles";

const styles = theme => ({
    main: {
        width: "auto",
        display: "block",
        marginLeft: theme.spacing.unit * 3,
        marginRight: theme.spacing.unit * 3,
        [theme.breakpoints.up(400 + theme.spacing.unit * 3)]: {
            width: 400,
            marginLeft: "auto",
            marginRight: "auto"
        }
    },
    wrapper: {
        marginTop: theme.spacing.unit * 8,
        display: 'flex',
        flexDirection: "column",
        alignItems: "center",
        padding: `${theme.spacing.unit * 2}px ${theme.spacing.unit * 3}px ${theme.spacing.unit * 3}px`
    },
    avatar: {
        margin: theme.spacing.unit,
        backgroundColor: theme.palette.secondary.main,
    },
    form: {
        width: '100%',
        marginTop: theme.spacing.unit,
    },
    submit: {
        marginTop: theme.spacing.unit * 3,
    }
});

class LoginModal extends React.Component {
    render() {
        const { classes } = this.props;

        return (
            <main className={classes.main}>
                <CssBaseline/>
                <div className={classes.wrapper}>
                    <Avatar className={classes.avatar}><Lock/></Avatar>
                    <Typography component="h1" variant="h5">Log In</Typography>
                    <form className={classes.form}>
                        <FormControl margin="normal" required fullWidth>
                            <InputLabel htmlFor="email">Email Address</InputLabel>
                            <Input id="email" name="email" autoComplete="email" autoFocus/>
                        </FormControl>
                        <FormControl margin="normal" required fullWidth>
                            <InputLabel htmlFor="password">Password</InputLabel>
                            <Input name="password" type="password" id="password" autoComplete="current-password"/>
                        </FormControl>
                        <FormControlLabel control={<Checkbox value="remember" color="primary"/>} label="Remember Me"/>
                        <Button type="submit" fullWidth variant="contained" color="primary" className={classes.submit}>Log In</Button>
                    </form>
                </div>
            </main>
        );
    }
}

LoginModal.propTypes = {
    classes: PropTypes.object.isRequired
};

export default withStyles(styles)(LoginModal);
