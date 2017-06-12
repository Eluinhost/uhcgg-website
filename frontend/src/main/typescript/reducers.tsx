import {SidebarActions} from "./actions";
import {AppStore} from "./AppStore";
import {buildReducer} from "./buildReducer";

export const sidebar = buildReducer<AppStore.Sidebar>()
    .handle(SidebarActions.toggle, (state, action) => ({ collapsed: !state.collapsed }))
    .handle(SidebarActions.expand, (state, action) => ({ collapsed: false }))
    .handle(SidebarActions.collapse, (state, action) => ({ collapsed: true }))
    .handle(SidebarActions.setCollapsedState, (state, action) => ({ collapsed: action.payload!.collapse }))
    .done();
