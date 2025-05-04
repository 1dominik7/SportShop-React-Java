import { useAppSelector } from "../state/store"

const useIsAdmin = (): boolean => {
    const user = useAppSelector((store) => store.auth.user)
    return user?.roleNames.some(role => role.toLowerCase() === "admin") || false
}

export default useIsAdmin;