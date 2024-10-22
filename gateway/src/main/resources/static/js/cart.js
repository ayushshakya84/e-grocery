import calculator from './common/order_calculator.js'
import validate from "./common/validation.js";

new Vue({
    el: '#app',
    data: {
        format: 'dd MMMM yyyy',
        cart: [],
        address: null,
        shipmentFromDate: new Date(),
        shipmentFromHour: moment().add('hours', 2).hour(),
        shipmentToDate: new Date(),
        shipmentToHour: moment().add('hours', 3).hour(),
        idempotencyKey: null,
        saveOrderButtonDisabled: false,

        addressError: null,
        shipmentFromError: null,
        shipmentToError: null,
    },

    components: {
        vuejsDatepicker
    },

    mounted: function () {
        this.loadPage()
    },

    methods: {
        loadPage: function () {
            let self = this;
            if (localStorage.getItem('products')) {
                self.cart = JSON.parse(localStorage.getItem('products'));
            }
            this.idempotencyKey = this._uuidv4();
        },

        removeFromCart(product) {
            this._refreshIdempotencyKey();

            const index = this.cart.indexOf(product);
            if (index > -1) {
                this.cart.splice(index, 1);
            }

            localStorage.setItem('products', JSON.stringify(this.cart));
        },

        changeQuantity: function (product, delta) {
            this._refreshIdempotencyKey();

            let self = this;
            self.cart.forEach(cartProduct => {
                if (cartProduct === product) {
                    cartProduct.qty += delta;
                    if (cartProduct.qty <= 0) {
                        self.removeFromCart(cartProduct)
                    }
                }
            });

            localStorage.setItem('products', JSON.stringify(self.cart));
        },

        saveOrder: function () {
            let self = this;
            let config = {
                headers: {
                    'Idempotency-key': self.idempotencyKey,
                    'Content-Type': 'application/json'
                }
            }
            let shipmentFrom = moment(self.shipmentFromDate)
                .set('hour', self.shipmentFromHour)
                .set('minute', 0)
                .set('second', 0)
                .toISOString()
            let shipmentTo = moment(self.shipmentToDate)
                .set('hour', self.shipmentToHour)
                .set('minute', 0)
                .set('second', 0)
                .toISOString()
            let orderItems = [];
            self.cart.forEach(product => {
                let orderItem =
                    {
                        productId: product.id,
                        productName: product.name,
                        productPrice: product.price,
                        qty: product.qty,
                    }
                orderItems.push(orderItem);
            })

            let order = {
                totalPrice: self.calcOrderPrice(),
                address: self.address,
                shipmentFrom: shipmentFrom,
                shipmentTo: shipmentTo,
                orderItems: orderItems
            }
            if (!self.validate(order)) {
                return;
            }
            self.saveOrderButtonDisabled = true;
            axios.post('/orders/v1/orders', order, config)
                .then(function (response) {
                    if (response.data === null) {
                        toastr.error("It appears that you're trying to send the order you have already made.");
                        return;
                    }
                    localStorage.setItem('order', JSON.stringify(response.data));
                    self.saveOrderButtonDisabled = false;
                    window.location.href = '/payment'
                })
                .catch(function (error) {
                    if (error.message === null || error.message.length === 0) {
                        toastr.error(error.response.data);
                    } else {
                        toastr.error(error.message)
                    }
                    self.saveOrderButtonDisabled = false;
                });
        },

        getImgSrc: function (productId) {
            return "/search/v1/image?productId=" + productId;
        },

        calcOrderPrice: function () {
            return calculator.calculateOrderPrice(this.cart);
        },

        calcDiscountPrice: function (product) {
            return calculator.calculateDiscountPrice(product.price, product.discountPercent);
        },

        validate: function(order) {
            let valid = true;
            this.addressError = null;
            this.shipmentFromError = null;
            this.shipmentToError = null;

            if (validate.isEmpty(order.address)) {
                valid = false;
                this.addressError = "Please, specify address."
            }
            if (validate.isEmpty(order.shipmentFrom)) {
                valid = false;
                this.shipmentFromError = "Please, specify 'Shipment from' date and hour."
            }
            if (validate.isEmpty(order.shipmentTo)) {
                valid = false;
                this.shipmentToError = "Please, specify 'Shipment to' date and hour."
            }
            if (moment(order.shipmentFrom).isBefore(moment())) {
                valid = false;
                this.shipmentFromError = "'Shipment from' must be a future date."
            }
            if (moment(order.shipmentTo).isBefore(moment())) {
                valid = false;
                this.shipmentToError = "'Shipment to' must be a future date."
            }
            if (moment(order.shipmentFrom).isAfter(moment(order.shipmentTo))) {
                valid = false;
                this.shipmentFromError = "'Shipment from' date must go before 'Shipment to' date."
            }
            if (order.orderItems.length === 0) {
                valid = false;
                toastr.warning("Your order is empty.")
            }
            return valid;
        },

        _uuidv4: function () {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        },

        _refreshIdempotencyKey: function () {
            this.idempotencyKey = this._uuidv4();
        }
    }
});
