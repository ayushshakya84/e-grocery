import validate from "./common/validation.js";

new Vue({
    el: '#app',
    data: {
        mode: 'create',
        photo: null,
        photoTmpUrl: null,
        buttonDisabled: false,
        categories: [],
        product: { categories: [], attributes: {}, discountPercent: 0 },
        categoryChosen: null,

        nameError: null,
        priceError: null,
        qtyError: null,
        weightError: null,
        discountError: null,
        categoriesError: null
    },

    mounted: function () {
        this.loadPage()
    },

    methods: {
        loadPage: function () {
            let self = this;
            self.loadRootCategories();
        },

        loadRootCategories: function () {
            let self = this;
            if (localStorage.getItem('productToEdit')) {
                self.product = JSON.parse(localStorage.getItem('productToEdit'));
                self.mode = 'edit';
                self.categoryChosen = self.product.categories[self.product.categories.length - 1];
                localStorage.clear();
            }
            axios.get('/products/v1/category/root-categories')
                .then(function (response) {
                    self.categories = response.data;
                })
                .catch(function (error) {
                    if (error.message === null || error.message.length === 0) {
                        toastr.error(error.response.data);
                    } else {
                        toastr.error(error.message)
                    }
                });
        },

        onImageChange(event) {
            let data = new FormData();
            const file = event.target.files[0];
            data.append('file', file);
            this.photoTmpUrl = URL.createObjectURL(file);
            this.photo = data;
        },

        onCategoryClick: function (category) {
            let self = this;
            self.product.categories.push(category);
            if (category.leaf) {
                toastr.success("Category assigned!");
                self.categoryChosen = category;
                return;
            }
            axios.get('/products/v1/category/sub-categories?parentId=' + category.id)
                .then(function (response) {
                    self.categories = response.data;
                })
                .catch(function (error) {
                    if (error.message === null || error.message.length === 0) {
                        toastr.error(error.response.data);
                    } else {
                        toastr.error(error.message)
                    }
                    self.resetCategories();
                });
        },

        saveOrUpdateProduct: function () {
            let self = this;
            if (!self.validate()) {
                return;
            }

            if (self.mode === 'create') {
                self.product.id = self._uuidv4();
                self.createProduct();
            } else if (self.mode === 'edit') {
                self.updateProduct();
            }
            if (self.photo !== null) {
                self.submitPhoto(self.product.id);
            }
        },

        createProduct: function () {
            let self = this;
            self.buttonDisabled = true;
            axios.post('/products/v1/product', self.product)
                .then(function (response) {
                    self.buttonDisabled = false;
                    self._nullifyState();
                    toastr.success("Product saved!");
                })
                .catch(function (error) {
                    self._handleProductErrorResponse(error);
                });
        },

        updateProduct: function () {
            let self = this;
            self.buttonDisabled = true;
            axios.put('/products/v1/product/' + self.product.id, self.product)
                .then(function (response) {
                    self.buttonDisabled = false;
                    self._nullifyState();
                    window.location.href = '/product-management'
                })
                .catch(function (error) {
                    self._handleProductErrorResponse(error);
                });
        },

        submitPhoto: function (productId) {
            let self = this;

            axios.post('/search/v1/image?productId=' + productId, self.photo)
                .then(function (response) {

                })
                .catch(function (error) {
                    if (error.message === null || error.message.length === 0) {
                        toastr.error(error.response.data);
                    } else {
                        toastr.error(error.message)
                    }
                });
        },

        resetCategories: function () {
            this.product.categories = [];
            this.categoryChosen = null;
            this.loadRootCategories();
        },

        validate: function() {
            let valid = true;
            this.nameError = null;
            this.priceError = null;
            this.qtyError = null;
            this.weightError = null;
            this.discountError = null;
            this.categoriesError = null;

            if (validate.isEmpty(this.product.name)) {
                valid = false;
                this.nameError = "Please, specify name."
            }
            if (validate.isEmpty(this.product.price)) {
                valid = false;
                this.priceError = "Please, specify price."
            }
            if (this.product.price <= 0) {
                valid = false;
                this.priceError = "Price must be > 0."
            }
            if (validate.isEmpty(this.product.qty)) {
                valid = false;
                this.qtyError = "Please, specify product quantity."
            }
            if (this.product.qty <= 0) {
                valid = false;
                this.qtyError = "Product quantity must be > 0."
            }
            if (validate.isEmpty(this.product.weight)) {
                valid = false;
                this.weightError = "Please, specify product weight."
            }
            if (this.product.qty <= 0) {
                valid = false;
                this.qtyError = "Product weight must be > 0."
            }
            if (validate.isEmpty(this.product.discountPercent)) {
                valid = false;
                this.discountError = "Please, specify product discount."
            }
            if (this.product.discountPercent < 0) {
                valid = false;
                this.discountError = "Product discount must be >= 0."
            }
            if (this.product.categories.length === 0) {
                valid = false;
                this.categoriesError = "Please, choose category."
            }
            return valid;
        },

        _nullifyState: function () {
            this.product = {categories: [], attributes: {}};
            this.photo = null;
            this.photoTmpUrl = null;
            this.resetCategories();
        },
        _uuidv4: function () {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
                var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        },

        _attrNameToCamelCase: function (str) {
            return str.replace(/(?:^\w|[A-Z]|\b\w)/g, function (word, index) {
                return index === 0 ? word.toLowerCase() : word.toUpperCase();
            }).replace(/\s+/g, '').replace(',', '');
        },

        _handleProductErrorResponse: function (error) {
            if (error.response.status === 409) {
                toastr.warning('Please, choose another product name, this product already exists');
            } else {
                if (error.message === null || error.message.length === 0) {
                    toastr.error(error.response.data);
                } else {
                    toastr.error(error.message)
                }
            }
            this.buttonDisabled = false;
            this.resetCategories();
        }
    },


});