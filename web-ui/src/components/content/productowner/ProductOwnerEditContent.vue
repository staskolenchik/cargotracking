<template>
  <div>
    <form novalidate @submit.prevent="validateProductOwner">

      <md-card>
        <md-card-content>
          <md-field :class="getValidationClass('name')">
            <label>Name</label>
            <md-input
              v-model="name"
              name="name"
              :disabled="sending"
              :maxlength="fieldsLength.PRODUCT_OWNER_NAME"
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
              {{errors.MAX_LENGTH(fieldsLength.PRODUCT_OWNER_NAME)}}
            </span>
          </md-field>

          <md-autocomplete
            id="address"
            :class="getValidationClass('address')"
            v-model="address"
            :md-options="addresses"
            :disabled="sending"
            :maxlength="fieldsLength.PRODUCT_OWNER_ADDRESS"
            md-dense
            @input.native="findPlaces"
          >
            <label>Address</label>
            <span
              class="md-error"
              v-if="!$v.address.required"
            >
              {{errors.FIELD_IS_REQUIRED}}
            </span>
            <span
              class="md-error"
              v-else-if="!$v.address.maxLength"
            >
              {{errors.MAX_LENGTH(fieldsLength.PRODUCT_OWNER_ADDRESS)}}
            </span>
            <span
              class="md-error"
              v-else-if="!addressValid"
            >
              {{errors.INVALID_ADDRESS}}
            </span>
          </md-autocomplete>
        </md-card-content>

        <md-progress-bar md-mode="indeterminate" v-if="sending"/>

        <md-card-actions>
          <md-button
            :disabled="sending"
            to="/product-owners"
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

        <div id="map"></div>
      </md-card>

      <md-snackbar
        :md-active.sync="productOwnerUpdated"
      >
        {{messages.PRODUCT_OWNER_UPDATED}}
      </md-snackbar>
    </form>
  </div>
</template>

<script>
  import {required, maxLength} from 'vuelidate/lib/validators';

  import {Errors} from '../../../constants/errors';
  import {FieldsLength} from '../../../constants/fieldsLength';
  import {Messages} from '../../../constants/messages';
  import {Url} from '../../../constants/url';

  export default {
    name: 'ProductOwnerEditContent',

    data: () => ({
      errors: Errors,
      fieldsLength: FieldsLength,
      messages: Messages,

      name: null,
      address: null,
      addresses: [],

      sending: false,
      productOwnerUpdated: false,
      hasError: false,
      errorMessage: null,

      service: null,
      map: null,
      addressValid: true
    }),

    validations: {
      name: {
        required,
        maxLength: maxLength(FieldsLength.PRODUCT_OWNER_NAME)
      },
      address: {
        required,
        maxLength: maxLength(FieldsLength.PRODUCT_OWNER_ADDRESS)
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

      findPlaces() {
        const request = {
          query: this.address,
          fields: ['name', 'geometry']
        };

        /* eslint-disable no-undef */
        this.service.findPlaceFromQuery(request, (results, status) => {
          this.addresses = [];
          if (status === google.maps.places.PlacesServiceStatus.OK) {
            this.map.setCenter(results[0].geometry.location);
            this.map.setZoom(12);
            for (let i = 0; i < results.length; i++) {
              this.addresses.push(results[i].name);
            }
          }
        });
        /* eslint-enable no-undef */
      },

      validateProductOwner() {
        this.$v.$touch();
        if (!this.$v.$invalid) {
          const request = {
            query: this.address,
            fields: ['name']
          };

          /* eslint-disable no-undef */
          this.service.findPlaceFromQuery(request, (results, status) => {
            if (status !== google.maps.places.PlacesServiceStatus.OK || results.length !== 1) {
              this.addressValid = false;
              document.getElementById('address').classList.add('md-invalid');
            } else {
              document.getElementById('address').classList.remove('md-invalid');
              this.updateProductOwner();
              this.addressValid = true;
            }
          });
          /* eslint-enable no-undef */
        }
      },

      updateProductOwner() {
        this.sending = true;
        const form = {
          name: this.name,
          address: this.address
        };
        this.$http.put(`${Url.PRODUCT_OWNER}/${this.$route.params.id}`, JSON.stringify(form), {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.productOwnerUpdated = true;
            this.sending = false;
            this.hasError = false;
            this.$v.$reset();
          }, response => {
            this.hasError = true;
            this.sending = false;
            this.errorMessage = response.body.errors[0];
          });
      }
    },

    mounted: function() {
      const userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!userRoles || !userRoles.includes('ADMIN')) {
        this.$router.replace('/');
      }

      /* eslint-disable no-undef */
      const map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 53.540, lng: 27.300},
        zoom: 8
      });
      this.map = map;
      this.service = new google.maps.places.PlacesService(map);
      /* eslint-enable no-undef */

      this.$http.get(`${Url.PRODUCT_OWNER}/${this.$route.params.id}`, {
        headers: {
          Authorization: `Bearer ${localStorage.accessToken}`
        }
      })
        .then(response => {
          this.name = response.body.name;
          this.address = response.body.address;

          const request = {
            query: this.address,
            fields: ['geometry']
          };

          /* eslint-disable no-undef */
          this.service.findPlaceFromQuery(request, (results, status) => {
            if (status === google.maps.places.PlacesServiceStatus.OK) {
              this.map.setCenter(results[0].geometry.location);
              this.map.setZoom(12);
            }
          });
          /* eslint-enable no-undef */
        }, response => {
          this.hasError = true;
          this.errorMessage = response.body.errors[0];
        });
    }
  };
</script>

<style scoped>
  #map {
    width: 100%;
    height: 500px;
  }
</style>
