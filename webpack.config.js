const path = require('path');

module.exports = {
    mode: 'development',
    entry: './src/main/tsx/index.tsx',
    output: {
        path: path.resolve(__dirname, 'src/main/resources/static'),
        filename: 'bundle.js'
    },
    resolve: {
        extensions: ['.tsx', '.ts', '.js']
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: 'ts-loader',
                exclude: /node_modules/
            },
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env', '@babel/preset-react']
                    }
                }
            }
        ]
    },
    devServer: {
        static: {
            directory: path.join(__dirname, 'src/main/resources/static'),
        },
        compress: true,
        port: 3000,
        proxy: [
            {
                context: ['/api'],
                target: 'http://localhost:8080',
                secure: false
            }
        ],
        hot: true
    }
};
