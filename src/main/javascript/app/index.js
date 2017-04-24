import 'antd/dist/antd.css'
import '../../css/styles.css'

import ReactDOM from 'react-dom';
import { pure } from 'recompose';
import { BrowserRouter as Router, Route, Link } from 'react-router-dom';
import { Layout, Menu, Breadcrumb } from 'antd';
const { Content, Footer, Sider } = Layout;

import { RegisterForm } from '../register/RegisterForm';

const Register = pure(() => <div>
    <h1>Final Step!</h1>
    <p style={{fontSize: '120%', marginBottom: 30}}>
        To create your account please provide your chosen email address and password.
    </p>
    <RegisterForm />
</div>);

const Home = pure(() => <h1>HOME</h1>);

const App = pure(() => <Router>
    <Layout style={{minHeight: '100%'}}>
        <Content style={{ padding: '0 50px' }}>
            <Breadcrumb style={{ margin: '12px 0' }}>
                <Breadcrumb.Item>
                    <Link to="/">Home</Link>
                </Breadcrumb.Item>
            </Breadcrumb>
            <Layout style={{ padding: '24px 0', background: '#fff' }}>
                <Sider width={200} style={{ background: '#fff' }}>
                    <Menu
                        mode="inline"
                        defaultSelectedKeys={['1']}
                        defaultOpenKeys={['sub1']}
                        style={{ height: '100%' }}
                    >
                        <Menu.Item key="1"><Link to="/">Home</Link></Menu.Item>
                        <Menu.Item key="2"><Link to="/register">Register</Link></Menu.Item>
                    </Menu>
                </Sider>
                <Content style={{ padding: '0 24px', minHeight: 280, textAlign: 'center' }}>

                    <Route exact path="/" component={Home} />
                    <Route path="/register" component={Register} />
                </Content>
            </Layout>
        </Content>
        <Footer style={{ textAlign: 'center' }}>
            uhc.gg ©2016 Created by Eluinhost
        </Footer>
    </Layout>
</Router>);

ReactDOM.render(<App />, document.getElementById('react-app-holder'));