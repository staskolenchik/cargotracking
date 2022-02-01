import Vue from 'vue';
import VueResource from 'vue-resource';
import Router from 'vue-router';
import Vuelidate from 'vuelidate';

import App from './App.vue';
import {MaterialComponents} from './config/material';
import {VendorComponents} from './config/vendorComponents';
import {AppRouter} from './config/routes';
import {store} from './store';

Vue.config.productionTip = false;

Vue.use(Router);
Vue.use(VueResource);
Vue.use(Vuelidate);

MaterialComponents.use();
VendorComponents.use();

new Vue({
  render: h => h(App),
  store,
  router: AppRouter.create()
}).$mount('#app');
