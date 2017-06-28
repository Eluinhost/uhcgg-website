import * as React from "react";
import {NonIdealState} from "@blueprintjs/core";
import {START_REGISTER_URL} from "./constants";
import {RegisterErrorJwtToken} from "./JwtTokens";

export const retryAction = <p>
    You may try again by <a href={START_REGISTER_URL}>clicking here</a>
</p>;

export const BackendError: React.SFC<{ token: RegisterErrorJwtToken }> = ({ token }) =>
    <NonIdealState
        title={token.header}
        description={token.message}
        visual="warning-sign"
        action={retryAction}
    />;