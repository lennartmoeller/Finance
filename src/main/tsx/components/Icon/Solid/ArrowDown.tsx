import React from "react";

import IconBase, {IconProps} from "@/components/Icon/Base/IconBase";
import Svg from '@/components/Icon/svgs/solid/arrow-down.svg';

const ArrowDown: React.FC<IconProps> = (props) => {
    return (
        <IconBase {...props}>
            <Svg/>
        </IconBase>
    );
};

export default ArrowDown;
