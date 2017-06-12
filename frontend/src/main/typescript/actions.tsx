import { createAction } from 'redux-actions';

export type SetCollapsedStatePayload = {
    collapse: boolean
}

export const SidebarActions = {
    toggle: createAction('SIDEBAR__TOGGLE'),
    expand: createAction('SIDEBAR__EXPAND'),
    collapse: createAction('SIDEBAR__COLLAPSE'),
    setCollapsedState: createAction<SetCollapsedStatePayload>('SIDEBAR__SET_COLLAPSED_STATE')
};
