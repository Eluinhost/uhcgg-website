const path = require('path');
const { HotModuleReplacementPlugin } = require('webpack');
const { CheckerPlugin } = require('awesome-typescript-loader');
const { optimize, NamedModulesPlugin } = require('webpack');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const extractCss = new ExtractTextPlugin({
    filename: "[name].[contenthash].css",
    disable: process.env.NODE_ENV !== 'production'
});

module.exports = {
    entry: [path.join(__dirname, 'src', 'main', 'typescript', 'index.tsx'), path.join(__dirname, 'src', 'main', 'sass', 'main.sass')],
    output: {
        filename: '[name].bundle.js',
        path: __dirname + '/dist',
        publicPath: '/'
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
                exclude: /node_modules/,
                loader: ['react-hot-loader/webpack', 'awesome-typescript-loader']
            },
            {
                test: /\.(js|tsx?)$/,
                loader: 'source-map-loader',
                enforce: 'pre',
                exclude: /node_modules/
            },
            {
                test: /\.sass$/,
                use: extractCss.extract({
                    publicPath: '/dist',
                    fallback: 'style-loader',
                    use: ['css-loader', 'sass-loader']
                })
            },
            {
                test: /\.css$/,
                use: extractCss.extract({
                    publicPath: '/dist',
                    fallback: 'style-loader',
                    use: ['css-loader']
                })
            },
            {
                test: /\.(eot|svg|ttf|woff|woff2|png)$/,
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
        extractCss,
        new HotModuleReplacementPlugin(),
        new HtmlWebpackPlugin({
            hash: true
        }),
        new NamedModulesPlugin()
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