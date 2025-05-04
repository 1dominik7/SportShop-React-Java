import { Button, TextField } from "@mui/material";
import { useFormik } from "formik";
import { useEffect, useState } from "react";
import { ProfileChangeType, ProfileType } from "../../types/userTypes";
import { api } from "../../config/api";
import { useAppSelector } from "../../state/store";
import LoadingAnimation from "../../ui/LoadingAnimation";
import { updateProfileValidationSchema } from "../../validator/userValidator";

const ProfilePersonalData = () => {
  const auth = useAppSelector((store) => store?.auth);
  const [profile, setProfile] = useState<ProfileType | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const getProfile = async () => {
    setIsLoading(true);
    try {
      const res = await api.get(`/api/v1/users/profile`, {
        headers: {
          Authorization: `Bearer ${auth.jwt}`,
        },
      });
      setProfile(res.data);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    getProfile();
  }, [auth.jwt]);

  const formik = useFormik({
    enableReinitialize: true,
    initialValues: {
      firstname: profile?.firstname ?? "",
      lastname: profile?.lastname ?? "",
      dateOfBirth: profile?.dateOfBirth ?? "",
    },
    validationSchema: updateProfileValidationSchema,
    onSubmit: async (values: ProfileChangeType) => {
      updateProfile(values);
    },
  });

  const updateProfile = async (values: ProfileChangeType) => {
    try {
      await api.put(`/api/v1/users/profile`, values, {
        headers: {
          Authorization: `Bearer ${auth.jwt}`,
        },
      });
      await getProfile();
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div className="relative py-11 px-20 max-md:px-6">
      {isLoading ? (
        <div className="fixed top-0 left-0 flex h-full w-full z-50 bg-white opacity-80 items-center justify-center">
          <LoadingAnimation />
        </div>
      ) : (
        <div className="w-[500px] flex flex-col gap-4 pb-6 border-b-[1px] border-gray-200 max-md:w-full">
          <TextField
            fullWidth
            name="firstname"
            label="Firstname"
            value={formik.values.firstname}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.firstname && Boolean(formik.errors.firstname)}
            helperText={formik.touched.firstname && formik.errors.firstname}
          />
          <TextField
            fullWidth
            name="lastname"
            label="Lastname"
            value={formik.values.lastname}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched.lastname && Boolean(formik.errors.lastname)}
            helperText={formik.touched.lastname && formik.errors.lastname}
          />
          <TextField
            fullWidth
            type="date"
            name="dateOfBirth"
            label="Date Of Birth"
            InputLabelProps={{
              shrink: true,
            }}
            value={formik.values.dateOfBirth}
            onChange={formik.handleChange}
          />
          <TextField
            fullWidth
            className="bg-slate-300"
            label="Email"
            InputProps={{
              readOnly: true,
            }}
            value={profile?.email ?? ""}
          />
          <TextField
            fullWidth
            type="date"
            className="bg-slate-300"
            label="Created date"
            InputProps={{
              readOnly: true,
            }}
            value={profile?.createdDate ? profile.createdDate.slice(0, 10) : ""}
          />
          <Button
            onClick={() => formik.handleSubmit()}
            className="w-full"
            variant="contained"
            sx={{ py: "11px", backgroundColor: "black", fontWeight: "bold" }}
          >
            Save
          </Button>
        </div>
      )}
    </div>
  );
};

export default ProfilePersonalData;
