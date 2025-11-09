const general = {
    border: {
        color: "#e3e3e3",
        radius: 12,
        width: 1,
        widthFocus: 1.5,
    },
    colors: {
        accent: "#3a53c0",
        accentBackground: "#3a53c00d",
    },
};

const theme = {
    ...general,

    fontSize: 15,
    mainPadding: 30,
    header: {
        height: 120,
    },
    inputField: {
        label: {
            fontWeight: 600,
            letterSpacing: "-.2px",
        },
    },
    table: {
        header1: {
            backgroundColor: "#f4f4f4",
            fontWeight: "bold",
        },
        header2: {
            backgroundColor: "#fafafa",
            fontWeight: "bold",
        },
        filter: {
            backgroundColor: "#f8f8f8",
            fontWeight: "normal",
        },
        body: {
            backgroundColor: "white",
            fontWeight: "normal",
        },
    },
};

export default theme;
