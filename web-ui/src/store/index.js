import Vue from 'vue';
import Vuex from 'vuex';

import storage from './modules/storage';
import client from './modules/client';
import invoice from './modules/invoice';
import waybill from './modules/waybill';
import sidebar from './modules/sidebar';
import user from './modules/user';
import productOwner from './modules/productOwner';
import car from './modules/car';
import websocket from './modules/websocket';

Vue.use(Vuex);

export const store = new Vuex.Store({
  modules: {
    storage: storage,
    client: client,
    invoice: invoice,
    waybill: waybill,
    user: user,
    productOwner: productOwner,
    car: car,
    sidebar: sidebar,
    websocket: websocket
  }
});



