import js from '@eslint/js';
import tsParser from '@typescript-eslint/parser';
import tsPlugin from '@typescript-eslint/eslint-plugin';
import reactPlugin from 'eslint-plugin-react';
import reactHooksPlugin from 'eslint-plugin-react-hooks';
import importPlugin from 'eslint-plugin-import';

export default [
  {
    ignores: ['webpack.config.js'],
  },
  js.configs.recommended,
  reactPlugin.configs.recommended,
  reactHooksPlugin.configs.recommended,
  tsPlugin.configs.recommended,
  importPlugin.configs.errors,
  importPlugin.configs.warnings,
  {
    languageOptions: {
      parser: tsParser,
      ecmaVersion: 12,
      sourceType: 'module',
      globals: {
        ...js.environments.browser.globals,
      },
    },
    settings: {
      'import/resolver': {
        alias: {
          map: [['@', './src/main/tsx']],
          extensions: ['.ts', '.tsx', '.js', '.jsx', '.json'],
        },
        node: {
          extensions: ['.js', '.jsx', '.ts', '.tsx'],
        },
      },
    },
    plugins: {
      react: reactPlugin,
      'react-hooks': reactHooksPlugin,
      '@typescript-eslint': tsPlugin,
      import: importPlugin,
    },
    rules: {
      'sort-imports': ['error', { ignoreCase: true, ignoreDeclarationSort: true }],
      'import/order': [
        'error',
        {
          groups: [
            ['builtin', 'external'],
            ['internal', 'sibling', 'parent', 'index'],
          ],
          pathGroups: [
            { pattern: 'react', group: 'external', position: 'before' },
            { pattern: '@src/**', group: 'internal' },
          ],
          pathGroupsExcludedImportTypes: ['react'],
          newlines-between: 'always',
          alphabetize: { order: 'asc', caseInsensitive: true },
        },
      ],
      '@typescript-eslint/ban-ts-comment': ['error', { 'ts-ignore': 'allow-with-description' }],
      semi: ['error', 'always'],
    },
  },
];
