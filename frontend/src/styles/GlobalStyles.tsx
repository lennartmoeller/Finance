import {createGlobalStyle} from 'styled-components';

const GlobalStyles = createGlobalStyle`
    body {
        margin: 0;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        font-size: ${(props) => props.theme.fontSize}px;
    }

    * {
        box-sizing: border-box;
    }
`;

export default GlobalStyles;
