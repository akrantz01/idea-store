import {VisibilityFilters, CREATE_PROJECT, UPDATE_PROJECT, DELETE_PROJECT, SET_VISIBILITY_FILTER} from "./actions";
import {combineReducers} from "redux";

const { SHOW_ALL } = VisibilityFilters;

function projects(state=[], action) {
    switch (action.type) {
        case CREATE_PROJECT:
            return [
                ...state,
                {
                    title: action.title,
                    description: action.description,
                    author: action.author
                }
            ];

        case UPDATE_PROJECT:
            return state.map((project, index) => {
                if (index === action.index) {
                    return Object.assign({}, project, {
                        title: (action.title !== "") ? action.title : project.title,
                        description: (action.description !== "") ? action.description : project.description
                    });
                }
                return project;
            });

        case DELETE_PROJECT:
            let s = Object.assign({}, state);
            return s.filter((p, i) => action.index === i);

        default:
            return state;
    }
}

function visibilityFilter(state=SHOW_ALL, action) {
    switch (action.type) {
        case SET_VISIBILITY_FILTER:
            return action.filter;

        default:
            return state
    }
}

const projectsApp = combineReducers({
    visibilityFilter,
    projects
});

export default projectsApp;
