import {
  keepPreviousData,
  skipToken,
  useMutation,
  useQuery,
  useQueryClient,
} from "@tanstack/react-query";
import {
  Address,
  CartItem,
  Category,
  FamousShoes,
  Filter,
  GetShopOrder,
  Product,
  ProductItem,
  ProductItemByColour,
  ProductItemList,
  ProductItemListGroupedByFilters,
  ProductItemsFilters,
} from "../types/userTypes";
import { api } from "../config/api";

export const fetchCategoriesById = async (
  categoryId: number[]
): Promise<Category[]> => {
  const res = await api.get(`/api/v1/category/byId?ids=${categoryId}`);
  return res.data;
};

export const useCategoryQuery = (categoryId: number[]) => {
  const { data, error, isLoading, isError } = useQuery<Category[], Error>({
    queryKey: ["category", categoryId],
    queryFn: () => fetchCategoriesById(categoryId),
  });

  if (isError && error) {
    console.error("Error fetching categories: ", error);
  }

  return { data, error, isLoading, isError };
};

export const fetchNewestProducts = async (
  numberOfProducts: number
): Promise<ProductItem[]> => {
  const res = await api.get(
    `/api/v1/productItems/getAll?items=${numberOfProducts}`
  );
  return res.data;
};

export const useNewestProductsQuery = (numberOfProducts: number) => {
  const { data, error, isLoading, isError } = useQuery<ProductItem[], Error>({
    queryKey: ["newestProducts", numberOfProducts],
    queryFn: () => fetchNewestProducts(numberOfProducts),
  });

  if (isError && error) {
    console.error("Error fetching newest products: ", error);
  }

  return { data, error, isLoading, isError };
};

export const fetchFamousShoesCollection = async (
  variationId: number,
  variationOptionId: number
): Promise<FamousShoes[]> => {
  const res = await api.get(
    `/api/v1/products/searchByCategory?variationId=${variationId}&variationOptionId=${variationOptionId}&pageNumber=0&pageSize=10&sortBy=id&sortOrder=asc`
  );
  return res.data;
};

export const useFamousShoesCollectionQuery = (
  variationId: number,
  variationOptionId: number
) => {
  const { data, error, isLoading, isError } = useQuery<FamousShoes[], Error>({
    queryKey: ["famousShoes", variationId, variationOptionId],
    queryFn: () => fetchFamousShoesCollection(variationId, variationOptionId),
  });

  if (isError && error) {
    console.error("Error fetching newest products: ", error);
  }

  return { data, error, isLoading, isError };
};

export const fetchFilters = async (categoryId: number): Promise<Filter[]> => {
  const res = await api.get(`/api/v1/category/byId?ids=${categoryId}`);
  return res.data;
};

export const useFetchFilters = (categoryId: number) => {
  const { data, error, isLoading, isError } = useQuery<Filter[], Error>({
    queryKey: ["filters", categoryId],
    queryFn: () => fetchFilters(categoryId),
  });

  if (isError && error) {
    console.error("Error fetching filters: ", error);
  }

  return { data, error, isLoading, isError };
};

export const buildVariationIds = (selectedOption: {
  [key: number]: { optionId: number[] };
}) => {
  return Object.keys(selectedOption).join(",");
};

export const buildVariationOptionIds = (selectedOption: {
  [key: number]: { optionId: number[] };
}) => {
  const variationOptionIds: number[] = [];
  Object.values(selectedOption).forEach(({ optionId }) => {
    variationOptionIds.push(...optionId);
  });
  return variationOptionIds.join(",");
};

export const fetchProductsByFilters = async (
  categoryId: number,
  selectedOption: { [key: number]: { optionId: number[] } },
  pageNumber: number = 0,
  pageSize: number = 20,
  sortBy: string = "id",
  sortOrder: string = "asc"
): Promise<ProductItemList> => {
  const variationIds = buildVariationIds(selectedOption);
  const variationOptionIds = buildVariationOptionIds(selectedOption);
  const url = `/api/v1/productItems/searchByCategory?categoryId=${categoryId}&variationIds=${variationIds}&variationOptionIds=${variationOptionIds}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortBy=${sortBy}&sortOrder=${sortOrder}`;

  const res = await api.get(url);
  return res.data;
};

