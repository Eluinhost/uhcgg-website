const path = require('path');
const { HotModuleReplacementPlugin } = require('webpack');
const { CheckerPlugin } = require('awesome-typescript-loader');
const { optimize } = require('webpack');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    entry: path.join(__dirname, 'src', 'main', 'typescript', 'index.tsx'),
    output: {
        filename: '[name].bundle.js',
        path: __dirname + '/dist'
    },
    devtool: 'eval',
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
                loader: ['react-hot-loader/webpack', 'awesome-typescript-loader']
            },
            {
                test: /\.(js|tsx?)$/,
                loader: 'source-map-loader',
                enforce: 'pre',
                exclude: /node_modules/
            },
            {
                test: /\.css$/,
                use: ExtractTextPlugin.extract({
                    publicPath: '/dist',
                    fallback: 'style-loader',
                    use: 'css-loader'
                })
            },
            {
                test: /\.png$/,
                loader: 'file-loader'
            }
        ]
    },
    plugins: [
        new CheckerPlugin(),
        new optimize.CommonsChunkPlugin({
            name: 'vendor',
            minChunks: module => module.context && module.context.indexOf('node_modules') !== -1
        }),
        new ExtractTextPlugin({
            filename: '[name].bundle.css',
            allChunks: true
        }),
        new HotModuleReplacementPlugin(),
        new HtmlWebpackPlugin({
            hash: true
        })
    ],
    devServer: {
        contentBase: path.join(__dirname, 'dist'),
        compress: true,
        port: 9000,
        hot: true,
        proxy: {
            '/api': 'http://localhost:10000'
        },
        historyApiFallback: true
    }

};