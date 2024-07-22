<script setup></script>

<template>
  <router-view :key="$route.path"></router-view>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: Arial, sans-serif;
}

body {
  height: 100vh;
  margin: 0;
}
</style>
