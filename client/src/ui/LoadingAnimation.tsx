import { useLottie } from "lottie-react";
import loadingAnimation from '../utils/loadingAnimation.json'

const LoadingAnimation = () => {

    const styles = {
        height: 200,
        width: 200
      };

    const defaultOptions = {
        loop: true,
        autoplay: true,
        animationData: loadingAnimation,
        rendererSettings: {
            preserveAspectRatio: "xMidYMid slice"
          },        
      };

      const { View } = useLottie(defaultOptions, styles);

  return (
    <div>
      {View}
    </div>
  )
}

export default LoadingAnimation
