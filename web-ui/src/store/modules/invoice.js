import Vue from 'vue';
import moment from 'moment';

import {Url} from '../../constants/url';

const state = {
  items: [],
  totalElements: 0,
  filter: {
    number: null,
    beforeCreationDate: null,
    afterCreationDate: null,
    beforeVerifiedDate: null,
    afterVerifiedDate: null,
    statuses: []
  },
  data: {
    number: null,
    storage: null,
    productOwner: null,
    driver: null,
    products: [],
    productWriteoffs: []
  },
  sort: null,
  sending: false,
  hasError: false,
  errorMessage: null,
  invoiceSaved: false,
  invoiceUpdated: false,

  productAdding: false,
  productUpdating: false,

  hideMainForm: false,
  showProductOwners: false,
  showStorages: false,
  showDrivers: false
};

const getters = {
  getProductOwnerInfo: state => {
    if (state.data.productOwner) {
      const item = state.data.productOwner;
      return `Name: ${item.name}. Address: ${item.address}`;
    } else {
      return null;
    }
  },

  getStorageInfo: state => {
    if (state.data.storage) {
      const item = state.data.storage;
      return `Name: ${item.name}. Address: ${item.address}`;
    } else {
      return null;
    }
  },

  getDriverInfo: state => {
    if (state.data.driver) {
      const item = state.data.driver;

      let stringValue = `Full name: ${item.surname}`;
      stringValue += item.name ? ` ${item.name}` : '';
      stringValue += item.patronymic ? ` ${item.patronymic}` : '';
      stringValue += `. Login: ${item.login}`;

      return stringValue;
    } else {
      return null;
    }
  }
};

const actions = {
  getProductWriteoffs({commit}, invoiceId) {
    Vue.http.get(Url.PRODUCT_WRITEOFF, {
      params: {
        invoiceId: invoiceId
      },
      headers: {
        Authorization: `Bearer ${localStorage.accessToken}`
      }
    })
      .then(response => {
        commit('updateDataProductWriteoffs', response.body);
      }, response => {
        commit('setHasError', true);
        commit('setErrorMessage', response.body.errors[0]);
      });
  },

  pageInvoiceChange({commit}, pageInfo) {
    const pageAttr = {
      page: pageInfo.pageNumber - 1,
      size: pageInfo.pageSize,
      number: state.filter.number,
      beforeCreationDate: state.filter.beforeCreationDate,
      afterCreationDate: state.filter.afterCreationDate,
      beforeVerifiedDate: state.filter.beforeVerifiedDate,
      afterVerifiedDate: state.filter.afterVerifiedDate
    };
    if (state.sort){
      pageAttr.sort = state.sort;
    }
    if (state.filter.statuses.length !== 0) {
      pageAttr.statuses = state.filter.statuses;
    }
    Vue.http.get(Url.INVOICE + '{?statuses*}', {
      params: pageAttr,
      headers: {
        Authorization: `Bearer ${localStorage.accessToken}`
      }
    })
      .then(response => {
        const invoices = [];
        for (const item of response.data.content) {
          const status = item.status.charAt(0) + item.status.slice(1).replace('_', ' ').toLowerCase();

          const invoice = {
            id: item.id,
            number: item.number,
            creationDate: item.creationDate,
            verifiedDate: item.verifiedDate,
            status: status
          };
          invoices.push(invoice);
        }
        commit('setInvoices', invoices);
        commit('setTotalElements', response.data.totalElements);
      });
  },

  saveInvoice({commit}) {
    const form = {
      number: state.data.number,
      productOwnerId: state.data.productOwner.id,
      storageId: state.data.storage.id,
      driverId: state.data.driver.id,
      products: state.data.products
    };
    commit('setSending', true);
    Vue.http.post(Url.INVOICE, JSON.stringify(form), {
      headers: {
        Authorization: `Bearer ${localStorage.accessToken}`
      }
    })
      .then(() => {
        commit('setInvoiceSaved', true);
        commit('setSending', false);
        commit('setHasError', false);
        commit('removeData');
      }, response => {
        commit('setSending', false);
        commit('setHasError', true);
        commit('setErrorMessage', response.body.errors[0]);
      });
  },

  updateInvoice({commit}, id) {
    const form = {
      number: state.data.number,
      productOwnerId: state.data.productOwner.id,
      storageId: state.data.storage.id,
      driverId: state.data.driver.id,
      products: state.data.products.map(function(obj) {
        return {
          name: obj.name,
          amount: obj.amount,
          id: obj.isNew ? null: obj.id
        };
      })
    };
    commit('setSending', true);
    Vue.http.put(`${Url.INVOICE}/${id}`, JSON.stringify(form), {
      headers: {
        Authorization: `Bearer ${localStorage.accessToken}`
      }
    })
      .then(() => {
        commit('setInvoiceUpdated', true);
        commit('setSending', false);
        commit('setHasError', false);
      }, response => {
        commit('setSending', false);
        commit('setHasError', true);
        commit('setErrorMessage', response.body.errors[0]);
      });
  }
};

