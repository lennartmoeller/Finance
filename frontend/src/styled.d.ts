import "styled-components";

interface Border {
    color: string;
    radius: number;
    width: number;
    widthFocus: number;
}

interface Colors {
    accent: string;
    accentBackground: string;
}

declare module "styled-components" {
    export interface DefaultTheme {
        border: Border;
        colors: Colors;
        fontSize: number;
        mainPadding: number;
        header: {
            height: number;
        };
        inputField: {
            label: {
                fontWeight: number;
                letterSpacing: string;
            };
        };
        table: Record<
            "header1" | "header2" | "filter" | "body",
            {
                backgroundColor: string;
                fontWeight?: string;
            }
        >;
    }
}
