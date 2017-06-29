import * as React from "react";
import { any } from "ramda";

export type InfinityScrollProps = {
    trigger(): Promise<any>
    hasMore: boolean,
    loadingComponent?: React.Component | React.ReactElement<any>,
    noMoreItemsComponent?: React.Component | React.ReactElement<any>
};

export type InfinityScrollState = {
    blocked: boolean
};

export class InfinityScroll extends React.Component<InfinityScrollProps, InfinityScrollState> {
    state = {
        blocked: false
    };

    _base: HTMLDivElement;
    _scroller: Element;

    block = () => this.setState({ blocked: true });
    unblock = () => this.setState({ blocked: false });

    onScroll = (event: UIEvent) => {
        if (this.state.blocked || !this.props.hasMore)
            return;

        const target = event.target as Element;
        const scrolled = .8 * (target.scrollHeight - target.scrollTop);

        if (scrolled <= target.clientHeight) {
            this.block();

            this.props.trigger()
                .then(this.unblock)
                .catch(this.unblock);
        }
    };

    findScroller(element: Element | null): Element {
        if (element === null) {
            return document.body;
        }

        const style: CSSStyleDeclaration = getComputedStyle(element);

        if (any(_ => /(auto|scroll)/.test(_!), [style.overflowY, style.overflow])) {
            return element;
        }

        return this.findScroller(element.parentElement);
    }

    componentDidMount() {
        this._scroller = this.findScroller(this._base);
        this._scroller.addEventListener('scroll', this.onScroll);
    }

    componentWillUnmount() {
        this._scroller.removeEventListener('scroll', this.onScroll);
    }

    render() {
        return <div ref={d => this._base = d!}>
            { this.props.children }
            { this.props.hasMore ? this.props.loadingComponent : this.props.noMoreItemsComponent }
        </div>;
    }
}