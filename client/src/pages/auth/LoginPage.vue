<script>
import { authApi } from "../../authentication/apiServices";

export default {
  name: "Login",
  data() {
    return {
      loginForm: {
        userEmail: "",
        password: "",
      },
    };
  },
  methods: {
    login(event) {
      event.preventDefault();
      authApi
        .login(this.loginForm)
        .then((response) => {
          localStorage.setItem(
            "user_info",
            JSON.stringify(response.data["data"])
          );
          location.assign("/");
          alert("Successfully login");
        })
        .catch((err) => {
          alert(err.response.data.message);
        });
    },
  },
};
</script>

<template>
  <div>
    <form @submit="login">
      <span>Login</span>
      <div>
        <input
          id="user_email"
          v-model="loginForm.userEmail"
          type="email"
          name="user_email"
          placeholder="example@example.com"
          required
        />
      </div>
      <div>
        <input
          id="password"
          v-model="loginForm.password"
          type="password"
          name="password"
          placeholder="Password"
          required
        />
      </div>
      <div><button>Login</button></div>
      <div>
        <router-link to="/reset-password">Forgot Password?</router-link>
      </div>
      <div>
        <router-link to="/register">Create your Account</router-link>
      </div>
    </form>
  </div>
</template>

<style scoped></style>
