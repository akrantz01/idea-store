import { ViewList, PlaylistAdd, AccountBox } from '@material-ui/icons';

const Routes = [
    {
        path: "/",
        sidebarName: "View Projects",
        navBarName: "View Projects",
        icon: ViewList,
        component: "",
        exact: true
    },
    {
        path: "/add",
        sidebarName: "Add Project",
        navBarName: "Add Project",
        icon: PlaylistAdd,
        component: "",
        exact: false
    },
    {
        path: "/login",
        sidebarName: "Login",
        navBarName: "Login",
        icon: AccountBox,
        component: "",
        exact: false
    }
];

export default Routes;
