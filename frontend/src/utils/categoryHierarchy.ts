import Category from "@/types/Category";

/**
 * Expands a list of category IDs to include all their child categories recursively.
 * This matches the backend logic in CategoryService.collectChildCategoryIdsRecursively.
 *
 * @param categoryIds - The list of category IDs to expand (can be null or empty)
 * @param allCategories - All available categories in the system
 * @returns An array of category IDs including the original IDs and all their children, or null if input is null/empty
 */
export const expandCategoryIds = (
    categoryIds: number[] | null | undefined,
    allCategories: Category[],
): number[] | null => {
    if (!categoryIds || categoryIds.length === 0) {
        return null;
    }

    const result = new Set<number>(categoryIds);

    // Helper function to recursively find children
    const addChildrenRecursively = (parentId: number): void => {
        const children = allCategories.filter(
            (cat) => cat.parentId === parentId,
        );
        children.forEach((child) => {
            result.add(child.id);
            addChildrenRecursively(child.id);
        });
    };

    // For each category ID, add all its children recursively
    categoryIds.forEach((id) => {
        addChildrenRecursively(id);
    });

    return Array.from(result);
};
