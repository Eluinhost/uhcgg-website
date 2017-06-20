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