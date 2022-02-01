<template>
  <div>
    <form
      novalidate
      @submit.prevent="validateUser"
    >
      <md-card>
        <md-card-content>
          <md-field>
            <label>Login</label>
            <md-input
              :value="form.login"
              readonly
            />
          </md-field>

          <md-field>
            <label>Email</label>
            <md-input
              :value="form.email"
              readonly
            />
            <md-button
              class="md-primary left-btn"
              @click="changeEmail = !changeEmail"
            >
              Change email
            </md-button>
          </md-field>

          <email-change-component v-if="changeEmail" />

          <md-field>
            <md-input
              :value="'Password'"
              readonly
            />
            <md-button
              class="md-primary left-btn"
              @click="changePassword = !changePassword"
            >
              Change password
            </md-button>
          </md-field>

          <password-change-component v-if="changePassword" />

          <h3>Person information</h3>

          <md-field :class="getValidationClass('name')">
            <label>Name</label>
            <md-input
              name="first-name"
              v-model="form.name"
              :disabled="sending"
              :maxlength="fieldsLength.USER_NAME"
            />
            <span
              class="md-error"
              v-if="!$v.form.name.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_NAME)}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('surname')">
            <label>Surname*</label>
            <md-input
              name="surname"
              v-model="form.surname"
              :disabled="sending"
              :maxlength="fieldsLength.USER_SURNAME"
            />
            <span
              class="md-error"
              v-if="!$v.form.surname.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.form.surname.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_SURNAME)}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('patronymic')">
            <label>Patronymic</label>
            <md-input
              name="patronymic"
              v-model="form.patronymic"
              :disabled="sending"
              :maxlength="fieldsLength.USER_PATRONYMIC"
            />
            <span
              class="md-error"
              v-if="!$v.form.patronymic.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_PATRONYMIC)}}
            </span>
          </md-field>

          <md-datepicker
            name="bornDate"
            id="bornDate"
            v-model="form.bornDate"
            :disabled="sending"
          >
            <label>Birthday</label>
          </md-datepicker>

          <h3>Address</h3>

          <md-field :class="getValidationClass('town')">
            <label>City</label>
            <md-input
              name="town"
              v-model="form.town"
              :disabled="sending"
              :maxlength="fieldsLength.USER_TOWN"
            />
            <span
              class="md-error"
              v-if="!$v.form.town.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_TOWN)}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('street')">
            <label>Street</label>
            <md-input
              name="street"
              v-model="form.street"
              :disabled="sending"
              :maxlength="fieldsLength.USER_STREET"
            />
            <span
              class="md-error"
              v-if="!$v.form.street.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_STREET)}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('house')">
            <label>Building</label>
            <md-input
              name="house"
              v-model="form.house"
              :disabled="sending"
              :maxlength="fieldsLength.USER_HOUSE"
            />
            <span
              class="md-error"
              v-if="!$v.form.house.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_HOUSE)}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('flat')">
            <label>Apartment</label>
            <md-input
              name="flat"
              v-model="form.flat"
              :disabled="sending"
              :maxlength="fieldsLength.USER_FLAT"
            />
            <span
              class="md-error"
              v-if="!$v.form.flat.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_FLAT)}}
            </span>
          </md-field>
        </md-card-content>

        <md-progress-bar md-mode="indeterminate" v-if="sending" />

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
        :md-active.sync="userUpdated"
      >
        {{messages.USER_UPDATED}}
      </md-snackbar>
    </form>
  </div>
</template>

<script>
  import {required, maxLength} from 'vuelidate/lib/validators';
  import moment from 'moment';

  import PasswordChangeComponent from './components/PasswordChangeComponent';
  import EmailChangeComponent from './components/EmailChangeComponent';
  import {Errors} from '../../../constants/errors';
  import {FieldsLength} from '../../../constants/fieldsLength';
  import {Messages} from '../../../constants/messages';
  import {Url} from '../../../constants/url';

  export default {
    name: 'ProfileContent',

    data: () => ({
      form: {
        login: null,
        email: null,
        name: null,
        surname: null,
        patronymic: null,
        bornDate: null,
        town: null,
        street: null,
        house: null,
        flat: null
      },
      userUpdated: false,
      sending: false,

      changePassword: false,
      changeEmail: false,

      errors: Errors,
      messages: Messages,
      fieldsLength: FieldsLength,

      errorMessage: null,
      hasError: false
    }),

    validations: {
      form: {
        name: {
          maxLength: maxLength(FieldsLength.USER_NAME)
        },
        surname: {
          required,
          maxLength: maxLength(FieldsLength.USER_SURNAME)
        },
        patronymic: {
          maxLength: maxLength(FieldsLength.USER_PATRONYMIC)
        },
        town: {
          maxLength: maxLength(FieldsLength.USER_TOWN)
        },
        street: {
          maxLength: maxLength(FieldsLength.USER_STREET)
        },
        house: {
          maxLength: maxLength(FieldsLength.USER_HOUSE)
        },
        flat: {
          maxLength: maxLength(FieldsLength.USER_FLAT)
        }
      }
    },

    methods: {
      getValidationClass(fieldName) {
        const field = this.$v.form[fieldName];
        if (field) {
          return {
            'md-invalid': field.$invalid && field.$dirty
          };
        }
      },

      validateUser() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.updatePersonDataUser();
        }
      },

      dateIntoString(date) {
        return date ? moment(date).format('YYYY-MM-DD') : null;
      },

      updatePersonDataUser() {
        const form = {
          name: this.form.name,
          surname: this.form.surname,
          patronymic: this.form.patronymic,
          bornDate: this.dateIntoString(this.form.bornDate),
          town: this.form.town,
          street: this.form.street,
          house: this.form.house,
          flat: this.form.flat
        };

        this.$http.put(Url.PROFILE, JSON.stringify(form), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.userUpdated = true;
            this.sending = false;
            this.hasError = false;
          }, response => {
            this.hasError = true;
            this.sending = false;
            this.errorMessage = response.body.errors[0];
          });
      }
    },

    mounted: function() {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles) {
        this.$router.replace('/login');
      }
      if (userRoles.includes('ADMIN')) {
        this.$router.replace('/users');
      }
      if (userRoles.includes('SYS_ADMIN')) {
        this.$router.replace('/clients');
      }

      this.$http.get(Url.PROFILE, {
        headers: {
          Authorization: `Bearer ${localStorage.accessToken}`
        }
      })
        .then(response => {
          this.form.login = response.body.login;
          this.form.email = response.body.email;
          this.form.name = response.body.name;
          this.form.surname = response.body.surname;
          this.form.patronymic = response.body.patronymic;
          this.form.bornDate = response.body.bornDate;
          this.form.town = response.body.town;
          this.form.street = response.body.street;
          this.form.house = response.body.house;
          this.form.flat = response.body.flat;
        }, () => {
          this.hasError = true;
          this.errorMessage = 'Error has occurred';
        });
    },

    components: {PasswordChangeComponent, EmailChangeComponent}
  };
</script>

<style scoped>
  p {
    font-size: 16px;
  }

  .left-btn {
    position: absolute;
    right: -10px;
    top: 5px;
  }
</style>
