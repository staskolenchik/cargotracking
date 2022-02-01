import page from 'v-page';
import Vue from 'vue';

export const VendorComponents = {
  use: () => {
    Vue.use(page, {
      info: false,
      pageSizeMenu: () => [10, 20],
      language: 'en',
      align: 'center',
      first: false,
      last:  false
    });
  }
};
