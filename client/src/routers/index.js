import { createRouter, createWebHistory } from "vue-router";
import { authApi } from "../authentication/apiServices";
import HomePage from "../pages/HomePage.vue";
import LoginPage from "../pages/auth/LoginPage.vue";
import RegisterPage from "../pages/auth/RegisterPage.vue";
import ResetPassword from "../pages/auth/ResetPassword.vue";

const routes = [
  {
    path: "/",
    name: "Home",
    component: HomePage,
    meta: { unProtectedRoute: true },
  },
  {
    path: "/login",
    name: "Login",
    component: LoginPage,
    meta: { unProtectedRoute: true },
  },
  {
    path: "/register",
    name: "Register",
    component: RegisterPage,
    meta: { unProtectedRoute: true },
  },
  {
    path: "/reset-password",
    name: "ResetPassword",
    component: ResetPassword,
    meta: { unProtectedRoute: true },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  if (to.meta["unProtectedRoute"]) {
    next();
  } else {
    authApi
      .jwtValidate()
      .then(() => next())
      .catch(() => next("/login"));
  }
});

export default router;
