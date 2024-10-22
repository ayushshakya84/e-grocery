import search from './common/search.js'

new Vue({
    el: '#app',
    data: {
        isTyping: false,
        searchQuery: "",
        breadcrumbs: [],
        categories: [],
        products: [],
        labels: [],
        dataset: [],
    },

    watch: {
        searchQuery: _.debounce(function() {
            this.isTyping = false;
        }, 1000),
        isTyping: function(value) {
            if (!value) {
                this.fullTextSearch(this.searchQuery);
            }
        }
    },

    mounted: function () {
        this.loadPage()
    },

    methods: {
        loadPage: function () {
            let self = this;
            self.loadInitState();
        },

        loadInitState: function () {
            let self = this;
            self.breadcrumbs = [{name: "Catalogue"}];
            search.loadRootCategoriesAndProducts()
                .then(res => {
                    self.categories = res.categories;
                    self.products = res.products;
                });
        },

        onCategoryClick: function (category) {
            let self = this;
            if (category.name === "Catalogue") {
                self.loadInitState();
                return;
            }
            search.onCategoryClick(category, self.breadcrumbs)
                .then(res => {
                    self.categories = res.categories;
                    self.products = res.products;
                    self.breadcrumbs = res.breadcrumbs;
                });
        },

        fullTextSearch: function (val) {
            let self = this;
            if (val.length === 0) {
                self.loadInitState();
                return;
            }
            search.fullTextSearch(val)
                .then(res => {
                    self.products = res;
                });
        },

        getImgSrc: function (productId) {
            return "/search/v1/image?productId=" + productId;
        },

        deleteProduct: function (product) {
            let self = this;
            axios.delete('/products/v1/product/' + product.id)
                .then(function (response) {
                    toastr.success("Product deleted!");
                    self.products.splice(product, 1)
                })
                .catch(function (error) {
                    if (error.message === null || error.message.length === 0) {
                        toastr.error(error.response.data);
                    } else {
                        toastr.error(error.message)
                    }
                });
        },

        editProduct: function (product) {
            localStorage.setItem('productToEdit', JSON.stringify(product));
            window.location.href = '/product-edit';
        },

        drawPriceChart: function (product) {
            let self = this;
            self.labels = []
            self.dataset = []
            axios.get('/products/v1/product/' + product.id + '/price')
                .then(function (response) {
                    response.data.forEach(data => {
                        self.labels.push(moment(data['startDate']).format("YYYY-MMM Do HH:mm"));
                        self.dataset.push(data['value']);
                    })
                    let ctx = document.getElementById("chart");
                    let chart = new Chart(ctx, {
                        type: 'line',
                        data: {
                            labels: self.labels,
                            datasets: [{
                                label: 'Price changes for ' + product.name,
                                backgroundColor: 'rgb(255, 99, 132)',
                                borderColor: 'rgb(255, 99, 132)',
                                data: self.dataset
                            }]
                        },
                        options: {}
                    });
                })
                .catch(function (error) {
                    if (error.message === null || error.message.length === 0) {
                        toastr.error(error.response.data);
                    } else {
                        toastr.error(error.message)
                    }
                });
        },

    },


});