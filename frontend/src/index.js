import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Routes from "./routes";
import * as serviceWorker from './serviceWorker';

ReactDOM.render(<Routes/>, document.getElementById('root'));

// Enable service worker for PWA
serviceWorker.register();
