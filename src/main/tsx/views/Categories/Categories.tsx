import React from "react";

import useGetQuery from "@/hooks/useGetQuery";
import Category, {CategoryDTO, categoryMapper} from "@/types/Category";

const Categories: React.FC = () => {
    const {
        data,
        error,
        isLoading
    } = useGetQuery<Record<number, CategoryDTO>, Array<Category>>(
        'categories',
        body => Object.values(body).map(categoryMapper.fromDTO)
    );

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;
    if (!data) return <div>No data available</div>;

    return (
        <div>
            <h1>Categories</h1>
            {data.map((category, index) => (
                <div key={index}>
                    <p>Id: {category.id}</p>
                    <p>Parent Id: {category.parentId}</p>
                    <p>Label: {category.label}</p>
                    <p>Transaction Type: {category.transactionType}</p>
                    <p>Smooth Type: {category.smoothType}</p>
                </div>
            ))}
        </div>
    );

};

export default Categories;
