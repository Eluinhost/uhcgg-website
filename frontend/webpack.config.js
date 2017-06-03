const { CheckerPlugin } = require('awesome-typescript-loader');
const { optimize } = require('webpack');

module.exports = {
    entry: './src/main/typescript/index.tsx',
    output: {
        filename: '[name].bundle.js',
        path: __dirname + '/dist'
    },
    devtool: 'source-map',
    resolve: {
        extensions: ['.ts', '.tsx', '.js', '.json', '.graphql']
    },
    module: {
        rules: [
            {
                test: /\.graphql$/,
                loader: 'graphql-tag/loader',
                exclude: /node_modules/
            },
            {
                test: /\.tsx?$/,
                loader: 'awesome-typescript-loader'
            },
            {
                test: /\.(js|tsx?)$/,
                loader: 'source-map-loader',
                enforce: 'pre',
                exclude: /node_modules/
            }
        ]
    },
    plugins: [
        new CheckerPlugin(),
        new optimize.CommonsChunkPlugin({
            name: 'vendor',
            minChunks: module => module.context && module.context.indexOf('node_modules') !== -1
        })
    ]

};