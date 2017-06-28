import {Redirect, RedirectProps, RouteComponentProps} from "react-router";
import {isRegisterErrorJwtToken, RegisterJwtToken} from "./JwtTokens";
import {BackendError} from "./BackendError";
import * as React from "react";
import * as jwtDecode from "jwt-decode";
import moment = require("moment");
import {RegisterForm} from "./RegisterForm";
import {SecondsUntil} from "../../time/SecondsUntil";

export type CompleteRegistrationParams = {
    header: string,
    body: string,
    signature: string
}

class TimeoutRedirect extends React.Component<{ timeout: number } & RedirectProps, { trigger: boolean }> {
    state = {
        trigger: false
    };

    timerId: number | null;

    componentDidMount() {
        this.timerId = window.setTimeout(
            () => this.setState({ trigger: true }),
            this.props.timeout * 1000
        );
    }

    componentWillUnmount() {
        if (this.timerId != null) {
            window.clearTimeout(this.timerId);
            this.timerId = null;
        }
    }

    render() {
        if (this.state.trigger) {
            return <Redirect {...this.props}/>
        }

        return null;
    }
}

export const CompleteRegistrationRoute: React.SFC<RouteComponentProps<CompleteRegistrationParams>> =
    ({ match: {params : {header, body, signature}}}) => {
        // recombine token from URL
        const rawToken = `${header}.${body}.${signature}`;

        // Attempt to parse a token from the URL
        // If we can't parse the token redirect to main register page
        let token: RegisterJwtToken;
        try {
            token = jwtDecode<RegisterJwtToken>(rawToken);
        } catch (err) {
            return <Redirect to="/register"/>
        }

        // If the backend forward some kind of error display it
        if (isRegisterErrorJwtToken(token)) {
            return <BackendError token={token}/>
        }

        // Fake 30 less seconds to compensate for network and/or timing issues
        const effectiveExpires = moment(token.exp, 'X').subtract(30, 'seconds');

        const diff = effectiveExpires.diff(moment(), 'seconds');

        // Check the token has at least 60 seconds remaining to complete the form
        if (diff < 60) {
            return <Redirect to="/register/timeout" />;
        }

        // Show the registration form
        return <div className="register-page-final">
            <h1>Final Step!</h1>
            <p style={{fontSize: '120%', marginBottom: 30}}>
                To create your account please provide your chosen email address and password.
            </p>
            <div className="pt-form-helper-text pt-ui-text register-form-expires">
                <TimeoutRedirect timeout={diff} to="/register/timeout"/>
                This form expires in <SecondsUntil time={effectiveExpires} interval={1000} /> seconds
            </div>
            <RegisterForm username={token.username} token={rawToken} />
        </div>;
    };