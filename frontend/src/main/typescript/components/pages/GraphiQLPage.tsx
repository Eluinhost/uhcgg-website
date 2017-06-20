import * as React from 'react';
import * as GraphiQL from 'graphiql';
import * as fetch from 'isomorphic-fetch';
import { parse, print } from 'graphql';
import {Redirect, RouteComponentProps} from 'react-router';

export class GraphiQLPage extends React.Component<RouteComponentProps<void>, { redirect: boolean }>{
    constructor(props: RouteComponentProps<void>) {
        super(props);

        this.state = {
            redirect: false
        };
    }

    graphql: any;

    static fetchData(params: any): Promise<any> {
        return fetch(
            window.location.origin + '/api/graphql', {
                method: 'post',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(params)
            }
        ).then(_ => _.json())
    }

    prettify = () => {
        const editor = this.graphql.getQueryEditor();
        editor.setValue(print(parse(editor.getValue())));
    };

    toggleHistory = () => {
        this.graphql.handleToggleHistory();
    };

    showSchema = () => {
        this.setState({
            redirect: true
        });
    };

    render() {
        if (this.state.redirect) {
            return <Redirect to="/dev/schema"/>
        } else {
            return <GraphiQL fetcher={GraphiQLPage.fetchData} editorTheme="monokai" ref={c => this.graphql = c}>
                <GraphiQL.Toolbar>
                    <GraphiQL.Button
                        onClick={this.showSchema}
                        title="Show full graphical schema diagram"
                        label="Schema"
                    />
                    <GraphiQL.Button
                        onClick={this.prettify}
                        title="Prettify"
                        label="Prettify"
                    />
                    <GraphiQL.Button
                        onClick={this.toggleHistory}
                        title="Show/Hide history panel"
                        label="History"
                    />
                </GraphiQL.Toolbar>
            </GraphiQL>
        }
    }
}