export const useProductsByFilters = (
  categoryId: number,
  selectedOption: { [key: number]: { optionId: number[] } }
) => {
  const { data, error, isFetching, isLoading, isError, refetch } = useQuery<
    ProductItemList,
    Error
  >({
    queryKey: ["productsByCategory", categoryId, selectedOption],
    queryFn: () => fetchProductsByFilters(categoryId, selectedOption),
  });

  if (isError && error) {
    console.error("Error fetching products by category: ", error);
  }

  return { data, error, isFetching, isLoading, isError, refetch };
};

export const fetchProductItemById = async (
  productId: number,
  colour: string
): Promise<ProductItemByColour> => {
  const res = await api.get(
    `/api/v1/productItems/${productId}?colour=${colour}`
  );
  return res.data;
};

export const useProductItemById = (productId: number, colour: string) => {
  const { data, error, isFetching, isError, isLoading } =
    useQuery<ProductItemByColour>({
      queryKey: ["productItemById", productId, colour],
      queryFn: () => fetchProductItemById(productId, colour),
    });

  if (isError && error) {
    console.error("Error fetching product item by id: ", error);
  }

  return { data, error, isFetching, isError, isLoading };
};

export const fetchProductsByFiltersGrouped = async (
  categoryId?: number,
  selectedOption: { [key: number]: { optionId: number[] } } = {},
  pageNumber: number = 0,
  pageSize: number = 24,
  sortBy: string = "id",
  sortOrder: string = "asc",
  limit?: number
): Promise<ProductItemListGroupedByFilters> => {
  let url = `/api/v1/productItems/searchByColour?pageNumber=${pageNumber}&pageSize=${pageSize}&sortBy=${sortBy}&sortOrder=${sortOrder}`;

  if (limit) {
    url += `&limit=${limit}`;
  }

  if (categoryId) {
    url += `&categoryId=${categoryId}`;
  }

  if (Object.keys(selectedOption).length > 0) {
    const variationOptionIds = buildVariationOptionIds(selectedOption);
    const variationIds = buildVariationIds(selectedOption);
    url += `&variationIds=${variationIds}&variationOptionIds=${variationOptionIds}`;
  }

  const res = await api.get(url);
  return res.data;
};

export const useProductsByFiltersGrouped = (
  categoryId?: number,
  selectedOption: { [key: number]: { optionId: number[] } } = {},
  pageNumber: number = 0,
  pageSize: number = 24,
  sortBy: string = "id",
  sortOrder: string = "asc",
  limit?: number
) => {
  const { data, error, isFetching, isLoading, isError, refetch } = useQuery<
    ProductItemListGroupedByFilters,
    Error,
    ProductItemListGroupedByFilters
  >({
    queryKey: [
      "productsByCategoryGrouped",
      categoryId,
      selectedOption,
      pageNumber,
      pageSize,
      sortBy,
      sortOrder,
      limit,
    ],
    queryFn: () =>
      fetchProductsByFiltersGrouped(
        categoryId,
        selectedOption,
        pageNumber,
        pageSize,
        sortBy,
        sortOrder,
        limit
      ),
    placeholderData: keepPreviousData,
  });

  if (isError && error) {
    console.error(
      "Error fetching products by category and selected Option: ",
      error
    );
  }

  return { data, error, isFetching, isLoading, isError, refetch };
};

export const fetchProductItemsFilters = async (
  categoryId?: number,
  selectedOption: { [key: number]: { optionId: number[] } } = {}
): Promise<ProductItemsFilters[]> => {
  let url = `/api/v1/productItems/filters`;

  if (categoryId) {
    url += `?categoryId=${categoryId}`;
  }

  if (Object.keys(selectedOption).length > 0) {
    const variationOptionIds = buildVariationOptionIds(selectedOption);
    const variationIds = buildVariationIds(selectedOption);
    url += `&variationIds=${variationIds}&variationOptionIds=${variationOptionIds}`;
  }

  const res = await api.get(url);
  return res.data;
};

export const useProductItemsFilters = (
  categoryId?: number,
  selectedOption: { [key: number]: { optionId: number[] } } = {}
) => {
  const { data, error, isFetching, isLoading, isError, refetch } = useQuery<
    ProductItemsFilters[],
    Error
  >({
    queryKey: ["productItemsFilters", categoryId, selectedOption],
    queryFn: () => fetchProductItemsFilters(categoryId, selectedOption),
    placeholderData: keepPreviousData,
  });

  if (isError && error) {
    console.error(
      "Error fetching productItemsFilters category and selected Option: ",
      error
    );
  }

  return { data, error, isFetching, isLoading, isError, refetch };
};

