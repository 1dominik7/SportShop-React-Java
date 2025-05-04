import BannerHome from "../components/homePage/BannerHome";
import BannerCenter from "../components/homePage/BannerCenter";
import LoadingAnimation from "../ui/LoadingAnimation";

const HomePage = () => {
  return (
    <div className="flex flex-col">
      <div className="h-screen">
        <BannerHome />
      </div>
      <div>
        <BannerCenter />
      </div>
    </div>
  );
};

export default HomePage;
