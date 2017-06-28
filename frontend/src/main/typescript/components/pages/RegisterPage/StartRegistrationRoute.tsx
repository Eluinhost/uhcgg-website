import {START_REGISTER_URL} from "./constants";
import * as React from "react";

export const StartRegistrationRoute: React.SFC = () =>
    <div style={{ textAlign: 'center', marginTop: 30}}>
        <h1>Register an account</h1>

        <p>Something about goign to reddit erhe is a button</p>

        <a href={START_REGISTER_URL}>
            <button className="pt-button pt-icon-database" style={{ marginTop: 20 }}>
                Register
            </button>
        </a>
    </div>;