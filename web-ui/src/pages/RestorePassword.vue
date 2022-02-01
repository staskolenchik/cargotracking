<template>
  <div class="page-container">
    <form novalidate @submit.prevent="validateForm">
      <md-card v-if="firstPage">
        <md-card-content>
          <div>
            <md-field :class="getValidationClass('password')">
              <label>Password</label>
              <md-input
                v-model="password"
                name="password"
                :disabled="sending"
                :minlength="fieldsLength.USER_MIN_PASSWORD"
                :maxlength="fieldsLength.USER_MAX_PASSWORD"
                type="password"
                autocomplete="passwordConfirm"
              />
              <span
                class="md-error"
                v-if="!$v.password.required"
              >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
              <span
                class="md-error"
                v-else-if="!$v.password.minLength"
              >
              {{errors.MIN_LENGTH}}
            </span>
              <span
                class="md-error"
                v-else-if="!$v.password.maxLength"
              >
              {{errors.MAX_LENGTH}}
            </span>
            </md-field>

            <md-field :class="getValidationClass('passwordConfirm')">
              <label>Confirm password</label>
              <md-input
                v-model="passwordConfirm"
                name="passwordConfirm"
                :disabled="sending"
                :minlength="fieldsLength.USER_MIN_PASSWORD"
                :maxlength="fieldsLength.USER_MAX_PASSWORD"
                type="password"
                autocomplete="passwordConfirm"
              />
              <span
                class="md-error"
                v-if="!$v.passwordConfirm.required"
              >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
              <span
                class="md-error"
                v-else-if="!$v.passwordConfirm.minLength"
              >
              {{errors.USER_MIN_PASSWORD}}
            </span>
              <span
                class="md-error"
                v-else-if="!$v.passwordConfirm.maxLength"
              >
              {{errors.USER_MAX_PASSWORD}}
            </span>
              <span
                class="md-error"
                v-else-if="!$v.passwordConfirm.sameAs"
              >
              {{errors.NOT_CONFIRMED}}
            </span>
            </md-field>
          </div>
        </md-card-content>
        <div v-if="firstPage">
          <md-progress-bar md-mode="indeterminate" v-if="sending"/>
          <md-card-actions>
            <md-button
              class="md-access"
              :disabled="sending"
              to="/"
            >
              Cancel
            </md-button>
            <md-button
              class="md-primary"
              type="submit"
              :disabled="sending"
            >
              Save
            </md-button>
          </md-card-actions>

          <md-chip
            class="md-accent md-layout md-alignment-center"
            v-if="hasError"
          >
            {{errorMessage}}
          </md-chip>
        </div>

      </md-card>
      <div v-if="successfulPage" class="md-display-3">
        Your password have been restored!
        <md-card-actions>
          <md-button
            class="md-accent"
            :disabled="sending"
            to="/login"
          >
            Back to site
          </md-button>
        </md-card-actions>
      </div>

      <md-snackbar
        :md-active.sync="passwordRestored"
      >
        {{messages.RESTORE_PASSWORD}}
      </md-snackbar>
    </form>
  </div>
</template>

<script>
  import {maxLength, minLength, required, sameAs} from 'vuelidate/lib/validators';

  import {FieldsLength} from '../constants/fieldsLength';
  import {Errors} from '../constants/errors';
  import {Messages} from '../constants/messages';
  import {Url} from '../constants/url';

  export default {
    name: 'RestorePassword',

    data: () => ({
      errors: Errors,
      fieldsLength: FieldsLength,
      messages: Messages,
      firstPage: true,

      password: null,
      passwordConfirm: null,

      sending: false,
      passwordRestored: false,
      hasError: false,
      errorMessage: null,
      successfulPage: false
    }),

    validations: {
      password: {
        required,
        maxLength: maxLength(FieldsLength.USER_MAX_PASSWORD),
        minLength: minLength(FieldsLength.USER_MIN_PASSWORD)
      },
      passwordConfirm: {
        required,
        maxLength: maxLength(FieldsLength.USER_MAX_PASSWORD),
        minLength: minLength(FieldsLength.USER_MIN_PASSWORD),
        sameAs: sameAs('password')
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

      validateForm() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.restorePassword();
        }
      },

      restorePassword() {
        this.sending = true;
        const form = {
          password: this.password,
          passwordConfirm: this.passwordConfirm
        };
        this.$http.put(`${Url.RESTORE_PASSWORD}/${this.$route.params.uuid}`, JSON.stringify(form))
          .then(() => {
            this.passwordRestored = true;
            this.sending = false;
            this.firstPage = false;
            this.clearForm();
            this.hasError = false;
            this.successfulPage = true;

            this.$v.$reset();
          }, response => {
            this.hasError = true;
            this.sending = false;
            this.errorMessage = response.body.errors[0];
          });
      },

      clearForm() {
        this.password = null;
        this.passwordConfirm = null;
      }
    }
  };
</script>

<style scoped>
  .page-container {
    max-width: 600px;
    margin: 8% auto 0;
  }
</style>
