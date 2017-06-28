import * as React from 'react';
import {Route, Switch} from 'react-router';
import {StartRegistrationRoute} from "./StartRegistrationRoute";
import {CompleteRegistrationRoute} from "./CompleteRegistrationRoute";
import {ExpiredTokenRoute} from "./ExpiredTokenRoute";

export const RegisterPage: React.SFC = () =>
    <Switch>
        <Route path="/register/:header/:body/:signature" component={CompleteRegistrationRoute} />
        <Route path="/register/timeout" component={ExpiredTokenRoute} />
        <Route path="/register" component={StartRegistrationRoute} />
    </Switch>;