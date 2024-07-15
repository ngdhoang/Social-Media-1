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
  <div>
    <form @submit="register">
      <span>Register an account</span>
      <div>
        <input
          id="first_name"
          v-model="registerForm.firstName"
          type="text"
          name="first_name"
          placeholder="First Name"
          required
        />
      </div>
      <div>
        <input
          id="last_name"
          v-model="registerForm.lastName"
          type="text"
          name="last_name"
          placeholder="Last Name"
          required
        />
      </div>
      <div>
        <input
          id="user_email"
          v-model="registerForm.userEmail"
          type="email"
          name="user_email"
          placeholder="example@example.com"
          required
        />
      </div>
      <div>
        <input
          id="password"
          v-model="registerForm.password"
          type="password"
          name="password"
          placeholder="Password"
          required
        />
      </div>
      <div>
        <input
          id="confirm_password"
          v-model="registerForm.confirmPassword"
          type="password"
          name="confirm_password"
          placeholder="Confirm Password"
          required
        />
      </div>
      <div>
        <input
          id="code"
          v-model="registerForm.code"
          type="text"
          name="code"
          placeholder="Code"
        />
      </div>
      <div><button>Register</button></div>
      <div>
        <span>Have you already got an account? </span>
        <router-link to="/login">Login</router-link>
      </div>
    </form>
  </div>
</template>

<style scoped></style>
