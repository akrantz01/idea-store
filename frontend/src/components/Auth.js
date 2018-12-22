import Auth0Lock from 'auth0-lock';
import history from '../history';

const clientID = "OOzEz1kAxK8N02IUmipUcDEV7ltq0Abk";
const domain = "alexkrantz.auth0.com";
const redirectURL = "http://localhost:3000/callback";

export default class Auth {
    lock = new Auth0Lock(clientID, domain, {
        autoclose: true,
        auth: {
            redirectUrl: redirectURL,
            responseType: "token id_token",
            params: {
                scope: "openid profile"
            }
        }
    });

    constructor() {
        this.handleAuthentication();

        this.login = this.login.bind(this);
        this.logout = this.logout.bind(this);
        this.isAuthenticated = this.isAuthenticated.bind(this);
        this.isAdmin = this.isAdmin.bind(this);

        this.state = {
            admin: "google-oauth2|102493818408140086770" // TODO: change to use Auth0 app_metadata instead
        }
    }

    login() {
        this.lock.show();
    }

    logout() {
        localStorage.removeItem("access_token");
        localStorage.removeItem("id_token");
        localStorage.removeItem("expires_at");
        localStorage.removeItem("profile");

        history.replace("/home");
    }

    isAuthenticated() {
        return new Date().getTime() < JSON.parse(localStorage.getItem("expires_at"));
    }

    isAdmin(id="") {
        if (id !== "") return this.state.admin === id;
        return this.isAuthenticated() && this.state.admin === JSON.parse(localStorage.getItem("profile")).sub;
    }

    handleAuthentication() {
        this.lock.on("authenticated", this.setSession.bind(this));
        this.lock.on("authorization_error", (err) => {
            console.log(err);
            alert(`Error: ${err.error}. Check the console for further details.`);
            history.replace("/");
        });
    }

    setSession(authResult) {
        if (authResult && authResult.accessToken && authResult.idToken) {
            this.lock.getUserInfo(authResult.accessToken, function(err, profile) {
                if (err) {
                    return;
                }

                localStorage.setItem("access_token", authResult.accessToken);
                localStorage.setItem("id_token", authResult.idToken);
                localStorage.setItem("expires_at", JSON.stringify((authResult.expiresIn * 1000) + new Date().getTime()));
                localStorage.setItem("profile", JSON.stringify(profile));

                history.replace("/home");
            });
        }
    }
}
