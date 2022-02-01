<template>
  <div>
    <div class="md-layout md-alignment-center pb-20">
      <h1 class="header-block">Automate your routine work with <span class="blue">Trucking</span></h1>
    </div>
    <div class="md-layout md-alignment-center pb-20">
      <div class="content-block">
        Trucking is a web application designed for comfortable
        management of your work associated with the transportation of cargo.
        Adding storages, product owners, creating invoices, waybills, generating reports, and more.
        We have the best prices in the market of freight management software and the most extensive functionality.
      </div>
    </div>
    <div class="footer">
      <md-divider></md-divider>
      <div class="md-layout md-alignment-left">
        Copyright &copy; 2019 iTechArt <span class="right">Application name: {{applicationName}}</span>
      </div>
      <div class="md-layout md-alignment-left">
        All rights reserved <span class="right">Application version: {{applicationVersion}}</span>
      </div>
    </div>
  </div>
</template>

<script>
  import {Url} from '../../constants/url';

  export default {
    name: 'AboutContent',

    data: () => ({
      applicationName: null,
      applicationVersion: null
    }),

    mounted:function () {
      this.$http.get(Url.ABOUT)
        .then(response => {
          this.applicationName = response.data.applicationName;
          this.applicationVersion = response.data.version;
        });
    }
  };
</script>

<style scoped>
  .blue {
    color: #ebb92f;
  }

  .header-block {
    font-size: 46px;
    text-align: center;
    line-height: 1.1;
  }

  .content-block {
    font-size: 20px;
    max-width: 700px;
    text-align: center;
    line-height: 1.2;
  }

  .pb-20 {
    padding-bottom: 20px;
  }

  .right {
    margin-left: auto
  }

  @media screen and (min-height: 450px) {
    .footer {
      position: absolute;
      bottom: 0;
      height: 80px;
      left: -0px;
      padding: 20px;
      width: 100%;
    }
  }
</style>
