<template>
  <div>
    <form novalidate @submit.prevent="validateClient">

      <md-card>
        <md-card-content>
          <h2>Company info</h2>

          <md-field :class="getValidationClass('name')">
            <label>Company name*</label>
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

          <md-field :class="getValidationClass('status')">
            <label>Company type*</label>
            <md-select
              v-model="status"
              name="status"
              :disabled="sending"
            >
              <md-option value="PRIVATE">Private</md-option>
              <md-option value="LEGAL">Legal</md-option>
            </md-select>
            <span
              class="md-error"
              v-if="!$v.status.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
          </md-field>
        </md-card-content>

        <md-card-content>
          <md-divider></md-divider>
          <h2>Administrator info</h2>

          <md-field :class="getValidationClassAdminInfo('adminName')">
            <label>Name</label>
            <md-input
              name="adminName"
              v-model="adminInfo.name"
              :disabled="sending"
              :maxlength="fieldsLength.USER_NAME"
            />
            <span
              class="md-error"
              v-if="!$v.adminInfo.name.alpha"
            >
              {{errors.NO_DIGITS}}
            </span>
          </md-field>

          <md-field :class="getValidationClassAdminInfo('surname')">
            <label>Surname*</label>
            <md-input
              name="surname"
              v-model="adminInfo.surname"
              :disabled="sending"
              :maxlength="fieldsLength.USER_SURNAME"
            />
            <span
              class="md-error"
              v-if="!$v.adminInfo.surname.required"
            >
               {{errors.FIELD_IS_REQUIRED}}
             </span>
            <span
              class="md-error"
              v-if="!$v.adminInfo.surname.alpha"
            >
              {{errors.NO_DIGITS}}
            </span>
          </md-field>

          <md-datepicker
            :class="getValidationClassAdminInfo('bornDate')"
            name="bornDate"
            v-model="adminInfo.bornDate"
            :disabled="sending"
          >
            <label>Birthday</label>
            <span
              class="md-error"
              v-if="!$v.adminInfo.bornDate.maxValue"
            >
              {{errors.MAX_DATE(moment(new Date()).format('YYYY-MM-DD'))}}
            </span>

          </md-datepicker>

          <md-field :class="getValidationClassAdminInfo('email')">
            <label>Email*</label>
            <md-input
              name="email"
              v-model="adminInfo.email"
              :disabled="sending"
              :maxlength="fieldsLength.USER_EMAIL"
            />
            <span
              class="md-error"
              v-if="!$v.adminInfo.email.required"
            >
               {{errors.FIELD_IS_REQUIRED}}
             </span>
            <span
              class="md-error"
              v-if="!$v.adminInfo.email.email"
            >
                {{errors.INVALID_EMAIL}}
              </span>
          </md-field>

        </md-card-content>

        <md-card-content>
          <md-divider></md-divider>
          <md-field :class="getValidationClassAdminInfo('login')">
            <label>Login*</label>
            <md-input
              v-model="adminInfo.login"
              name="login"
              :disabled="sending"
              :minlength="fieldsLength.USER_MIN_LOGIN"
              :maxlength="fieldsLength.USER_MAX_LOGIN"
            />
            <span
              class="md-error"
              v-if="!$v.adminInfo.login.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.adminInfo.login.maxlength"
            >
              {{errors.MIN_LENGTH(fieldsLength.USER_MIN_LOGIN)}}
            </span>
          </md-field>

          <md-field :class="getValidationClassAdminInfo('password')">
            <label>Password*</label>
            <md-input
              type="password"
              name="password"
              autocomplete="password"
              v-model="adminInfo.password"
              :disabled="sending"
              :minLength="fieldsLength.USER_MIN_PASSWORD"
              :maxlength="fieldsLength.USER_MAX_PASSWORD"
            />
            <span
              class="md-error"
              v-if="!$v.adminInfo.password.required"
            >
               {{errors.FIELD_IS_REQUIRED}}
           </span>
            <span
              class="md-error"
              v-else-if="!$v.adminInfo.password.minLength"
            >
            {{errors.MIN_LENGTH(fieldsLength.USER_MIN_PASSWORD)}}
           </span>
            <span
              class="md-error"
              v-else-if="!$v.adminInfo.password.maxLength"
            >
              {{errors.MAX_LENGTH(FieldsLength.USER_MAX_PASSWORD)}}
            </span>
          </md-field>

          <md-field :class="getValidationClassAdminInfo('passwordConfirm')">
            <label>Confirm Password*</label>
            <md-input
              type="password"
              id="passwordConfirm"
              name="passwordConfirm"
              autocomplete="passwordConfirm"
              v-model="adminInfo.passwordConfirm"
              :disabled="sending"
              :minLength="fieldsLength.USER_MIN_PASSWORD"
              :maxlength="fieldsLength.USER_MAX_PASSWORD"
            />
            <span
              class="md-error"
              v-if="!$v.adminInfo.passwordConfirm.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.adminInfo.passwordConfirm.sameAs"
            >
              {{errors.NOT_CONFIRMED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.adminInfo.passwordConfirm.minLength"
            >
              {{errors.MIN_LENGTH(fieldsLength.USER_MIN_PASSWORD)}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.adminInfo.passwordConfirm.maxLength"
            >
              {{errors.MAX_LENGTH(FieldsLength.USER_MAX_PASSWORD)}}
            </span>
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
        :md-active.sync="clientSaved"
      >
        {{messages.CLIENT_SAVED}}
      </md-snackbar>
    </form>
  </div>
</template>

<script>
  import {alpha, email, maxLength, maxValue, minLength, required, sameAs} from 'vuelidate/lib/validators';
  import moment from 'moment';

  import {Errors} from '../../../constants/errors';
  import {FieldsLength} from '../../../constants/fieldsLength';
  import {Messages} from '../../../constants/messages';
  import {Url} from '../../../constants/url';



  export default {
    name: 'ClientAddContent',

    data: () => ({
      errors: Errors,
      fieldsLength: FieldsLength,
      messages: Messages,

      name: null,
      status: '',
      adminInfo: {
        name: null,
        surname: null,
        bornDate: null,
        email: null,
        login: null,
        password: null,
        passwordConfirm: null
      },

      sending: false,
      clientSaved: false,
      hasError: false,
      errorMessage: null,

      moment: moment
    }),

    validations: {
      name: {
        required,
        maxLength: maxLength(FieldsLength.CLIENT_NAME)
      },
      status: {
        required
      },
      adminInfo: {
        name: {
          alpha,
          maxLength: maxLength(FieldsLength.USER_NAME)
        },
        surname: {
          required,
          alpha,
          maxLength: maxLength(FieldsLength.USER_SURNAME)
        },
        email: {
          required,
          email,
          maxLength: maxLength(FieldsLength.USER_EMAIL)
        },
        login: {
          required,
          minLength: minLength(FieldsLength.USER_MIN_LOGIN),
          maxLength: maxLength(FieldsLength.USER_MAX_LOGIN)
        },
        password: {
          required,
          minLength: minLength(FieldsLength.USER_MIN_PASSWORD),
          maxLength: maxLength(FieldsLength.USER_MAX_PASSWORD)
        },
        passwordConfirm: {
          required,
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
        const field = this.$v[fieldName];
        if (field) {
          return {
            'md-invalid': field.$invalid && field.$dirty
          };
        }
      },

      getValidationClassAdminInfo(fieldName) {
        const field = this.$v.adminInfo[fieldName];
        if (field) {
          return {
            'md-invalid': field.$invalid && field.$dirty
          };
        }
      },

      validateClient() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          this.saveClient();
        }
      },

      saveClient() {
        this.sending = true;
        const form = {
          name: this.name,
          status: this.status,
          adminInfo: this.adminInfo
        };
        this.$http.post(Url.CLIENT, JSON.stringify(form), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.clientSaved = true;
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
        this.name = null;
        this.status = '';
        this.adminInfo.name = null;
        this.adminInfo.surname = null;
        this.adminInfo.bornDate = null;
        this.adminInfo.email = null;
        this.adminInfo.login = null;
        this.adminInfo.password = null;
        this.adminInfo.passwordConfirm = null;
      }
    },

    mounted: function () {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('SYS_ADMIN')) {
        this.$router.replace('/');
      }
    }
  };
</script>
