import validate from "./common/validation.js";

new Vue({
    el: '#app',
    data: {
        paymentButtonDisabled: false,
        order: null,
        idempotencyKey: null,

        cardNumberError: null,
        cardValidToError: null,
        cardCvvError: null
    },

    mounted: function () {
        this.loadPage()
    },

    methods: {
        loadPage: function () {
            let self = this;
            self.order = JSON.parse(localStorage.getItem("order"));
            self.idempotencyKey = self._uuidv4();
        },

        doPayment: function () {
            let self = this;
            if (!self.validate()) {
                return;
            }
            self.paymentButtonDisabled = true;
            let config = {
                headers: {
                    "Idempotency-key": self.idempotencyKey,
                    'Content-Type': 'application/json'
                }
            }
            axios.post('/orders/v1/orders/start-order-processing', self.order, config)
                .then(function (response) {
                    if (response.data === null) {
                        toastr.error("It appears that you're trying to send the order you have already made.");
                        return;
                    }
                    self.paymentButtonDisabled = false;
                    window.location.href = '/order-accepted';
                })
                .catch(function (error) {
                    if (error.message === null || error.message.length === 0) {
                        toastr.error(error.response.data);
                    } else {
                        toastr.error(error.message)
                    }
                    self.paymentButtonDisabled = false;
                });
        },

        validate: function() {
            let valid = true;
            this.cardNumberError = null;
            this.cardValidToError = null;
            this.cardCvvError = null;

            if (validate.isEmpty(this.order.cardNumber)) {
                valid = false;
                this.cardNumberError = "Please, specify Card Number."
            }
            if (validate.isEmpty(this.order.cardValidTo)) {
                valid = false;
                this.cardValidToError = "Please, specify Card Valid To."
            }
            if (validate.isEmpty(this.order.cardCvv)) {
                valid = false;
                this.cardCvvError = "Please, specify Card CVV code."
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
