const path = require("path");
const CopyWebpackPlugin = require("copy-webpack-plugin");
const HtmlWebpackPlugin = require("html-webpack-plugin");

module.exports = (env, argv) => {
    const isProd = argv.mode === "production";

    return {
        mode: argv.mode ?? "production",
        entry: "./src/index.tsx",
        output: {
            path: path.resolve(__dirname, "dist"),
            filename: isProd ? "[name].[contenthash].js" : "[name].js",
            chunkFilename: isProd ? "[name].[contenthash].chunk.js" : "[name].chunk.js",
            clean: true,
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
                            presets: ["@babel/preset-env", "@babel/preset-react"],
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
            new HtmlWebpackPlugin({
                template: "assets/index.html",
                filename: "index.html",
                inject: "body",
                scriptLoading: "defer",
            }),
            new CopyWebpackPlugin({
                patterns: [
                    {
                        from: "assets",
                        to: "",
                        globOptions: { ignore: ["**/index.html"] },
                    },
                ],
            }),
        ],
        devtool: isProd ? "source-map" : "eval-source-map",
        optimization: {
            minimize: isProd,
            splitChunks: {
                chunks: "all",
                cacheGroups: {
                    default: false,
                    defaultVendors: false,
                    // Vendor chunk for node_modules
                    vendor: {
                        test: /[\\/]node_modules[\\/]/,
                        name: "vendors",
                        chunks: "initial",
                        priority: 20,
                        enforce: true,
                    },
                    // React and React-DOM in separate chunk
                    react: {
                        test: /[\\/]node_modules[\\/](react|react-dom)[\\/]/,
                        name: "react",
                        chunks: "initial",
                        priority: 30,
                        enforce: true,
                    },
                    // Chart.js in separate chunk due to its size
                    charts: {
                        test: /[\\/]node_modules[\\/](chart\.js|react-chartjs-2)[\\/]/,
                        name: "charts",
                        chunks: "initial",
                        priority: 25,
                        enforce: true,
                    },
                },
            },
            // Better runtime chunk for long-term caching
            runtimeChunk: {
                name: "runtime",
            },
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
