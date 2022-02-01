import Vue from 'vue';
import Router from 'vue-router';

import Login from '../../pages/LoginPage.vue';
import MainPage from '../../pages/MainPage.vue';
import AboutContent from '../../components/content/AboutContent';
import HomeContent from '../../components/content/HomeContent';
import ClientList from '../../components/content/client/ClientList';
import StorageAddContent from '../../components/content/storage/StorageAddContent';
import StorageEditContent from '../../components/content/storage/StorageEditContent';
import StorageListContent from '../../components/content/storage/StorageListContent';
import InvoiceAddContent from '../../components/content/invoice/InvoiceAddContent';
import InvoiceEditContent from '../../components/content/invoice/InvoiceEditContent';
import InvoiceListContent from '../../components/content/invoice/InvoiceListContent';
import WaybillListContent from '../../components/content/waybill/WaybillListContent';
import WaybillAddContent from '../../components/content/waybill/WaybillAddContent';
import WaybillViewContent from '../../components/content/waybill/WaybillViewContent';
import UserListContent from '../../components/content/user/UserListContent';
import AddUser from '../../components/content/user/AddUser';
import EditUser from '../../components/content/user/EditUser';
import ProductOwnerListContent from '../../components/content/productowner/ProductOwnerListContent';
import ProductOwnerAddContent from '../../components/content/productowner/ProductOwnerAddContent';
import ProductOwnerEditContent from '../../components/content/productowner/ProductOwnerEditContent';
import ClientAddContent from '../../components/content/client/ClientAddContent';
import CarListContent from '../../components/content/car/CarListContent';
import CarAddContent from '../../components/content/car/CarAddContent';
import CarEditContent from '../../components/content/car/CarEditContent';
import ClientEditContent from '../../components/content/client/ClientEditContent';
import EmailContent from '../../components/content/email/EmailContent';
import RepairAccount from '../../pages/RepairAccount';
import RestorePassword from '../../pages/RestorePassword';
import CompanyOwnerReportContent from '../../components/content/reports/CompanyOwnerReportContent';
import ProfileContent from '../../components/content/profile/ProfileContent';
import EmailConfirmedPage from '../../pages/EmailConfirmedPage';
import TemplateContent from '../../components/content/template/TemplateContent';
import SysAdminReportContent from '../../components/content/reports/SysAdminReportContent';

export const AppRouter = {
  create: () => {
    Vue.material.router.linkActiveClass = linkActiveClass;

    const router = new Router({
      routes,
      linkActiveClass
    });
    return router;
  }
};

const routes = [
  {
    path: '/',
    component: MainPage,
    children: [
      {
        path: '',
        component: HomeContent
      },
      {
        path: 'about',
        component: AboutContent
      },
      {
        path: 'storages',
        component: StorageListContent
      },
      {
        path: 'storages/add',
        component: StorageAddContent
      },
      {
        path: 'storages/:id',
        name: 'get-storage',
        props: true,
        component: StorageEditContent
      },
      {
        path: 'clients',
        component: ClientList
      },
      {
        path: 'clients/add',
        component: ClientAddContent
      },
      {
        path: 'clients/:id',
        name: 'get-client',
        props: true,
        component: ClientEditContent
      },
      {
        path: 'invoices',
        component: InvoiceListContent
      },
      {
        path: 'invoices/add',
        component: InvoiceAddContent
      },
      {
        path: 'invoices/:id',
        name: 'get-invoice',
        props: true,
        component: InvoiceEditContent
      },
      {
        path: 'waybills',
        component: WaybillListContent
      },
      {
        path: 'invoices/:id/waybills/add',
        name: 'create-waybill',
        props: true,
        component: WaybillAddContent
      },
      {
        path: 'waybills/:id',
        name: 'get-waybill',
        props: true,
        component: WaybillViewContent
      },
      {
        path: 'users',
        component: UserListContent
      },
      {
        path: 'users/add',
        component: AddUser
      },
      {
        path: 'users/:id',
        name: 'get-user',
        props: true,
        component: EditUser
      },
      {
        path: 'product-owners',
        component: ProductOwnerListContent
      },
      {
        path: 'product-owners/add',
        component: ProductOwnerAddContent
      },
      {
        path: 'product-owners/:id',
        name: 'get-product-owner',
        props: true,
        component: ProductOwnerEditContent
      },
      {
        path: 'cars',
        component: CarListContent
      },
      {
        path: 'cars/add',
        component: CarAddContent
      },
      {
        path: 'cars/:id',
        name: 'get-car',
        props: true,
        component: CarEditContent
      },
      {
        path: 'email',
        component: EmailContent
      },
      {
        path: 'template',
        component: TemplateContent
      },
      {
        path: 'reports/client',
        component: CompanyOwnerReportContent
      },
      {

        path: 'reports/sysadmin',
        component: SysAdminReportContent
      },
      {
        path: 'profile',
        component: ProfileContent
      }
    ]
  },
  {
    path: '/login',
    component: Login
  },
  {
    path: '/email/repairing',
    component: RepairAccount
  },
  {
    path: '/restore/:uuid',
    name: 'restore',
    props: true,
    component: RestorePassword
  },
  {
    path: '/confirm-change-email/:uuid',
    name: 'confirm-email',
    props: true,
    component: EmailConfirmedPage
  },
  {
    path: '*',
    redirect: '/'
  }
];

const linkActiveClass = 'md-accent';
