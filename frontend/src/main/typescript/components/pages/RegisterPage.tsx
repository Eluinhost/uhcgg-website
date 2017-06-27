import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { RegisterForm, RegisterFormProps } from '../RegisterForm';
import * as jwtDecode from 'jwt-decode';

const FinalStepPage: React.SFC<RegisterFormProps> = props =>
    <div>
        <h1>Final Step!</h1>
        <p style={{fontSize: '120%', marginBottom: 30}}>
            To create your account please provide your chosen email address and password.
        </p>
        <RegisterForm { ...props }/>
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
    <div style={{ textAlign: 'center', marginTop: 30}}>
        <h1>Register an account</h1>

        <p>Something about goign to reddit erhe is a button</p>

        <button
            className="pt-button pt-icon-database"
            onClick={() => window.location.href = '/api/register'}
            style={{ marginTop: 20 }}
        >Register</button>
    </div>;

export const RegisterPage: React.SFC<RouteComponentProps<void>> = ({ location: { hash }}) => {
    if (!hash || hash.length < 2)
        return <FirstStepPage />;

    const token = hash.substring(1);

    try {
        const claims = jwtDecode(token);

        if (claims.username) {
            // TODO check expires and fire stuff when it expires

            return <FinalStepPage username={claims.username} token={token}/>
        } else if (claims.header && claims.message) {
            return <RegisterErrorPage
                header={claims.header}
                message={claims.message}
            />
        }
    } catch (ignored) {}

    return <RegisterErrorPage
        header="Invalid Token"
        message="There was an error parsing your token, if this continues please contact an administrator"
    />;
};
