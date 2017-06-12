import * as React from 'react';
import { Link, withRouter, RouteComponentProps } from 'react-router-dom';
import { Menu, Layout, Icon } from 'antd';
import {connect} from "react-redux";
import {AppStore} from "../AppStore";
import {SidebarActions} from "../actions";

const logoUrl = require('../../images/logo.png');

export interface SidebarStateProps {
    collapsed: boolean
}

export interface SidebarDispatchProps {
    toggle: () => void
}

export interface SiderSFC extends React.SFC<RouteComponentProps<{}> & SidebarStateProps & SidebarDispatchProps> {
    __ANT_LAYOUT_SIDER?: boolean
}

const SidebarComponent: SiderSFC = ({ location: { pathname }, collapsed, toggle }) =>
    <Layout.Sider width={200} style={{ height: '100vh', overflow: 'auto' }} collapsible={true} collapsed={collapsed} onCollapse={toggle} >
        <div className="logo">
            <Link to="/">
                <img src={logoUrl} alt="logo" />
                <div className="nav-text">uhc.gg</div>
            </Link>
        </div>

        <Menu
            mode="inline"
            theme="dark"
            selectedKeys={[ pathname ]}
            style={{ height: '100%' }}
        >
            <Menu.Item key="/">
                <Link to="/">
                    <span>
                        <Icon type="home" />
                        <span className="nav-text">Home</span>
                    </span>
                </Link>
            </Menu.Item>
            <Menu.Item key="/register">
                <Link to="/register">
                    <span>
                        <Icon type="user" />
                        <span className="nav-text">Register</span>
                     </span>
                </Link>
            </Menu.Item>
            <Menu.Item key="/networks">
                <Link to="/networks">
                    <span>
                        <Icon type="database" />
                        <span className="nav-text">Networks</span>
                    </span>
                </Link>
            </Menu.Item>
        </Menu>
    </Layout.Sider>;

SidebarComponent.__ANT_LAYOUT_SIDER = true;

export const Sidebar: React.ComponentClass<{}> = connect(
    (state: AppStore.All) => ({
        collapsed: state.sidebar.collapsed
    }),
    dispatch => ({
        toggle: () => dispatch(SidebarActions.toggle())
    })
)(withRouter<{}>(SidebarComponent));