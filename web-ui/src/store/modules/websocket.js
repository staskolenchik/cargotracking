import SockJS from 'sockjs-client';
import {Stomp} from '@stomp/stompjs';

import {Url} from '../../constants/url';

const state = {
  messageTaken: false,
  messageText: null,
  stompClient: null,
  subscription: null
};

const actions = {
  disconnect({commit}) {
    if (state.subscription) {
      state.subscription.unsubscribe();
    }
    if (state.stompClient) {
      state.stompClient.disconnect(function() {});
    }
    commit('removeSubscribe');
    commit('removeStompClient');
  },

  connect({commit}) {
    state.stompClient = Stomp.over(function() {
      return new SockJS(Url.WS_CONNECTION);
    });
    state.stompClient.connect({
      Authorization: `Bearer ${localStorage.accessToken}`
    }, () => {
      const userRoles = JSON.parse(localStorage.getItem('roles'));

      if (userRoles.includes('MANAGER')) {
        state.subscription = state.stompClient.subscribe(
          `${Url.WS_WAYBILL_NOTIFICATION}/${localStorage.clientId}`,
          response => {
            commit('changeMessageTaken', true);
            commit('changeMessageText', response.body);
          }
        );
      }
    });
  },

  // eslint-disable-next-line
  send({commit}, message) {
    const form = {
      achieveTime: message.achieveTime,
      checkpointAddress: message.checkpointAddress
    };

    state.stompClient.send(`${Url.WS_CHECKPOINT_REACHED}/${localStorage.clientId}`, {
      Authorization: `Bearer ${localStorage.accessToken}`
    }, JSON.stringify(form));
  }
};

const mutations = {
  changeMessageTaken(state, value) {
    state.messageTaken = value;
  },

  changeMessageText(state, text) {
    state.messageText = text;
  },

  removeStompClient(state) {
    state.stompClient = null;
  },

  removeSubscribe(state) {
    state.subscription = null;
  }
};

export default {
  namespaced: true,
  state,
  actions,
  mutations
};
