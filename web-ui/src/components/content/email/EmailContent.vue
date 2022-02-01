<template>
  <div>
    <form novalidate @submit.prevent="validateEmail">
      <md-card>
        <md-card-content>

          <md-field :class="getValidationClass('recipient')">
            <label>Recipient*</label>
            <md-input
              v-model="recipient"
              name="recipient"
              :disabled="sending"
              :maxlength="fieldsLength.USER_EMAIL"
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
          </md-field>

          <md-field :class="getValidationClass('subject')">
            <label>Subject</label>
            <md-input
              v-model="subject"
              name="subject"
              :disabled="sending"
              :maxlength="fieldsLength.EMAIL_SUBJECT"
            />
          </md-field>

          <md-field :class="getValidationClass('text')">
            <label>Text*</label>
            <md-textarea
              v-model="text"
              name="text"
              :disabled="sending"
              :maxlength="fieldsLength.EMAIL_TEXT"
            />
            <span
              class="md-error"
              v-if="!$v.text.required"
              >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
          </md-field>
        </md-card-content>

        <md-progress-bar md-mode="indeterminate" v-if="sending"/>

        <md-card-actions>
          <md-button
            class="md-accent"
            :disabled="sending"
            to="/"
          >
            Back
          </md-button>

          <md-button
            type="submit"
            class="md-primary"
            :disabled="sending"
          >
            <md-icon>send</md-icon>
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
        {{messages.MESSAGE_SENT}}
      </md-snackbar>
    </form>
  </div>

</template>

<script>
  import {maxLength, required, email} from 'vuelidate/lib/validators';

  import {Errors} from '../../../constants/errors';
  import {FieldsLength} from '../../../constants/fieldsLength';
  import {Messages} from '../../../constants/messages';
  import {Url} from '../../../constants/url';

  export default {
    name: 'EmailContent',

    data: () => ({
      errors: Errors,
      fieldsLength: FieldsLength,
      messages: Messages,

      recipient: null,
      subject: null,
      text: null,

      sending: false,
      emailSent: false,
      hasError: false,
      errorMessage: null
    }),

    validations: {
      recipient: {
        email,
        required,
        maxLength: maxLength(FieldsLength.USER_EMAIL)
      },
      subject: {
        maxLength: maxLength(FieldsLength.EMAIL_SUBJECT)
      },
      text: {
        required,
        maxLength: maxLength(FieldsLength.EMAIL_TEXT)
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

      validateEmail() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.sendEmail();
        }
      },

      sendEmail() {
        this.sending = true;
        const form = {
          recipient: this.recipient,
          subject: this.subject,
          text: this.text
        };
        this.$http.post(Url.EMAIL, JSON.stringify(form), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.emailSent = true;
            this.clearForm();
            this.sending = false;
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
        this.subject = null;
        this.text = null;
      }
    },

    mounted: function() {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('ADMIN')) {
        this.$router.replace('/');
      }
    }
  };
</script>

<style scoped>

</style>
