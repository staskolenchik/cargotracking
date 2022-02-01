<template>
  <div>
    <form
      novalidate
      @submit.prevent="validatePasswordChangeForm"
    >
      <md-card>
        <md-card-content>
          <md-field :class="getValidationClass('oldPassword')">
            <label>Old password</label>
            <md-input
              type="password"
              name="password"
              v-model="oldPassword"
              :maxlength="fieldsLength.USER_MAX_PASSWORD"
              :disabled="sending"
            />
            <span
              class="md-error"
              v-if="!$v.oldPassword.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.oldPassword.minLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_MAX_PASSWORD)}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.oldPassword.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_MAX_PASSWORD)}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('newPassword')">
            <label>New password</label>
            <md-input
              type="password"
              name="password"
              v-model="newPassword"
              :maxlength="fieldsLength.USER_MAX_PASSWORD"
              :disabled="sending"
            />
            <span
              class="md-error"
              v-if="!$v.newPassword.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.newPassword.minLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_MIN_PASSWORD)}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.newPassword.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_MAX_PASSWORD)}}
            </span>
          </md-field>
        </md-card-content>

        <md-progress-bar md-mode="indeterminate" v-if="sending" />

        <md-card-actions>
          <md-button
            type="submit"
            class="md-primary"
            :disabled="sending"
          >
            Save password
          </md-button>
        </md-card-actions>

        <md-chip
          class="md-accent md-layout md-alignment-center"
          v-if="hasError"
        >
          {{errorMessage}}
        </md-chip>
      </md-card>
      <md-snackbar
        :md-active.sync="passwordUpdated"
      >
        {{messages.PASSWORD_UPDATED}}
      </md-snackbar>
    </form>
  </div>
</template>

<script>
  import {required, maxLength, minLength} from 'vuelidate/lib/validators';

  import {Errors} from '../../../../constants/errors';
  import {FieldsLength} from '../../../../constants/fieldsLength';
  import {Messages} from '../../../../constants/messages';
  import {Url} from '../../../../constants/url';

  export default {
    name: 'PasswordChangeComponent',

    data: () => ({
      oldPassword: null,
      newPassword: null,
      passwordUpdated: false,
      sending: false,

      errors: Errors,
      messages: Messages,
      fieldsLength: FieldsLength,

      errorMessage: null,
      hasError: false
    }),

    validations: {
      oldPassword: {
        required,
        maxLength: maxLength(FieldsLength.USER_MAX_PASSWORD),
        minLength: minLength(FieldsLength.USER_MIN_PASSWORD)
      },
      newPassword: {
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

      validatePasswordChangeForm() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.updatePassword();
        }
      },

      updatePassword() {
        this.sending = true;

        const form = {
          oldPassword: this.oldPassword,
          newPassword: this.newPassword
        };
        this.clearForm();

        this.$http.put(Url.CHANGE_PASSWORD, JSON.stringify(form), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.passwordUpdated = true;
            this.sending = false;
            this.hasError = false;
          }, response => {
            this.hasError = true;
            this.sending = false;
            this.errorMessage = response.body.errors[0];
          });
        this.$v.$reset();
      },

      clearForm() {
        this.oldPassword = null;
        this.newPassword = null;
      }
    }
  };
</script>

<style scoped>

</style>
