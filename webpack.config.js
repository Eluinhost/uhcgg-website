const path = require('path');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const webpack = require('webpack');
const { map } = require('lodash');
const fs = require('fs');

const outputBase = path.resolve(__dirname, 'src/main/resources/build');
const appBase = path.resolve(__dirname, 'src', 'main', 'javascript');

const entry = fs
    .readdirSync(appBase)
    .filter(_ => fs.statSync(path.resolve(appBase, _)).isDirectory()) // find only directories
    .reduce((acc, entry) => {
        acc[entry] = path.resolve(appBase, entry, 'index.js');
        return acc;
    }, {}); // combine into entry object

module.exports = function(env) {
    const config = {
        entry,
        output: {
            filename: '[name].bundle.js',
            path: outputBase,
            sourceMapFilename: '[file].map',
            publicPath: '/resources'
        },
        devtool: '#inline-source-map',
        target: 'web',
        module: {
            rules: [
                {
                    test: /\.js$/,
                    loader: 'babel-loader',
                    options: {
                        'presets': ['react', ['es2015', {modules: false}]],
                        'plugins': ['transform-runtime', 'transform-class-properties', 'transform-object-rest-spread'],
                        'cacheDirectory': true
                    },
                    exclude: /node_modules/
                },
                {
                    test: /\.(png|jpe?g|gif|svg)(\?\S*)?$/,
                    loader: 'file-loader'
                },
                {
                    test: /\.(woff|woff2|ttf|eot)(\?\S*)?$/,
                    loader: 'url-loader'
                },
                {
                    test: /\.css$/,
                    use: ExtractTextPlugin.extract({
                        fallback: 'style-loader',
                        use: ['css-loader?importLoaders=1']
                    })
                }
            ]
        },
        resolve: {
            extensions: ['.js', '.json', '.jsx']
        },
        plugins: [
            new ExtractTextPlugin({
                filename: 'app.bundle.css',
                disable: false,
                allChunks: true
            }),
            new webpack.ProvidePlugin({
                Promise: 'es6-promise',
                fetch: 'isomorphic-fetch',
                React: 'react'
            }),
            new webpack.optimize.CommonsChunkPlugin({
                name: 'vendor',
                minChunks: function (module) {
                    return module.context && module.context.indexOf('node_modules') !== -1;
                }
            })
        ]
    };

    if (env.production) {
        config.plugins.push(
            new webpack.optimize.UglifyJsPlugin({
                minimize: true,
                sourceMap: true,
                compress: {
                    warnings: false,
                    drop_console: false
                }
            }),
            new webpack.DefinePlugin({
                'process.env.NODE_ENV': '"production"'
            })
        );

        config.devtool = 'source-map';
    }

    return config;
};