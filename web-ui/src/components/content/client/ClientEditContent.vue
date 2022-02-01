<template>
  <div>
    <form novalidate @submit.prevent="validateClient">

      <md-card>
        <md-card-content>
          <md-field :class="getValidationClass('name')">
            <label>Company name</label>
            <md-input
              v-model="name"
              name="name"
              :disabled="sending"
              :maxlength="fieldsLength.CLIENT_NAME"
            />
            <span
              class="md-error"
              v-if="!$v.name.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.name.maxlength"
            >
              {{errors.MAX_LENGTH(fieldsLength.CLIENT_NAME)}}
            </span>
          </md-field>

          <md-field>
            <label>Company type</label>
            <md-select
              v-model="status"
              name="status"
              :disabled="sending"
            >
              <md-option value="PRIVATE">Private</md-option>
              <md-option value="LEGAL">Legal</md-option>
            </md-select>
          </md-field>
        </md-card-content>

        <md-progress-bar md-mode="indeterminate" v-if="sending"/>

        <md-card-actions>
          <md-button
            class="md-primary"
            :disabled="sending"
            to="/clients"
          >
            Back
          </md-button>

          <md-button
            type="submit"
            class="md-primary"
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

      </md-card>

      <md-snackbar
        :md-active.sync="clientUpdated"
      >
        {{messages.CLIENT_UPDATED}}
      </md-snackbar>
    </form>
  </div>
</template>

<script>
  import {maxLength, required} from 'vuelidate/lib/validators';

  import {Errors} from '../../../constants/errors';
  import {FieldsLength} from '../../../constants/fieldsLength';
  import {Messages} from '../../../constants/messages';
  import {Url} from '../../../constants/url';

  export default {
    name: 'ClientEditContent',

    data: () => ({
      errors: Errors,
      fieldsLength: FieldsLength,
      messages: Messages,

      name: null,
      status: null,

      sending: false,
      clientUpdated: false,
      errorMessage: null,
      hasError: false
    }),

    validations: {
      name: {
        required,
        maxLength: maxLength(FieldsLength.CLIENT_NAME)
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

      updateClient() {
        this.sending = true;
        const form = {
          name: this.name,
          status: this.status
        };
        this.$http.put(`${Url.CLIENT}/${this.$route.params.id}`, JSON.stringify(form), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.clientUpdated = true;
            this.sending = false;
            this.hasError = false;
            this.$v.$reset();
          }, response => {
            this.hasError = true;
            this.sending = false;
            this.errorMessage = response.body.errors[0];
          });
      },

      validateClient() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.updateClient();
        }
      }
    },

    mounted: function () {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('SYS_ADMIN')) {
        this.$router.replace('/');
      }

      this.$http.get(`${Url.CLIENT}/${this.$route.params.id}`, {
        headers: {
          Authorization: `Bearer ${localStorage.accessToken}`
        }
      })
        .then(response => {
          this.name = response.body.name;
          this.status = response.body.status;
        }, response => {
          this.hasError = true;
          this.errorMessage = response.body.errors[0];
        });
    }
  };
</script>

<style scoped>

</style>
