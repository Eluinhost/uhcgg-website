declare module '*.graphql' {
    import { DocumentNode } from 'graphql';

    const value: DocumentNode;
    export = value;
}

declare module '*.css' {
}

interface Window {
    __REDUX_DEVTOOLS_EXTENSION_COMPOSE__?: (a: object) => Function
}

declare module 'graphiql' {
    interface GraphQLSchema {}

    interface GraphQLStorage {
        getItem: Function,
        setItem: Function,
        removeItem: Function
    }

    interface GraphiQLProps {
        fetcher(any): Promise<any>,
        schema?: GraphQLSchema,
        query?: string,
        variables?: string,
        operationName?: string,
        response?: string,
        storage?: GraphQLStorage,
        defaultQuery?: string,
        onEditQuery?: Function,
        onEditVariables?: Function,
        onEditOperationName?: Function,
        onToggleDocs?: Function,
        getDefaultFieldNames?: Function,
        editorTheme?: string,
        onToggleHistory?: string
    }

    interface GraphiQLButtonProps {
        onClick(): any,
        label: string,
        title: string
    }

    interface GraphiQL extends React.ComponentClass<GraphiQLProps> {
        Button: React.ComponentClass<GraphiQLButtonProps>
        Toolbar: React.ComponentClass<{}>
    }

    const value: GraphiQL;
    export = value;
}

declare module 'graphql-voyager' {
    interface VoyagerProps {
        introspection(string): Promise<any>,
        displayOptions?: {
            rootType?: string;
            skipRelay?: boolean;
            sortByAlphabet?: boolean;
        }
    }

    interface GraphQLVoyager {
        Voyager: React.SFC<VoyagerProps>
    }

    const value: GraphQLVoyager;
    export = value;
}

declare module 'snuownd' {
    interface SnuOwndParser {
        render(markdown: string): string
    }
    interface SnuOwnd {
        getParser(): SnuOwndParser
    }

    const value: SnuOwnd;
    export = value;
}

declare module 'react-transition-group' {
    interface HTMLTransitionGroupProps<T> extends React.HTMLAttributes<T> {
        component?: React.ReactType;
        childFactory?(child: React.ReactElement<any>): React.ReactElement<any>;
    }

    interface TransitionGroupChildLifecycle {
        componentWillAppear?(callback: () => void): void;
        componentDidAppear?(): void;
        componentWillEnter?(callback: () => void): void;
        componentDidEnter?(): void;
        componentWillLeave?(callback: () => void): void;
        componentDidLeave?(): void;
    }

    type TransitionGroupProps = HTMLTransitionGroupProps<TransitionGroup>;

    interface CSSTransitionGroupTransitionName {
        enter: string;
        enterActive?: string;
        leave: string;
        leaveActive?: string;
        appear?: string;
        appearActive?: string;
    }

    interface CSSTransitionGroupProps extends HTMLTransitionGroupProps<CSSTransitionGroup> {
        transitionName: string | CSSTransitionGroupTransitionName;
        transitionAppear?: boolean;
        transitionAppearTimeout?: number;
        transitionEnter?: boolean;
        transitionEnterTimeout?: number;
        transitionLeave?: boolean;
        transitionLeaveTimeout?: number;
    }

    export const CSSTransitionGroup: React.SFC<CSSTransitionGroupProps>;
    export const TransitionGroup: React.SFC<TransitionGroupProps>;
}