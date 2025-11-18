module.exports = {
    testEnvironment: "jsdom",
    roots: ["<rootDir>/tests"],
    moduleFileExtensions: ["ts", "tsx", "js", "jsx"],
    transform: {
        "^.+\\.(t|j)sx?$": ["babel-jest", { presets: ["@babel/preset-env", "@babel/preset-react", "@babel/preset-typescript"] }],
    },
    moduleNameMapper: {
        "^@/(.*)$": "<rootDir>/src/$1",
        "\\.(css|less|scss)$": "identity-obj-proxy",
    },
};
