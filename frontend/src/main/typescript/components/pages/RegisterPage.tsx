import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { RegisterForm, RegisterFormProps } from '../RegisterForm';
import * as jwtDecode from 'jwt-decode';
import {NonIdealState} from "@blueprintjs/core";

const FinalStepPage: React.SFC<RegisterFormProps> = props =>
    <div className="register-page-final">
        <h1>Final Step!</h1>
        <p style={{fontSize: '120%', marginBottom: 30}}>
            To create your account please provide your chosen email address and password.
        </p>
        <RegisterForm { ...props }/>
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

type RegisterStartJwtToken = { username: string }
type RegisterErrorJwtToken = { header: string, message: string }
type RegisterJwtToken = RegisterStartJwtToken | RegisterErrorJwtToken

function isRegisterErrorJwtToken(token: any): token is RegisterErrorJwtToken {
    return !!token.header;
}

export const RegisterPage: React.SFC<RouteComponentProps<void>> = ({ location: { hash }}) => {
    // If no has provided then there is no token to parse, show the start screen
    if (!hash || hash.length < 2)
        return <FirstStepPage />;

    const token = hash.substring(1);

    try {
        const claims = jwtDecode<RegisterJwtToken>(token);

        if (isRegisterErrorJwtToken(claims)) {
            return <NonIdealState
                title={claims.header || 'Unknown error'}
                description={claims.message}
                visual="warning-sign"
                action={<p>
                    You may try again by <a href="/api/register">clicking here</a>
                </p>}
            />;
        }

        // TODO check expires and fire stuff when it expires

        return <FinalStepPage username={claims.username} token={token}/>
    } catch (ignored) {
        return <NonIdealState
            title="Invalid Token"
            description="There was an error parsing your token, if this continues please contact an administrator"
            visual="warning-sign"
            action={<p>
                You may try again by <a href="/api/register">clicking here</a>
            </p>}
        />;
    }
};
