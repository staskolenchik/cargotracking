const state = {
  shown: false,
  authorized: false,
  username: null
};

const mutations = {
  changeAuthorized(state, value) {
    state.authorized = value;
  },

  changeVisibility(state, value) {
    state.shown = value;
  },

  changeUsername(state, username) {
    state.username = username;
  }
};

export default {
  namespaced: true,
  state,
  mutations
};
