import Category from "@/types/Category";

export const expandCategoryIds = (
    categoryIds: number[] | null | undefined,
    allCategories: Category[],
): number[] | null => {
    if (!categoryIds || categoryIds.length === 0) {
        return null;
    }

    const result = new Set<number>(categoryIds);

    const addChildrenRecursively = (parentId: number): void => {
        const children = allCategories.filter(
            (cat) => cat.parentId === parentId,
        );
        children.forEach((child) => {
            result.add(child.id);
            addChildrenRecursively(child.id);
        });
    };

    categoryIds.forEach((id) => {
        addChildrenRecursively(id);
    });

    return Array.from(result);
};
