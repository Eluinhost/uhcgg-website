import * as React from 'react';
import {Link, withRouter, RouteComponentProps, NavLink} from 'react-router-dom';
import {SidebarActions} from "../actions";
import {connect_SD} from "../reduxUtil";
import {Position, Tooltip} from "@blueprintjs/core";

const logoUrl = require('../../images/logo.png');

export type SidebarStateProps = {
    collapsed: boolean
}

export type SidebarDispatchProps = {
    toggle: () => void
}

interface SidebarItemProps {
    text: string,
    icon: string,
    to: string,
    tooltipDisabled: boolean,
    exact?: boolean
}

const SidebarItem: React.SFC<SidebarItemProps> = ({ tooltipDisabled, text, icon , to, exact = false}) =>
    <div className="sidebar-item">
        <NavLink to={to} activeClassName="sidebar-item-active" exact={exact}>
            <Tooltip content={text} isDisabled={tooltipDisabled} position={Position.RIGHT} hoverOpenDelay={400}>
                <span className={`pt-icon pt-icon-${icon}`} />
            </Tooltip>
            <span className="nav-text">{ text }</span>
        </NavLink>
    </div>;

export type SidebarProps = RouteComponentProps<void> & SidebarStateProps & SidebarDispatchProps;

const SidebarComponent: React.SFC<SidebarProps> = ({ location: { pathname }, collapsed, toggle }) =>
    <div className={`${collapsed ? "sidebar sidebar-collapsed" : "sidebar"}`}>
        <div className="sidebar-items">
            <div className="sidebar-logo">
                <Link to="/">
                    <img src={logoUrl} alt="logo" />
                    <div className="nav-text">uhc.gg</div>
                </Link>
            </div>

            <SidebarItem text="Home" icon="home" to="/" exact={true} tooltipDisabled={!collapsed}/>
            <SidebarItem text="Matches" icon="calendar" to="/matches" tooltipDisabled={!collapsed}/>
            <SidebarItem text="Register" icon="user" to="/register" tooltipDisabled={!collapsed}/>
            <SidebarItem text="Networks" icon="database" to="/networks" tooltipDisabled={!collapsed}/>
            <SidebarItem text="API" icon="git-repo" to="/dev" tooltipDisabled={!collapsed}/>
        </div>

        <div className="sidebar-toggle" onClick={toggle}>
            <span className={`pt-icon pt-icon-${collapsed ? 'caret-right' : 'caret-left'}`}/>
        </div>
    </div>;

export const Sidebar: React.ComponentClass =
    withRouter<{}>(
        connect_SD<SidebarStateProps, SidebarDispatchProps>(
            state => ({
                collapsed: state.sidebar.collapsed,
            }),
            dispatch => ({
                toggle: () => dispatch(SidebarActions.toggle())
            })
        )(SidebarComponent)
    );
