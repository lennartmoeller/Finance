const path = require("path");
const CopyWebpackPlugin = require("copy-webpack-plugin");

module.exports = (env, argv) => {
    const isProd = argv.mode === "production";

    return {
        mode: argv.mode ?? "production",
        entry: "./src/index.tsx",
        output: {
            path: path.resolve(__dirname, "dist"),
            filename: "bundle.js",
        },
        resolve: {
            extensions: [".tsx", ".ts", ".js"],
            alias: {
                "@": path.resolve(__dirname, "src"),
            },
        },
        module: {
            rules: [
                {
                    test: /\.tsx?$/,
                    use: "ts-loader",
                    exclude: /node_modules/,
                },
                {
                    test: /\.js$/,
                    exclude: /node_modules/,
                    use: {
                        loader: "babel-loader",
                        options: {
                            presets: [
                                "@babel/preset-env",
                                "@babel/preset-react",
                            ],
                            plugins: ["babel-plugin-styled-components"],
                        },
                    },
                },
                {
                    test: /\.css$/,
                    use: ["style-loader", "css-loader"],
                    exclude: /node_modules/,
                },
            ],
        },
        plugins: [
            new CopyWebpackPlugin({
                patterns: [{ from: "assets", to: "" }],
            }),
        ],
        devtool: isProd ? "source-map" : "eval-source-map",
        optimization: {
            minimize: isProd,
        },
        devServer: isProd
            ? undefined
            : {
                  historyApiFallback: true,
                  static: {
                      directory: path.join(__dirname, "dist"),
                  },
                  compress: true,
                  port: 80,
                  proxy: [
                      {
                          context: ["/api"],
                          target: "http://localhost:8080",
                          secure: false,
                      },
                  ],
                  hot: true,
              },
    };
};
