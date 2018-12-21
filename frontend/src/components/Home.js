import React, { Component } from 'react';

class Home extends Component {

    render() {
        const { isAuthenticated } = this.props.auth;
        return (
            <div className="container">
                <h4>Some placeholder text</h4>
            </div>
        );
    }
}

export default Home;
