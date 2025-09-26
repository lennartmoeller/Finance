import { RefObject, useEffect, useState } from "react";
const useElementWidth = <T extends HTMLElement>(ref: RefObject<T | null>) => {
    const [width, setWidth] = useState(0);

    useEffect(() => {
        const handleResize = () => {
            if (ref.current) {
                setWidth(ref.current.offsetWidth);
            }
        };

        handleResize();

        window.addEventListener("resize", handleResize);
        return () => {
            window.removeEventListener("resize", handleResize);
        };
    }, [ref]);

    return width;
};

export default useElementWidth;
