import { useEffect, useRef, useState } from "react";
import ProductItemLeft from "../components/productItemPage/ProductItemLeft";
import ProductItemRight from "../components/productItemPage/ProductItemRight";
import { useParams } from "react-router";
import { useProductItemById } from "../hooks/query";
import { api } from "../config/api";
import { UserReviewProductById } from "../types/userTypes";

const ProductItemPage = () => {
  const { productId } = useParams<{ productId: any }>();
  const [id, colour] = productId.split("-");
  const [review, setReview] = useState<UserReviewProductById | null>(null);
  const productDescRef = useRef<HTMLDivElement | null>(null);
  const productDetailsRef = useRef<HTMLDivElement | null>(null);
  const productReviewsRef = useRef<HTMLDivElement | null>(null);
  const deliveryRef = useRef<HTMLDivElement | null>(null);
  const [openSection, setOpenSection] = useState<{ [key: number]: boolean }>({
    1: false,
    2: false,
    3: false,
    4: false,
  });

  const { data } = useProductItemById(id, colour);

  useEffect(() => {
    const getUserReviewById = async () => {
      try {
        const res = await api.get(`/api/v1/review/productById/${id}`);
        setReview(res.data);
      } catch (error) {
        console.error(error);
      }
    };
    getUserReviewById();
  }, [id]);

  return (
    <div className="relative flex flex-col pt-[120px] min-h-screen max-lg:pt-[100px]">
      <div className="w-full flex gap-8 py-6 px-12 max-lg:px-6 max-lg:gap-2 max-md:flex-col">
        <div className="w-[60%] max-md:w-full">
          <ProductItemLeft
            data={data}
            review={review}
            productDescRef={productDescRef}
            productDetailsRef={productDetailsRef}
            productReviewsRef={productReviewsRef}
            deliveryRef={deliveryRef}
            openSection={openSection}
            setOpenSection={setOpenSection}
          />
        </div>
        <div className="w-[40%] max-md:w-full">
          <ProductItemRight
            data={data}
            review={review}
            productDescRef={productDescRef}
            productDetailsRef={productDetailsRef}
            productReviewsRef={productReviewsRef}
            deliveryRef={deliveryRef}
            openSection={openSection}
            setOpenSection={setOpenSection}
          />
        </div>
      </div>
    </div>
  );
};

export default ProductItemPage;
