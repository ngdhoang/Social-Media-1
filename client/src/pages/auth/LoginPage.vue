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
  <div class="login-container">
    <div class="login-form-container">
      <form @submit="login">
        <h2>Login</h2>
        <div class="input-group">
          <input
            id="user_email"
            v-model="loginForm.userEmail"
            type="email"
            name="user_email"
            placeholder="example@example.com"
            required
          />
        </div>
        <div class="input-group">
          <input
            id="password"
            v-model="loginForm.password"
            type="password"
            name="password"
            placeholder="Password"
            required
          />
        </div>
        <div class="button-group">
          <button type="submit">Login</button>
        </div>
        <div class="link-group">
          <router-link to="/reset-password">Forgot Password?</router-link>
          <router-link to="/register">Create your Account</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<style>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url("../../assets/images/bg.png") no-repeat center center;
  background-size: cover;
  width: 100%;
  position: relative;
}

.login-form-container {
  background: rgba(255, 255, 255, 0.9);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  max-width: 400px;
  width: 100%;
  text-align: center;
  z-index: 1;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.input-group {
  margin-bottom: 15px;
}

input[type="email"],
input[type="password"] {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 16px;
}

.button-group {
  margin-top: 20px;
}

button {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 4px;
  background-color: #28a745;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #218838;
}

.link-group {
  margin-top: 15px;
}

.link-group a {
  display: block;
  color: #007bff;
  text-decoration: none;
  margin-top: 5px;
}

.link-group a:hover {
  text-decoration: underline;
}
</style>
