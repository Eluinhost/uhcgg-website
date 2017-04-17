import 'antd/dist/antd.css'

import ReactDOM from 'react-dom';

import { Layout, Menu, Breadcrumb } from 'antd';
const { Content, Footer, Sider } = Layout;

import { RegisterForm } from './RegisterForm'

ReactDOM.render(
    <Layout>
        <Content style={{ padding: '0 50px' }}>
            <Breadcrumb style={{ margin: '12px 0' }}>
                <Breadcrumb.Item>Home</Breadcrumb.Item>
                <Breadcrumb.Item>Register</Breadcrumb.Item>
            </Breadcrumb>
            <Layout style={{ padding: '24px 0', background: '#fff' }}>
                <Sider width={200} style={{ background: '#fff' }}>
                    <Menu
                        mode="inline"
                        defaultSelectedKeys={['1']}
                        defaultOpenKeys={['sub1']}
                        style={{ height: '100%' }}
                    >
                        <Menu.Item key="1">Register</Menu.Item>
                    </Menu>
                </Sider>
                <Content style={{ padding: '0 24px', minHeight: 280, textAlign: 'center' }}>
                    <h1>Final Step!</h1>
                    <p style={{fontSize: '120%', marginBottom: 30}}>
                        To create your account please provide your chosen email address and password.
                    </p>
                    <RegisterForm />
                </Content>
            </Layout>
        </Content>
        <Footer style={{ textAlign: 'center' }}>
            uhc.gg ©2016 Created by Eluinhost
        </Footer>
    </Layout>,
    document.getElementById('react-app-holder')
);