import useItems, { UseItemsResult } from "@/services/util/useItems";
import Category, { CategoryDTO, categoryMapper } from "@/types/Category";
import { ExtURL } from "@/utils/ExtURL";

export const categoriesUrl = new ExtURL(
    "api/categories",
    window.location.origin,
);

export const useCategories = (): UseItemsResult<Array<Category>> =>
    useItems({
        url: categoriesUrl,
        converter: (cs: Array<CategoryDTO>) => cs.map(categoryMapper.fromDTO),
    });
