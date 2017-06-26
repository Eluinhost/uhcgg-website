import * as Snuownd from 'snuownd';
import * as React from 'react';

const parser = Snuownd.getParser();

export interface MarkdownProps {
    markdown: string,
    className?: string
}

export const Markdown: React.SFC<MarkdownProps> = ({ markdown, className }) =>
    <div
        className={`markdown ${className || ''}`}
        dangerouslySetInnerHTML={{ __html: parser.render(markdown)}}
    />;

