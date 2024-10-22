import validate from "./common/validation.js";

new Vue({
    el: '#app',
    data: {
        user: null,

        firstNameError: null,
        lastNameError: null,
        emailError: null
    },

    mounted: function () {
        this.loadPage()
    },

    methods: {
        loadPage: function () {
            this.loadUserProfile();
        },

        loadUserProfile: function () {
            let self = this;
            axios.get('/profiles/v1/my-profile')
                .then(function (response) {
                    self.user = response.data;
                })
                .catch(function (error) {
                    if (error.message === null || error.message.length === 0) {
                        toastr.error(error.response.data);
                    } else {
                        toastr.error(error.message)
                    }
                });
        },

        changeUserProfile: function () {
            let self = this;
            if (!self.validate()) {
                return;
            }
            axios.patch('/profiles/v1/my-profile', self.user)
                .then(function (response) {
                    toastr.success("Profile has been changed successfully!")
                    self.user = response.data;
                })
                .catch(function (error) {
                    self.loadUserProfile();
                    if (error.message === null || error.message.length === 0) {
                        toastr.error(error.response.data);
                    } else {
                        toastr.error(error.message)
                    }
                });
        },

        validate: function(order) {
            let valid = true;
            this.firstNameError = null;
            this.lastNameError = null;
            this.emailError = null;

            if (validate.isEmpty(this.user.firstName)) {
                valid = false;
                this.firstNameError = "Please, specify first name."
            }
            if (validate.isEmpty(this.user.lastName)) {
                valid = false;
                this.lastNameError = "Please, specify last name."
            }
            if (validate.isEmpty(this.user.email)) {
                valid = false;
                this.emailError = "Please, specify e-mail."
            }
            return valid;
        }

    }
});
