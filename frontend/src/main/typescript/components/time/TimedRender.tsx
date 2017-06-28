import * as React from "react";

export type TimedRenderProps = {
    interval: number,
    render: () => React.ReactElement<any>
};

export type TimedRenderState = {
    value: string
};

export class TimedRender extends React.Component<TimedRenderProps, TimedRenderState> {
    state = {
        value: ''
    };

    timerId: number | null = null;

    tick = () => this.setState((prevState, props) => ({
        value: props.render()
    }));

    componentDidMount() {
        if (this.timerId === null) {
            this.timerId = window.setInterval(this.tick, this.props.interval);
        }
        this.tick();
    }

    componentWillUnmount() {
        if (this.timerId !== null) {
            clearInterval(this.timerId);
            this.timerId = null;
        }
    }

    render() {
        return <span className="moment moment-time-until">{ this.state.value }</span>;
    }
}