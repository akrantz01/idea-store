import React from 'react';
import PropTypes from 'prop-types';
import { AppBar, CssBaseline, Drawer, List, ListItem,
  ListItemIcon, ListItemText, Toolbar, Typography,
  IconButton, Button} from '@material-ui/core';
import { Menu } from '@material-ui/icons';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';
import { withStyles } from '@material-ui/core/styles';
import routes from './routes';

const styles = theme => ({
  root: {
    display: 'flex',
  },
  toolbar: theme.mixins.toolbar,
  content: {
    flexGrow: 1,
    padding: theme.spacing.unit * 3,
  },
});

class App extends React.Component {
  constructor(props) {
    super(props);

    this.activeRoute = this.activeRoute.bind(this);
  }

  state = {
    drawerOpen: false,
    authenticated: false
  };

  toggleDrawer = () => {
    this.setState(state => ({ drawerOpen: !state.drawerOpen }));
  };

  activeRoute = routeName => {
    return this.props.location.pathname.indexOf(routeName) > -1;
  };

  render() {
    const { classes } = this.props;
    return (
        <Router>
          <div className={classes.root}>
            <CssBaseline />
            <AppBar position="fixed">
              <Toolbar>
                <IconButton color="inherit" aria-label="Open drawer" onClick={this.toggleDrawer}>
                  <Menu />
                </IconButton>
                <Typography variant="h6" color="inherit" noWrap style={{ paddingLeft: "10px", flexGrow: "1" }}>Idea Storage</Typography>
                <Button color="secondary" variant="contained">{ this.state.authenticated ? "Logout" : "Login" }</Button>
              </Toolbar>
            </AppBar>
            <Drawer anchor="left" open={this.state.drawerOpen} onClose={this.toggleDrawer}>
              <div tabIndex={0} role="button" onClick={this.toggleDrawer} onKeyDown={this.toggleDrawer}>
                <List>
                  {routes.map((prop, key) => {
                    return (
                        <Link to={prop.path} style={{ textDecoration: "none" }} key={key}>
                          <ListItem button>
                            <ListItemIcon><prop.icon/></ListItemIcon>
                            <ListItemText primary={prop.sidebarName}/>
                          </ListItem>
                        </Link>
                    )
                  })}
                </List>
              </div>
            </Drawer>
            <main className={classes.content}>
              <div className={classes.toolbar} />
              {routes.map((prop, key) => {
                return (
                    <Route path={prop.path} exact={prop.exact} component={prop.component} key={key}/>
                )
              })}
            </main>
          </div>
        </Router>
    );
  }
}

App.propTypes = {
  classes: PropTypes.object.isRequired,
};

export default withStyles(styles, { withTheme: true })(App);