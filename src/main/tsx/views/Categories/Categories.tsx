import {categoryMapper} from "@/mapper/mappings";
import {useGetQuery} from "@/mapper/useGetQuery";
import {Category} from "@/types/Category";
import {CategoryDTO} from "@/types/CategoryDTO";
import React from "react";

const Categories: React.FC = () => {
    const {data, error, isLoading} = useGetQuery<Record<number, CategoryDTO>>('categories');

    if (isLoading) return <div>Loading...</div>;
    if (error) return <div>Error: {error.message}</div>;
    if (!data) return <div>No data available</div>;

    const categories: Category[] = Object.values(data).map(categoryMapper.fromDTO);

    return (
        <div>
            <h1>Categories</h1>
            {categories.map((category, index) => (
                <div key={index}>
                    <p>Id: {category.id}</p>
                    <p>Parent Id: {category.parentId}</p>
                    <p>Label: {category.label}</p>
                    <p>Transaction Type: {category.transactionType}</p>
                    <p>Smooth Type: {category.smoothType}</p>
                    <p>Start: {category.start.toLocaleDateString()}</p>
                    <p>End: {category.end.toLocaleDateString()}</p>
                    <p>Target: {category.target}</p>
                </div>
            ))}
        </div>
    )

};

export default Categories;
