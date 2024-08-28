import useGetQuery, {UseGetQueryResult} from "@/services/queries/util/useGetQuery";
import Category, {CategoryDTO, categoryMapper} from "@/types/Category";

const useCategories = (): UseGetQueryResult<Array<Category>> => useGetQuery({
    url: "/api/categories",
    converter: (cs: Array<CategoryDTO>) => cs.map(categoryMapper.fromDTO),
});

export default useCategories;
