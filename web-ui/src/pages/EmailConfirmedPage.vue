<template>
  <div class="page-container">
    <md-card>
      <md-card-content id="content">
        <div v-if="successConfirm">
          {{ messages.EMAIL_CONFIRMED }}
        </div>
        <div v-else>
          {{ messages.EMAIL_NOT_CONFIRMED }}
        </div>
      </md-card-content>

      <md-card-actions>
        <md-button
          class="md-primary"
          to="/"
        >
          Go to site
        </md-button>
      </md-card-actions>
    </md-card>
  </div>
</template>

<script>
  import {Messages} from '../constants/messages';
  import {Url} from '../constants/url';

  export default {
    name: 'EmailConfirmedPage',

    data: () => ({
      messages: Messages,

      successConfirm: null
    }),

    mounted: function() {
      const content = document.getElementById('content');
      this.$http.get(`${Url.CONFIRM_EMAIL}/${this.$route.params.uuid}`, {
        headers: {
          Authorization: `Bearer ${localStorage.accessToken}`
        }
      })
        .then(() => {
          content.classList.add('success');
          content.classList.remove('error');
          this.successConfirm = true;
        }, () => {
          content.classList.remove('success');
          content.classList.add('error');
          this.successConfirm = false;
        });
    }
  };
</script>

<style scoped>
  .page-container {
    max-width: 600px;
    margin: 8% auto 0;
  }
  .success {
    background-color: #bce8ae;
  }
  .error {
    background-color: #eba49b;
  }
</style>
