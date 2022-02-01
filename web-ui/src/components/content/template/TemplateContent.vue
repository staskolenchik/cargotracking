<template>
  <div>
    <form novalidate @submit.prevent="validateTemplate">
      <md-card>
        <md-card-header class="md-title">Template</md-card-header>

        <md-card-content>
          <span class="md-caption">
            Note: this template will be used to generate daily birthday congratulation email.
            Please, be conscious and attentive! Remember, your message purpose is to bring joy and happiness!
          </span>
          <md-field
            :class="getValidationClass('templateMessage')"
          >
            <label>Fill this field with template text</label>
            <md-textarea
              v-model="templateMessage"
              :disabled="sending"

            />
            <span
              class="md-error"
              v-if="!$v.templateMessage.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.templateMessage.required"
            >
              {{errors.MAX_LENGTH(fieldsLength.TEMPLATE_MAX_LENGTH)}}
            </span>
          </md-field>
          <md-chip
            class="md-primary"
            v-for="chip in templates"
            :key="chip" md-clickable
            @click="addTemplateText(chip)"
          >
            {{ chip }}
          </md-chip>

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
              :disabled="sending"
              class="md-primary"
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
        </md-card-content>

        <md-progress-bar md-mode="indeterminate" v-if="sending"/>
      </md-card>

      <md-snackbar
        :md-active.sync="templateSaved"
      >
        {{messages.TEMPLATE_SAVED}}
      </md-snackbar>
    </form>
  </div>
</template>

<script>
  import {maxLength, required} from 'vuelidate/lib/validators';

  import {Messages} from '../../../constants/messages';
  import {Errors} from '../../../constants/errors';
  import {FieldsLength} from '../../../constants/fieldsLength';
  import {Url} from '../../../constants/url';

  export default {
    name: 'TemplateContent',

    data: () => ({
      templates: [
        'Full Name',
        'Full Address',
        'Email'
      ],
      templateMessage: null,
      errors: Errors,
      messages: Messages,

      sending: false,
      hasError: false,
      errorMessage: null,
      templateSaved: false
    }),

    validations: {
      templateMessage: {
        required,
        maxLength: maxLength(FieldsLength.TEMPLATE_MAX_LENGTH)
      }
    },

    methods: {
      addTemplateText(chip) {
        if (chip === 'Full Name') {
          this.templateMessage = (this.templateMessage ? this.templateMessage : '') + '<fullName; separator=" ">';
        } else if (chip === 'Full Address') {
          this.templateMessage = (this.templateMessage ? this.templateMessage : '') + '<fullAddress; separator=" ">';
        } else if (chip === 'Email') {
          this.templateMessage = (this.templateMessage ? this.templateMessage : '') + '<email>';
        }
      },

      saveTemplate() {
        this.sending = true;
        this.$http.post(Url.TEMPLATE, JSON.stringify(this.templateMessage), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.templateSaved = true;
            this.sending = false;
            this.hasError = false;
            this.$v.$reset();
          }, response => {
            this.sending = false;
            this.hasError = true;
            this.errorMessage = response.body.errors[0];
          });
      },

      validateTemplate() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.saveTemplate();
        }
      },

      getValidationClass(fieldName) {
        const field = this.$v[fieldName];
        if (field) {
          return {
            'md-invalid': field.$invalid && field.$dirty
          };
        }
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
