import * as React from 'react';
import { Moment } from 'moment';
import moment = require("moment");
import {TimedRender} from "./TimedRender";

export type SecondsUntilProps = {
    interval: number,
    time: Moment
}

export const SecondsUntil: React.SFC<SecondsUntilProps> = ({ time, interval }) =>
    <TimedRender
        interval={interval}
        render={() => <span>{ time.diff(moment(), 'seconds') }</span>}
    />;