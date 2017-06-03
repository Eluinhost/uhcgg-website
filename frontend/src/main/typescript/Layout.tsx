import * as React from 'react';

import { HashRouter as Router, Link, Switch, Route } from 'react-router-dom';
import { Layout as ALayout, Menu, Breadcrumb } from 'antd';
const { Content, Footer, Sider } = ALayout;

import { RegisterForm } from './RegisterForm';
import {RouteComponentProps} from 'react-router';
import { ApolloExample } from './ApolloExample';

interface RegisterProps extends RouteComponentProps<any> {
    username: string
}

class Register extends React.PureComponent<RegisterProps, any> {
    render() {
        return <div>
            <h1>Final Step!</h1>
            <p style={{fontSize: '120%', marginBottom: 30}}>
                To create your account please provide your chosen email address and password.
            </p>
            <RegisterForm username={decodeURIComponent(this.props.location.hash.substring(1)) /* TODO redireect if none in hash? */ } />
        </div>
    }
}

class Home extends React.PureComponent<any, any> {
    render() {
        return <h1>HOME</h1>;
    }
}

class RegisterError extends React.PureComponent<RouteComponentProps<any>, any> {
    render() {
        return <h1>
            Error:
            {this.props.location.hash ? decodeURIComponent(this.props.location.hash.substring(1)) : 'Unknown error' }
        </h1>;
    }
}

class Example extends React.PureComponent<RouteComponentProps<any>, any> {
    render() {
        return <ApolloExample />;
    }
}

export class Layout extends React.PureComponent<any, any> {
    render() {
        return <Router>
            <ALayout style={{ minHeight: '100%' }}>
                <Content style={{ padding: '0 50px' }}>
                    <Breadcrumb style={{ margin: '12px 0' }}>
                        <Breadcrumb.Item>
                            <Link to="/">Home</Link>
                        </Breadcrumb.Item>
                    </Breadcrumb>
                    <ALayout style={{ padding: '24px 0', background: '#fff' }}>
                        <Sider width={200} style={{ background: '#fff' }}>
                            <Menu
                                mode="inline"
                                defaultSelectedKeys={['1']}
                                defaultOpenKeys={['sub1']}
                                style={{ height: '100%' }}
                            >
                                <Menu.Item key="1"><Link to="/">Home</Link></Menu.Item>
                                <Menu.Item key="2"><Link to="/register">Register</Link></Menu.Item>
                                <Menu.Item key="3"><Link to="/example">Example</Link></Menu.Item>
                            </Menu>
                        </Sider>
                        <Content style={{ padding: '0 24px', minHeight: 280, textAlign: 'center' }}>
                            <Switch>
                                <Route exact path="/" component={Home} />
                                <Route path="/register/error" component={RegisterError} />
                                <Route path="/register" component={Register} />
                                <Route path="/example" component={Example} />
                            </Switch>
                        </Content>
                    </ALayout>
                </Content>
                <Footer style={{ textAlign: 'center' }}>
                    uhc.gg ©2016 Created by Eluinhost
                </Footer>
            </ALayout>
        </Router>;
    }
}