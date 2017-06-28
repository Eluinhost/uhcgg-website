import {NonIdealState} from "@blueprintjs/core";
import {retryAction} from "./BackendError";
import * as React from "react";

export const ExpiredTokenRoute: React.SFC = () =>
    <NonIdealState
        title="Expired token"
        description="The authentication token has expired"
        visual="warning-sign"
        action={retryAction}
    />;