import Auth0Lock from 'auth0-lock';
import jwtDecode from 'jwt-decode';
import history from '../history';

const clientID = "OOzEz1kAxK8N02IUmipUcDEV7ltq0Abk";
const domain = "alexkrantz.auth0.com";
const redirectURL = "http://localhost:3000/callback";
const adminID = "google-oauth2|102493818408140086770";

export default class Auth {
    lock = new Auth0Lock(clientID, domain, {
        autoclose: true,
        auth: {
            audience: "https://alexkrantz.auth0.com/userinfo",
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
        if (id !== "") return adminID === id;
        return this.isAuthenticated() && adminID === JSON.parse(localStorage.getItem("profile")).sub
            && jwtDecode(localStorage.getItem("id_token"))["https://alexkrantz.com/app_metadata"].isAdmin;
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
