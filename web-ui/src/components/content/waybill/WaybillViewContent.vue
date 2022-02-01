<template>
  <div>
    <md-card>
      <md-card-content>
        <span v-if="currentCheckpoint !== null">
          <h1>Address: {{ currentCheckpoint.address }}</h1>
          <p>Required arrival date: {{ currentCheckpoint.requiredArrivalDate }}</p>
        </span>
        <span v-if="currentCheckpoint === null">
          <h1>Path is over</h1>
        </span>
      </md-card-content>

      <md-progress-bar md-mode="indeterminate" v-if="sending"/>

      <md-card-actions>
        <md-button
          class="md-primary"
          :disabled="sending"
          to="/waybills"
        >
          Back
        </md-button>

        <md-button
          v-if="currentCheckpoint !== null && userRoles.includes('DRIVER')"
          class="md-primary"
          :disabled="sending"
          @click="reachCheckpoint()"
        >
          {{ reachButtonTitle }}
        </md-button>
      </md-card-actions>

      <div id="map"></div>

      <md-chip
        class="md-accent md-layout md-alignment-center"
        v-if="hasError"
      >
        {{errorMessage}}
      </md-chip>
    </md-card>

    <md-snackbar
      :md-active.sync="checkpointReached"
    >
      {{messages.CHECKPOINT_REACHED}}
    </md-snackbar>
  </div>
</template>

<script>
  import moment from 'moment';

  import {Messages} from '../../../constants/messages';
  import {Url} from '../../../constants/url';

  export default {
    name: 'WaybillViewContent',

    data: () => ({
      messages: Messages,
      errorMessage: null,
      hasError: false,
      userRoles: [],

      sending: false,
      checkpointReached: false,
      checkpoints: [],
      currentCheckpoint: null,

      reachButtonTitle: 'Next checkpoint',

      map: null,
      service: null
    }),

    methods: {
      reachCheckpoint() {
        this.$http.put(`${Url.WAYBILL}/${this.$route.params.id}`, {}, {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(() => {
            this.$store.dispatch('websocket/send', {
              achieveTime: moment(new Date()).format('YYYY-MM-DDTHH:mm'),
              checkpointAddress: this.currentCheckpoint.address
            });

            this.getCheckpoints();

            this.hasError = false;
            this.sending = false;
          }, response => {
            this.hasError = true;
            this.errorMessage = response.body.errors[0];
            this.sending = false;
          });
      },

      getCheckpoints() {
        this.$http.get(`${Url.WAYBILL}/${this.$route.params.id}`, {
          headers: {
            Authorization: `Bearer ${localStorage.accessToken}`
          }
        })
          .then(response => {
            this.checkpoints = response.body.map(item => {
              return {
                address: item.address,
                requiredArrivalDate: moment(item.requiredArrivalDate).format('YYYY-MM-DD HH:MM'),
                checkpointDate: item.checkpointDate ? moment(item.checkpointDate).format('YYYY-MM-DD HH:MM') : null
              };
            });

            for (let i = 0; i < this.checkpoints.length; i++) {
              this.currentCheckpoint = null;
              if (this.checkpoints[i].checkpointDate === null) {
                this.currentCheckpoint = this.checkpoints[i];
                if (i === this.checkpoints.length - 1) {
                  this.reachButtonTitle = 'Finish';
                }
                break;
              }
            }

            /* eslint-disable no-undef */
            for (let i = 0; i < this.checkpoints.length; i++) {
              const request = {
                query: this.checkpoints[i].address,
                fields: ['name', 'geometry']
              };

              this.service.findPlaceFromQuery(request, (results, status) => {
                if (status === google.maps.places.PlacesServiceStatus.OK) {
                  let color = 'blue';
                  if (i === this.checkpoints.length - 1) {
                    color = 'green';
                  }
                  if (this.checkpoints[i].checkpointDate !== null) {
                    color = 'red';
                  }
                  this.pushCheckpointLocation(i, results[0].geometry.location);
                  new google.maps.Marker({
                    map: this.map,
                    title: results[0].name,
                    icon: `http://maps.google.com/mapfiles/ms/micons/${color}.png`,
                    position: results[0].geometry.location
                  });
                }
              });
            }
            /* eslint-enable no-undef */
          });
      },

      pushCheckpointLocation(index, location) {
        this.checkpoints[index].location = location;

        for (let i = 0; i < this.checkpoints.length; i++) {
          if (!this.checkpoints[i].location) {
            return;
          }
        }
        this.writePath(this.map);
      },

      writePath(map) {
        /* eslint-disable no-undef */
        let path = new google.maps.MVCArray();
        let poly = new google.maps.Polyline({
          map: this.map,
          strokeColor: '#4986E7'
        });
        const directionService = new google.maps.DirectionsService();

        const waypoints = [...this.checkpoints];
        waypoints.shift();
        waypoints.pop();

        const waypointsArray = [];
        for (let i = 0; i < waypoints.length; i++) {
          waypointsArray.push({location: waypoints[i].location});
        }

        directionService.route({
          origin: this.checkpoints[0].location,
          destination: this.checkpoints[this.checkpoints.length - 1].location,
          waypoints: waypointsArray,
          travelMode: google.maps.DirectionsTravelMode.DRIVING
        }, (result, status) => {
          if (status == google.maps.DirectionsStatus.OK) {
            for (let j = 0, len = result.routes[0].overview_path.length; j < len; j++) {
              path.push(result.routes[0].overview_path[j]);
              poly.getPath().push(path.getAt(j));
              map.panTo(path.getAt(j));
            }
            const latlng = this.currentCheckpoint ? this.currentCheckpoint.location
                          : this.checkpoints[this.checkpoints.length - 1].location;
            map.setCenter(latlng);
          }
        });
        /* eslint-enable no-undef */
      }
    },

    mounted: function() {
      this.userRoles = JSON.parse(localStorage.getItem('roles'));
      if (!this.userRoles || !this.userRoles.includes('MANAGER')
          && !this.userRoles.includes('DRIVER')
          && !this.userRoles.includes('COMPANY_OWNER')
      ) {
        this.$router.replace('/');
      }

      /* eslint-disable no-undef */
      const map = new google.maps.Map(document.getElementById('map'), {
        zoom: 8
      });
      this.map = map;
      this.service = new google.maps.places.PlacesService(map);
      /* eslint-enable no-undef */

      this.getCheckpoints();
    }
  };
</script>

<style scoped>
  #map {
    width: 100%;
    height: 500px;
  }
</style>
