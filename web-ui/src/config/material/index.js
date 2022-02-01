import {
  MdApp,
  MdButton,
  MdCard,
  MdCheckbox,
  MdChips,
  MdContent,
  MdDialog,
  MdDialogAlert,
  MdDialogConfirm,
  MdDrawer,
  MdEmptyState,
  MdField,
  MdList,
  MdMenu,
  MdRipple,
  MdTable,
  MdToolbar,
  MdProgress,
  MdSnackbar,
  MdDatepicker,
  MdRadio,
  MdDivider,
  MdAutocomplete
} from 'vue-material/dist/components';
import 'vue-material/dist/vue-material.min.css';
import 'vue-material/dist/theme/default.css';
import Vue from 'vue';

export const MaterialComponents = {
  use: () => {
    Vue.use(MdApp);
    Vue.use(MdChips);
    Vue.use(MdContent);
    Vue.use(MdToolbar);
    Vue.use(MdDrawer);
    Vue.use(MdList);
    Vue.use(MdCard);
    Vue.use(MdField);
    Vue.use(MdButton);
    Vue.use(MdTable);
    Vue.use(MdRipple);
    Vue.use(MdMenu);
    Vue.use(MdEmptyState);
    Vue.use(MdProgress);
    Vue.use(MdDialog);
    Vue.use(MdDialogConfirm);
    Vue.use(MdDialogAlert);
    Vue.use(MdCheckbox);
    Vue.use(MdSnackbar);
    Vue.use(MdDatepicker);
    Vue.use(MdRadio);
    Vue.use(MdDivider);
    Vue.use(MdAutocomplete);
  }
};
