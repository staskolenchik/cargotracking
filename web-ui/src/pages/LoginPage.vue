<template>
  <div class="page-container">
    <md-card>
      <md-card-header>
        <div class="md-title">Log In</div>
      </md-card-header>

      <md-card-content>
        <md-field :class="getValidationClass('login')">
          <label>Login:</label>
          <md-input
            v-model="login"
            :maxlength="fieldsLength.USER_MAX_LOGIN"
            :disable="sending"
            placeholder="Enter login"
          />
          <span
            class="md-error"
            v-if="!$v.login.required"
          >
            {{errors.FIELD_IS_REQUIRED}}
          </span>
          <span
            class="md-error"
            v-else-if="!$v.login.maxLength"
          >
            {{errors.MAX_LENGTH(fieldsLength.USER_MAX_LOGIN)}}
          </span>
          <span
            class="md-error"
            v-else-if="!$v.login.minLength"
          >
            {{errors.MIN_LENGTH(fieldsLength.USER_MIN_LOGIN)}}
          </span>
        </md-field>
        <md-field  :class="getValidationClass('password')">
          <label>Password:</label>
          <md-input
            v-model="password"
            type="password"
            :disable="sending"
            :maxlength="fieldsLength.USER_MAX_PASSWORD"
            placeholder="Enter password"
          />
          <span
            class="md-error"
            v-if="!$v.password.required"
          >
            {{errors.FIELD_IS_REQUIRED}}
          </span>
          <span
            class="md-error"
            v-else-if="!$v.password.maxLength"
          >
            {{errors.MAX_LENGTH(fieldsLength.USER_MAX_PASSWORD)}}
          </span>
          <span
            class="md-error"
            v-else-if="!$v.password.minLength"
          >
            {{errors.MIN_LENGTH(fieldsLength.USER_MIN_PASSWORD)}}
          </span>
        </md-field>

        <md-chip
          class="md-accent md-layout md-alignment-center"
          v-if="hasError"
        >
          {{errorMessage}}
        </md-chip>
      </md-card-content>

      <md-progress-bar md-mode="indeterminate" v-if="sending"/>

      <md-card-actions>
        <md-button
          class="md-accent"
          :disable="sending"
          to="/"
        >
          Back
        </md-button>
        <md-button
          class="md-primary"
          :disable="sending"
          to="/email/repairing"
        >
          Forgot password?
        </md-button>
        <md-button
          :disable="sending"
          @click="checkFields"
          class="md-primary"
        >
          Sign In
        </md-button>
      </md-card-actions>
    </md-card>
  </div>
</template>

<script>
  import {required, maxLength, minLength} from 'vuelidate/lib/validators';

  import {FieldsLength} from '../constants/fieldsLength';
  import {Errors} from '../constants/errors';
  import {Url} from '../constants/url';

  export default {
    name: 'Login',

    data: () => ({
      login: null,
      password: null,
      hasError: false,
      errorMessage: null,
      sending: false,
      fieldsLength: FieldsLength,
      errors: Errors
    }),

    validations: {
      login: {
        required,
        maxLength: maxLength(FieldsLength.USER_MAX_LOGIN),
        minLength: minLength(FieldsLength.USER_MIN_LOGIN)
      },
      password: {
        required,
        maxLength: maxLength(FieldsLength.USER_MAX_PASSWORD),
        minLength: minLength(FieldsLength.USER_MIN_PASSWORD)
      }
    },

    methods: {
      getValidationClass(fieldName) {
        const field = this.$v[fieldName];
        if (field) {
          return {
            'md-invalid': field.$invalid && field.$dirty
          };
        }
      },

      checkFields() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.signin();
        }
      },

      signin() {
        this.sending = true;
        if (!localStorage.refreshToken) {
          this.authorize();
          return;
        }
        const refreshTokenPayload = JSON.parse(atob(localStorage.refreshToken.split('.')[1]));
        if (refreshTokenPayload.exp + '000' < Date.now()) {
          this.$http.post(Url.LOGOUT, {
            userId: refreshTokenPayload.userId,
            ip: localStorage.ip
          })
            .then(() => {
              this.hasError = false;
              this.sending = false;
              localStorage.removeItem('accessToken');
              localStorage.removeItem('refreshToken');
              localStorage.removeItem('ip');
              localStorage.removeItem('roles');
              localStorage.removeItem('clientId');
              this.authorize();
            }, response => {
              this.hasError = true;
              this.sending = false;
              this.errorMessage = response.body.errors[0];
            });
        } else {
          this.authorize();
        }
      },

      authorize() {
        this.$http.get('//api.ipify.org?format=json').then(response => {
          localStorage.ip = response.body.ip;
          this.$http.post(Url.SIGNIN, {
            login: this.login,
            password: this.password,
            ip: response.body.ip
          })
            .then(response => {
              this.$store.dispatch('websocket/disconnect');

              this.hasError = false;
              this.sending = false;

              const tokens = response.bodyText.split(' ');
              localStorage.accessToken = tokens[0];
              localStorage.refreshToken = tokens[1];

              const payload = JSON.parse(atob(tokens[0].split('.')[1]));
              localStorage.setItem('roles', JSON.stringify(payload.auth));
              localStorage.clientId = payload.clientId;

              this.$router.push('/');
            }, response => {
              this.hasError = true;
              this.sending = false;
              this.errorMessage = response.body.errors[0];
            });
        });
      }
    },

    mounted: function() {
      this.$store.commit('sidebar/changeVisibility', false);
    }
  };
</script>

<style scoped>
  .page-container {
    max-width: 600px;
    margin: 0 auto;
  }

  .md-card {
    margin-top: 10%;
  }
</style>
