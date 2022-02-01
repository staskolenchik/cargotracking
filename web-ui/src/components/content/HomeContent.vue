<template>
  <p>Home page</p>
</template>

<script>
  export default {
    name: 'HomeContent',

    created: function() {
      const roles = JSON.parse(localStorage.getItem('roles'));

      if (roles) {
        if (roles.includes('SYS_ADMIN')) {
          this.$router.replace('/clients');
        } else if (roles.includes('ADMIN')) {
          this.$router.replace('/users');
        } else if (roles.includes('DISPATCHER') || roles.includes('MANAGER')) {
          this.$router.replace('/invoices');
        } else if (roles.includes('DRIVER')) {
          this.$router.replace('/waybills');
        } else if (roles.includes('COMPANY_OWNER')) {
          this.$router.replace('/reports/client');
        }
      } else {
        localStorage.clear();
        this.$router.replace('/about');
      }
    }
  };
</script>

<style scoped>

</style>
