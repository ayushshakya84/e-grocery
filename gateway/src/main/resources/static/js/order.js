import calculator from './common/order_calculator.js'

new Vue({
    el: '#app',
    data: {
        orders: [],
        currentOrder: {},
        orderShipments: [],
        orderPayments: [],
        orderLogs: [],
    },

    mounted: function () {
        this.loadPage()
    },

    methods: {
        loadPage: function () {
            let self = this;
            axios.get('/orders/v1/orders')
                .then(function (response) {
                    self.orders = response.data;
                })
                .catch(function (error) {
                    if (error.message === null || error.message.length === 0) {
                        toastr.error(error.response.data);
                    } else {
                        toastr.error(error.message)
                    }
                });

        },

        onOrderItemsClick: function (order) {
            this.currentOrder = order;
        },

        doPayment: function (order) {
            localStorage.setItem('order', JSON.stringify(order));
            window.location.href = '/payment';
        },

        onOrderDetailsClick: function (order) {
            let self = this;
            self.currentOrder = order;

            let getShipments = axios.get('/shipments/v1/shipments/' + order.id)
            let getPayments = axios.get('/payments/v1/payments/' + order.id)
            let getLogs = axios.get('/ordersaga/v1/logs/' + order.id)

            Promise.all([getShipments, getPayments, getLogs])
                .then((data) => {
                    self.orderShipments = data[0].data;
                    self.orderPayments = data[1].data;
                    self.orderLogs = data[2].data;
                })
                .catch((err) => {
                    if (err.message === null || err.message.length === 0) {
                        toastr.error(err.response.data);
                    } else {
                        toastr.error(err.message)
                    }
                });
        },

        calcDiscountPrice: function (product) {
            return calculator.calculateDiscountPrice(product.productPrice, product.discountPercent);
        },

        _getStatusColor: function (status) {
            if (status === 'CREATED') {
                return 'grey';
            } else if (status === 'SUCCESS') {
                return 'green';
            } else if (status === 'ERROR') {
                return 'red';
            } else if (status === 'PROCESSING') {
                return 'blue';
            } else {
                return 'black';
            }
        }
    }
});