export const fetchProductById = async (productId: number): Promise<Product> => {
  const res = await api.get(`/api/v1/products/${productId}`);
  return res.data;
};

export const useProductById = (productId: number) => {
  const { data, error, isLoading, isFetching, isError, refetch } = useQuery<
    Product,
    Error
  >({
    queryKey: ["productById", productId],
    queryFn: () => fetchProductById(productId),
  });

  if (isError && error) {
    console.error("Error fetching product by id: ", error);
  }

  return { data, error, isLoading, isFetching, isError, refetch };
};

export const fetchCartByUserId = async (jwt: string): Promise<CartItem> => {
  const res = await api.get(`/api/v1/cart/users/cart`, {
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  });
  return res.data;
};

export const useCartByUserId = (jwt: string | null) => {
  const { data, error, isLoading, isFetching, isError, refetch } = useQuery<
    CartItem,
    Error
  >({
    queryKey: ["userCart", jwt],
    queryFn: jwt ? () => fetchCartByUserId(jwt) : skipToken,
  });

  if (isError && error) {
    console.error("Error fetching user cart by id: ", error);
  }

  return { data, error, isLoading, isFetching, isError, refetch };
};

export const fetchUserAddresses = async (
  userId: number
): Promise<Address[]> => {
  const res = await api.get(`/api/v1/address/${userId}`);
  return res.data;
};

export const useUserAddresses = (userId: number) => {
  const { data, error, isLoading, isFetching, isError, refetch } = useQuery<
    Address[],
    Error
  >({
    queryKey: ["userAddresses", userId],
    queryFn: () => fetchUserAddresses(userId),
  });

  if (isError && error) {
    console.error("Error fetching user addresses by id: ", error);
  }

  return { data, error, isLoading, isFetching, isError, refetch };
};

export const fetchUserShopOrders = async (
  jwt: string
): Promise<GetShopOrder[]> => {
  const res = await api.get(`/api/v1/shop-order/user`, {
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
  });
  return res.data;
};

export const useUserShopOrders = (jwt: string | null) => {
  const { data, error, isLoading, isFetching, isError, refetch } = useQuery<
    GetShopOrder[],
    Error
  >({
    queryKey: ["userShopOrders", jwt],
    queryFn: jwt ? () => fetchUserShopOrders(jwt) : skipToken,
  });

  if (isError && error) {
    console.error("Error fetching user addresses by id: ", error);
  }

  return { data, error, isLoading, isFetching, isError, refetch };
};

export const getFavoritesFromCookies = (): number[] => {
  const cookies = document.cookie.split(";");
  const favoritesCookie = cookies.find((c) =>
    c.trim().startsWith("favorites=")
  );

  if (favoritesCookie) {
    try {
      const parsed = JSON.parse(favoritesCookie.split("=")[1]);
      return Array.isArray(parsed)
        ? parsed.map(Number).filter((n) => !isNaN(n))
        : [];
    } catch (e) {
      console.error("Error parsing favorites cookie", e);
      return [];
    }
  }
  return [];
};

export const setFavoritesInCookies = (favorites: number[]) => {
  document.cookie = `favorites=${JSON.stringify(favorites)}; path=/; max-age=${
    30 * 24 * 60 * 60
  }`;
};

export const useFavorites = () => {
  return useQuery<number[], Error>({
    queryKey: ["favorites"],
    queryFn: () => Promise.resolve(getFavoritesFromCookies()),
  });
};

export const useToggleFavorite = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (productItemId: number) => {
      const currentFavorites = getFavoritesFromCookies();
      let updatedFavorites: number[];

      if (currentFavorites.includes(productItemId)) {
        updatedFavorites = currentFavorites.filter(
          (id) => id !== productItemId
        );
      } else {
        updatedFavorites = [...currentFavorites, productItemId];
      }

      setFavoritesInCookies(updatedFavorites);
      return Promise.resolve(updatedFavorites);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["favorites"] });
    },
    onError: (error: Error) => {
      console.error("Error toggling favorite:", error);
    },
  });
};