const mutations = {
  changeProductCount(state, data) {
    for (let i = 0; i < state.data.products.length; i++) {
      if (state.data.products[i].id === data.id) {
        let product = state.data.products[i];
        product.amount = data.amount;
        state.data.products.splice(i, 1, product);
      }
    }
  },

  setInvoices(state, invoices) {
    state.items = invoices;
  },

  setTotalElements(state, totalElements) {
    state.totalElements = totalElements;
  },

  removeData(state) {
    state.data.number = null;
    state.data.productOwner = null;
    state.data.storage = null;
    state.data.driver = null;
    state.data.products = [];
  },

  removeFilter(state) {
    state.filter.number = null;
    state.filter.beforeCreationDate = null;
    state.filter.afterCreationDate = null;
    state.filter.beforeVerifiedDate = null;
    state.filter.afterVerifiedDate = null;
    state.filter.statuses = [];
  },

  setSort(state, sort) {
    state.sort = sort;
  },

  setSending(state, value) {
    state.sending = value;
  },

  setHasError(state, value) {
    state.hasError = value;
  },

  setErrorMessage(state, errorMessage) {
    state.errorMessage = errorMessage;
  },

  setInvoiceSaved(state, value) {
    state.invoiceSaved = value;
  },

  setInvoiceUpdated(state, value) {
    state.invoiceUpdated = value;
  },

  updateNumber(state, number) {
    state.filter.number = number;
  },

  updateBeforeCreationDate(state, date) {
    state.filter.beforeCreationDate = parseDate(date);
  },

  updateAfterCreationDate(state, date) {
    state.filter.afterCreationDate = parseDate(date);
  },

  updateBeforeVerifiedDate(state, date) {
    state.filter.beforeVerifiedDate = parseDate(date);
  },

  updateAfterVerifiedDate(state, date) {
    state.filter.afterVerifiedDate = parseDate(date);
  },

  updateStatuses(state, statuses) {
    state.filter.statuses = statuses;
  },

  updateDataNumber(state, number) {
    state.data.number = number;
  },

  updateDataProductOwner(state, productOwner) {
    state.data.productOwner = productOwner;
  },

  updateDataStorage(state, storage) {
    state.data.storage = storage;
  },

  updateDataDriver(state, driver) {
    state.data.driver = driver;
  },

  updateDataProducts(state, products) {
    state.data.products = products;
  },

  updateDataProductWriteoffs(state, productWriteoffs) {
    state.data.productWriteoffs = productWriteoffs;
  },

  addDataProduct(state, product) {
    state.data.products.push(product);
  },

  setUpdatingDataProduct(state, id) {
    for (let i = 0; i < state.data.products.length; i++) {
      if (state.data.products[i].id === id) {
        let product = state.data.products[i];
        product.id = null;
        state.data.products.splice(i, 1, product);
        return;
      }
    }
  },

  pushDataProduct(state, product) {
    if (state.data.products.length === 1) {
      product.id = 1;
    } else {
      product.id = state.data.products[state.data.products.length - 2].id + 1;
    }
    product.isNew = true;
    state.data.products.splice(state.data.products.length - 1, 1, product);
  },

  updateDataProduct(state, product) {
    for (let i = 0; i < state.data.products.length; i++) {
      if (state.data.products[i].id === null) {
        state.data.products.splice(i, 1, product);
        return;
      }
    }
  },

  deleteDataProduct(state, productIds) {
    state.data.products = state.data.products.filter(function(value) {
      return !productIds.includes(value.id);
    });
  },

  deleteDataProductWriteoff(state, writeoffIds) {
    state.data.productWriteoffs = state.data.productWriteoffs.filter(function(value) {
      return !writeoffIds.includes(value.id);
    });
  },

  setProductAdding(state, value) {
    state.productAdding = value;
  },

  setProductUpdating(state, value) {
    state.productUpdating = value;
  },

  showProductOwnersTable(state) {
    state.hideMainForm = true;
    state.showProductOwners = true;
  },

  showStoragesTable(state) {
    state.hideMainForm = true;
    state.showStorages = true;
  },

  showDriversTable(state) {
    state.hideMainForm = true;
    state.showDrivers = true;
  },

  showMainForm(state) {
    state.hideMainForm = false;
    state.showProductOwners = false;
    state.showStorages = false;
    state.showDrivers = false;
  }
};

function parseDate(date) {
    return date ? moment(date).format('YYYY-MM-DD') : null;
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
};
