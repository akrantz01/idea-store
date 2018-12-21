import ReactDOM from 'react-dom';
import './index.css';
import { makeMainRoutes } from "./routes";
import * as serviceWorker from './serviceWorker';

const routes = makeMainRoutes();

ReactDOM.render(routes, document.getElementById('root'));

// Enable service worker for PWA
serviceWorker.register();
