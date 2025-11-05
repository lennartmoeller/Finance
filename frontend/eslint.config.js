import { FlatCompat } from "@eslint/eslintrc";
import js from "@eslint/js";
import tsPlugin from "@typescript-eslint/eslint-plugin";
import tsParser from "@typescript-eslint/parser";
import importPlugin from "eslint-plugin-import";
import reactPlugin from "eslint-plugin-react";
import reactHooksPlugin from "eslint-plugin-react-hooks";
import globals from "globals";
import path from "node:path";
import { fileURLToPath } from "node:url";

const compat = new FlatCompat({
    baseDirectory: path.dirname(fileURLToPath(import.meta.url)),
    recommendedConfig: js.configs.recommended,
});

export default [
    ...compat.extends(
        "eslint:recommended",
        "plugin:react/recommended",
        "plugin:react-hooks/recommended",
        "plugin:@typescript-eslint/recommended",
        "plugin:import/errors",
        "plugin:import/warnings",
    ),
    {
        ignores: ["**/*", "!src", "!src/**/*"],
    },
    {
        languageOptions: {
            parser: tsParser,
            ecmaVersion: "latest",
            sourceType: "module",
            globals: {
                ...globals.browser,
                ...globals.es2021,
            },
            parserOptions: { ecmaFeatures: { jsx: true } },
        },
        settings: {
            react: { version: "detect" },
            "import/resolver": {
                alias: {
                    map: [["@", "./src"]],
                    extensions: [".ts", ".tsx", ".js", ".jsx", ".json"],
                },
                node: { extensions: [".js", ".jsx", ".ts", ".tsx"] },
                typescript: {},
            },
        },
        plugins: {
            react: reactPlugin,
            "react-hooks": reactHooksPlugin,
            "@typescript-eslint": tsPlugin,
            import: importPlugin,
        },
        rules: {
            "sort-imports": ["error", { ignoreCase: true, ignoreDeclarationSort: true }],
            "import/order": [
                "error",
                {
                    groups: [
                        ["builtin", "external"],
                        ["internal", "sibling", "parent", "index"],
                    ],
                    pathGroups: [
                        {
                            pattern: "react",
                            group: "external",
                            position: "before",
                        },
                        { pattern: "@/**", group: "internal" },
                    ],
                    pathGroupsExcludedImportTypes: ["react"],
                    "newlines-between": "always",
                    alphabetize: { order: "asc", caseInsensitive: true },
                },
            ],
            "@typescript-eslint/ban-ts-comment": ["error", { "ts-ignore": "allow-with-description" }],
            "react/prop-types": "off",
            semi: ["error", "always"],
            quotes: ["error", "double"],
        },
    },
];
