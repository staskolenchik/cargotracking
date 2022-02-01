<template>
  <div>
    <form
      novalidate
      @submit.prevent="validateUser"
    >
      <md-card>
        <md-card-content>
          <md-field :class="getValidationClass('login')">
            <label>Login*</label>
            <md-input
              name="login"
              v-model="form.login"
              :disabled="sending"
              :maxlength="fieldsLength.USER_LOGIN"
            />
            <span
              class="md-error"
              v-if="!$v.form.login.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.form.login.minLength"
            >
              {{errors.MIN_LENGTH(fieldsLength.USER_MIN_LOGIN)}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.form.login.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_MAX_LOGIN)}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('email')">
            <label>Email*</label>
            <md-input
              name="email"
              v-model="form.email"
              :disabled="sending"
              :maxlength="fieldsLength.USER_EMAIL"
            />
            <span
              class="md-error"
              v-if="!$v.form.email.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.form.email.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_EMAIL)}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.form.email.email"
            >
              {{errors.INVALID_EMAIL}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('password')">
            <label>Change password</label>
            <md-input
              type="password"
              name="password"
              v-model="form.password"
              :disabled="sending"
              :maxlength="fieldsLength.USER_MAX_PASSWORD"
            />
            <span
              class="md-error"
              v-if="!$v.form.password.minLength"
            >
              {{errors.MIN_LENGTH(fieldsLength.USER_MIN_PASSWORD)}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.form.password.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_MAX_PASSWORD)}}
            </span>
          </md-field>

          <md-field :class="getValidationClass('passwordConfirm')">
            <label>Confirm Password</label>
            <md-input
              type="password"
              name="passwordConfirm"
              v-model="form.passwordConfirm"
              :disabled="sending"
              :maxlength="fieldsLength.USER_MAX_PASSWORD"
            />
            <span
              class="md-error"
              v-if="!$v.form.passwordConfirm.minLength"
            >
              {{errors.MIN_LENGTH(fieldsLength.USER_MIN_PASSWORD)}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.form.passwordConfirm.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.USER_MAX_PASSWORD)}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.form.passwordConfirm.sameAs"
            >
              {{errors.NOT_CONFIRMED}}
            </span>
          </md-field>

          <md-field>
            <label>Role*</label>
            <md-select
              v-model="form.userRoles"
              :disabled="sending"
              multiple
            >
              <md-option value="DISPATCHER">Dispatcher</md-option>
              <md-option value="MANAGER">Manager</md-option>
              <md-option value="DRIVER">Driver</md-option>
              <md-option value="COMPANY_OWNER">Company Owner</md-option>
            </md-select>
          </md-field>

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
            :class="getValidationClass('bornDate')"
            name="bornDate"
            v-model="form.bornDate"
            :disabled="sending"
          >
            <label>Birthday</label>
            <span
              class="md-error"
              v-if="!$v.form.bornDate.maxValue"
            >
              {{errors.MAX_DATE(moment(new Date()).format('YYYY-MM-DD'))}}
            </span>
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

        <md-progress-bar md-mode="indeterminate" v-if="sending"/>

        <md-card-actions>
          <md-button
            class="md-accent"
            :disabled="sending"
            to="/users"
          >
            Back
          </md-button>

          <md-button
            type="submit"
            class="md-primary"
            :disabled="sending"
          >
            Edit
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
  import {email, maxLength, maxValue, minLength, required, sameAs} from 'vuelidate/lib/validators';
  import moment from 'moment';

  import {Errors} from '../../../constants/errors';
  import {FieldsLength} from '../../../constants/fieldsLength';
  import {Messages} from '../../../constants/messages';
  import {Url} from '../../../constants/url';

  export default {
    name: 'EditUser',

    data: () => ({
      form: {
        name: null,
        surname: null,
        patronymic: null,
        email: null,
        userRoles: [],
        bornDate: null,
        login: null,
        town: null,
        street: null,
        house: null,
        flat: null,
        password: null,
        passwordConfirm: null,
        isChangePassword: false
      },

      password: null,
      passwordConfirm: null,

      userUpdated: false,
      sending: false,

      errors: Errors,
      messages: Messages,
      fieldsLength: FieldsLength,
      errorMessage: null,
      hasError: false,

      moment: moment
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
        email: {
          required,
          email,
          maxLength: maxLength(FieldsLength.USER_EMAIL)
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
        },
        login: {
          required,
          minLength: minLength(FieldsLength.USER_MIN_LOGIN),
          maxLength: maxLength(FieldsLength.USER_MAX_LOGIN)
        },
        password: {
          minLength: minLength(FieldsLength.USER_MIN_PASSWORD),
          maxLength: maxLength(FieldsLength.USER_MAX_PASSWORD)
        },
        passwordConfirm: {
          minLength: minLength(FieldsLength.USER_MIN_PASSWORD),
          maxLength: maxLength(FieldsLength.USER_MAX_PASSWORD),
          sameAs: sameAs('password')
        },
        bornDate: {
          maxValue: maxValue(new Date())
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

      dateIntoString(date) {
        return date ? moment(date).format('YYYY-MM-DD') : null;
      },

      validateUser() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.updateUser();
        }
      },

      updateUser() {
        if (this.form.password !== null && this.form.password !== undefined && this.form.password !== '') {
          this.form.isChangePassword = true;
        } else {
          this.form.password = this.password;
          this.form.passwordConfirm = this.passwordConfirm;
        }

        const form = {
          name: this.form.name,
          surname: this.form.surname,
          patronymic: this.form.patronymic,
          email: this.form.email,
          bornDate: this.dateIntoString(this.form.bornDate),
          town: this.form.town,
          street: this.form.street,
          house: this.form.house,
          flat: this.form.flat,
          login: this.form.login,
          password: this.form.password,
          passwordConfirm: this.form.passwordConfirm,
          isChangePassword: this.form.isChangePassword,
          userRoles: this.form.userRoles
        };

        this.$http.put(`${Url.USER}/${this.$route.params.id}`, JSON.stringify(form), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.userUpdated = true;
            this.sending = false;
            this.hasError = false;
            this.form.isChangePassword = false;
            this.form.password = null;
            this.form.passwordConfirm = null;
          })
          .catch((response) => {
            this.form.isChangePassword = false;
            this.form.password = null;
            this.form.passwordConfirm = null;
            this.errorMessage = response.body.errors[0];
          });
      }
    },

    mounted: function () {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('ADMIN')) {
        this.$router.replace('/');
      }

      this.$http.get(`${Url.USER}/${this.$route.params.id}`, {
        headers: {
          Authorization: `Bearer ${localStorage.accessToken}`
        }
      })
        .then(response => {
          this.form.name = response.body.name;
          this.form.surname = response.body.surname;
          this.form.patronymic = response.body.patronymic;
          this.form.bornDate = new Date(response.body.bornDate);
          this.form.town = response.body.town;
          this.form.street = response.body.street;
          this.form.house = response.body.house;
          this.form.flat = response.body.flat;
          this.form.email = response.body.email;
          this.form.login = response.body.login;
          for (let i = 0; i < response.body.userRoles.length; i++) {
            this.form.userRoles.push(response.body.userRoles[i].id.role);
          }
          this.password = 'password';
          this.passwordConfirm = 'password';
        }, response => {
          this.hasError = true;
          this.errorMessage = response.body.errors[0];
        });
    }
  };
</script>

<style scoped>

</style>
