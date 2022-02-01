<template>
  <div class="page-container">
    <form novalidate @submit.prevent="validateForm">
      <md-card>
        <md-card-content>
          <md-field :class="getValidationClass('recipient')">
            <label>Email</label>
            <md-input
              v-model="recipient"
              name="recipient"
              :maxlength="fieldsLength.USER_EMAIL"
              :disabled="sending"
            />
            <span
              class="md-error"
              v-if="!$v.recipient.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.recipient.email"
            >
              {{errors.INVALID_EMAIL}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.recipient.fieldsLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_EMAIL)}}
            </span>
          </md-field>
        </md-card-content>
        <md-progress-bar md-mode="indeterminate" v-if="sending"/>
        <md-card-actions>
          <md-button
            class="md-accent"
            :disabled="sending"
            to="/login"
          >
            Cancel
          </md-button>
          <md-button
            class="md-primary"
            type="submit"
            :disabled="sending"
          >
            Send
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
        :md-active.sync="emailSent"
      >
        {{messages.REPAIR_ACCOUNT}}
      </md-snackbar>
    </form>
  </div>
</template>

<script>
  import {email, maxLength, required} from 'vuelidate/lib/validators';

  import {FieldsLength} from '../constants/fieldsLength';
  import {Errors} from '../constants/errors';
  import {Messages} from '../constants/messages';
  import {Url} from '../constants/url';

  export default {
    name: 'RepairAccount',

    data: () => ({
      errors: Errors,
      fieldsLength: FieldsLength,
      messages: Messages,

      recipient: null,

      sending: false,
      emailSent: false,
      hasError: false,
      errorMessage: null
    }),

    validations: {
      recipient: {
        required,
        email,
        maxLength: maxLength(FieldsLength.USER_EMAIL)
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
          this.sendEmail();
        }
      },

      sendEmail() {
        this.sending = true;
        const form = {
          recipient: this.recipient,
          text: `Hello dear user!\nIn order to reset your password follow the link bellow:\n${Url.HOST_ADDRESS}#/restore/%s`
        };
        this.$http.post(Url.REPAIR, JSON.stringify(form))
          .then(() => {
            this.emailSent = true;
            this.sending = false;
            this.clearForm();
            this.hasError = false;
            this.$v.$reset();
          }, response => {
            this.hasError = true;
            this.sending = false;
            this.errorMessage = response.body.errors[0];
          });
      },

      clearForm() {
        this.recipient = null;
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
