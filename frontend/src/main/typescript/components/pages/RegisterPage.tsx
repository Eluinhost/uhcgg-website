import * as React from 'react';
import { RouteComponentProps } from 'react-router';
import { RegisterForm } from '../RegisterForm';

export const RegisterPage: React.SFC<RouteComponentProps<void>> = ({ location: { hash }}) =>
    <div>
        <h1>Final Step!</h1>
        <p style={{fontSize: '120%', marginBottom: 30}}>
            To create your account please provide your chosen email address and password.
        </p>
        <RegisterForm username={decodeURIComponent(hash.substring(1)) /* TODO redireect if none in hash? */ } />
    </div>;