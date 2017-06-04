import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { RegisterForm } from '../RegisterForm';
import { Button } from 'antd';

const FinalStepPage: React.SFC<{ username: string }> = ({ username }) =>
    <div>
        <h1>Final Step!</h1>
        <p style={{fontSize: '120%', marginBottom: 30}}>
            To create your account please provide your chosen email address and password.
        </p>
        <RegisterForm username={username} />
    </div>;

const FirstStepPage: React.SFC<{}> = () =>
    <div>
        <h1>Register an account</h1>

        <p>Something about goign to reddit erhe is a button</p>

        <Button
            type="primary"
            htmlType="submit"
            size="large"
            onClick={() => window.location.href = '/api/register'}
            style={{ marginTop: 20 }}
        >
            Register
        </Button>
    </div>;

export const RegisterPage: React.SFC<RouteComponentProps<void>> = ({ location: { hash }}) => {
    if (!hash || hash.length < 2)
        return <FirstStepPage />;

    try {
        const username = decodeURIComponent(hash.substring(1));

        return <FinalStepPage username={username} />
    } catch (ignored) {
        return <FirstStepPage />
    }
};
