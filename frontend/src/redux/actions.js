// Actions
export const CREATE_PROJECT = "CREATE_PROJECT";
export const UPDATE_PROJECT = "UPDATE_PROJECT";
export const DELETE_PROJECT = "DELETE_PROJECT";
export const SET_VISIBILITY_FILTER = "SET_VISIBILITY_FILTER";

// Other
export const VisibilityFilters = {
    SHOW_NONE: "SHOW_NONE",
    SHOW_ALL: "SHOW_ALL",
    SHOW_QUEUED: "SHOW_QUEUED",
    SHOW_WORKING: "SHOW_WORKING",
    SHOW_COMPLETED: "SHOW_COMPLETED",
    SHOW_QUEUED_WORKING: "SHOW_QUEUED_WORKING",
    SHOW_WORKING_QUEUED: this.SHOW_QUEUED_WORKING,
    SHOW_WORKING_COMPLETED: "SHOW_WORKING_COMPLETED",
    SHOW_COMPLETED_WORKING: this.SHOW_WORKING_COMPLETED,
    SHOW_COMPLETED_QUEUED: "SHOW_COMPLETED_QUEUED",
    SHOW_QUEUED_COMPLETED: this.SHOW_COMPLETED_QUEUED
};

// Action Creators
export function createProject(title, description, author) {
    return {type: CREATE_PROJECT, title, description, author}
}

export function updateProject(index, title="", description="") {
    return {type: UPDATE_PROJECT, index, title, description}
}

export function deleteProject(index) {
    return {type: DELETE_PROJECT, index}
}

export function setVisibilityFilter(filter) {
    return {type: SET_VISIBILITY_FILTER, filter}
}
