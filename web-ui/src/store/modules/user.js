import Vue from 'vue';

import {Url} from '../../constants/url';

const state = {
  items: [],
  totalElements: 0,
  sort: null,
  name: null,
  surname: null,
  patronymic: null,
  bornDate: null,
  email: null,
  town: null,
  street: null,
  house: null,
  flat: null,
  login: null,
  password: null,
  passwordConfirm: null,
  userRoles:[],
  filter: {
    surname: null,
    userRoles: []
  }
};

const actions = {
  pageUsersChange({commit}, pageInfo) {
    const pageAttr = {
      page: pageInfo.pageNumber - 1,
      size: pageInfo.pageSize,
      surname: state.filter.surname,
      userRoles: state.filter.userRoles
    };
    if (state.sort){
      pageAttr.sort = state.sort;
    }
    if (state.filter.userRoles.length !== 0) {
      pageAttr.userRoles = state.filter.userRoles;
    }
    Vue.http.get(Url.USER + '{?userRoles*}', {
      params: pageAttr,
      headers: {
        Authorization: `Bearer ${localStorage.accessToken}`
      }
    })
      .then(response => {
        const users = [];
        for (const item of response.data.content) {
          const user = {
            id: item.id,
            name: item.name,
            surname: item.surname,
            patronymic: item.patronymic,
            login: item.login,
            userRoles: []
          };
          for (let i = 0; i < item.userRoles.length; i++) {
            const role = item.userRoles[i].id.role.charAt(0) +
                item.userRoles[i].id.role.slice(1).replace('_', ' ').toLowerCase();

            user.userRoles.push(role);
          }
          users.push(user);
        }
        commit('setUsers', users);
        commit('setTotalElements', response.data.totalElements);
      });
  }
};

const mutations = {
  setUsers(state, users) {
    state.items = users;
  },

  setTotalElements(state, totalElements) {
    state.totalElements = totalElements;
  },

  setSort(state, sort) {
    state.sort = sort;
  },

  removeFilter(state) {
    state.filter.surname = null;
    state.filter.userRoles = [];
  },

  updateSurname(state, surname) {
    state.filter.surname = surname;
  },

  updateUserRoles(state, userRoles) {
    state.filter.userRoles = userRoles;
  }
};

export default {
  namespaced: true,
  state,
  actions,
  mutations
};
