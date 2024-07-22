<script>
import { authApi } from "../../authentication/apiServices";

export default {
  name: "RegisterPage",
  data() {
    return {
      registerForm: {
        firstName: "",
        lastName: "",
        userEmail: "",
        password: "",
        confirmPassword: "",
        code: "",
      },
    };
  },
  methods: {
    register(event) {
      event.preventDefault();
      authApi
        .register(this.registerForm)
        .then(() => {
          location.assign("/login");
          alert("Successfully register an account");
        })
        .catch((err) => {
          alert(err.response.data.message);
        });
    },
  },
};
</script>

<template>
  <div class="register-container">
    <div class="register-form-container">
      <form @submit="register">
        <h2>Register an account</h2>
        <div class="input-group">
          <input
            id="first_name"
            v-model="registerForm.firstName"
            type="text"
            name="first_name"
            placeholder="First Name"
            required
          />
        </div>
        <div class="input-group">
          <input
            id="last_name"
            v-model="registerForm.lastName"
            type="text"
            name="last_name"
            placeholder="Last Name"
            required
          />
        </div>
        <div class="input-group">
          <input
            id="user_email"
            v-model="registerForm.userEmail"
            type="email"
            name="user_email"
            placeholder="example@example.com"
            required
          />
        </div>
        <div class="input-group">
          <input
            id="password"
            v-model="registerForm.password"
            type="password"
            name="password"
            placeholder="Password"
            required
          />
        </div>
        <div class="input-group">
          <input
            id="confirm_password"
            v-model="registerForm.confirmPassword"
            type="password"
            name="confirm_password"
            placeholder="Confirm Password"
            required
          />
        </div>
        <div class="input-group">
          <input
            id="code"
            v-model="registerForm.code"
            type="text"
            name="code"
            placeholder="Code"
          />
        </div>
        <div class="button-group">
          <button type="submit">Register</button>
        </div>
        <div class="link-group">
          <span>Have you already got an account? </span>
          <router-link to="/login">Login</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: url("../../assets/images/bg.png") no-repeat center center;
  background-size: cover;
  width: 100%;
  position: relative;
}

.register-form-container {
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

input[type="text"],
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
  color: #007bff;
  text-decoration: none;
  margin-left: 5px;
}

.link-group a:hover {
  text-decoration: underline;
}
</style>
