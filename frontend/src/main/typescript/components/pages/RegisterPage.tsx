import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { RegisterForm } from '../RegisterForm';
import { Button } from 'antd';
import * as jwtDecode from 'jwt-decode';

const FinalStepPage: React.SFC<{ username: string }> = ({ username }) =>
    <div>
        <h1>Final Step!</h1>
        <p style={{fontSize: '120%', marginBottom: 30}}>
            To create your account please provide your chosen email address and password.
        </p>
        <RegisterForm username={username} />
    </div>;

const RegisterErrorPage: React.SFC<{ message: string, header: string }> = ({ message, header }) =>
    <div>
        <h1>{ header }</h1>

        <p>
            { message }
        </p>

        <p>
            You may try again by <a href="/api/register">clicking here</a>
        </p>
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
    if (!hash)
        return <FirstStepPage />;

    try {
        const claims = jwtDecode(hash);

        if (claims.username) {
            // TODO check expires and fire stuff when it expires

            return <FinalStepPage username={claims.username}/>
        } else if (claims.header && claims.message) {
            return <RegisterErrorPage
                header={claims.header}
                message={claims.message}
            />
        }

    } catch (err) {
        return <RegisterErrorPage
            header="Invalid Token"
            message="There was an error parsing your token, if this continues please contact an administrator"
        />;
    }


    try {
        const username = decodeURIComponent(hash.substring(1));

        return <FinalStepPage username={username} />
    } catch (ignored) {
        return <FirstStepPage />
    }
};
