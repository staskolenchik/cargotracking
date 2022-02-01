<template>
  <div>
    <form
      novalidate
      @submit.prevent="validateEmailChangeForm"
    >
      <md-card>
        <md-card-content>
          <md-field :class="getValidationClass('password')">
            <label>Type your password</label>
            <md-input
              type="password"
              name="password"
              v-model="password"
              :maxlength="fieldsLength.USER_MAX_PASSWORD"
              :disabled="sending"
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
              {{errors.MAX_LENGTH(fieldsLength.USER_MAX_PASSWORD)}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.password.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_MAX_PASSWORD)}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('email')">
            <label>New email</label>
            <md-input
              name="email"
              v-model="email"
              :maxlength="fieldsLength.USER_EMAIL"
              :disabled="sending"
            />
            <span
              class="md-error"
              v-if="!$v.email.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.email.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_MAX_PASSWORD)}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.email.email"
            >
              {{errors.INVALID_EMAIL}}
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
            Save email
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
        :md-active.sync="emailUpdated"
      >
        {{messages.EMAIL_UPDATED}}
      </md-snackbar>
    </form>
  </div>
</template>

<script>
  import {required, maxLength, minLength, email} from 'vuelidate/lib/validators';

  import {Errors} from '../../../../constants/errors';
  import {FieldsLength} from '../../../../constants/fieldsLength';
  import {Messages} from '../../../../constants/messages';
  import {Url} from '../../../../constants/url';

  export default {
    name: 'EmailChangeComponent',

    data: () => ({
      email: null,
      password: null,
      emailUpdated: false,
      sending: false,

      errors: Errors,
      messages: Messages,
      fieldsLength: FieldsLength,

      errorMessage: null,
      hasError: false
    }),

    validations: {
      email: {
        required,
        email,
        maxLength: maxLength(FieldsLength.USER_EMAIL)
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

      validateEmailChangeForm() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.updateEmail();
        }
      },

      updateEmail() {
        this.sending = true;

        const form = {
          recipient: this.email,
          text: `Hello dear user!\nIn order to update your email follow the link bellow:\n${Url.HOST_ADDRESS}#/confirm-change-email/%s`,
          password: this.password
        };

        this.$http.put(Url.CHANGE_EMAIL, JSON.stringify(form), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.emailUpdated = true;
            this.sending = false;
            this.hasError = false;
            this.clearForm();
          }, response => {
            this.hasError = true;
            this.sending = false;
            this.errorMessage = response.body.errors[0];
          });
        this.$v.$reset();
      },

      clearForm() {
        this.password = null;
        this.email = null;
      }
    }
  };
</script>

<style scoped>

</style>
