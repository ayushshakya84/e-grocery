import search from './common/search.js'
import calculator from './common/order_calculator.js'

new Vue({
    el: '#app',
    data: {
        products: [],
        categories: [],
        cart: [],
        searchQuery: "",
        isTyping: false,
        breadcrumbs: [],
        chosenProduct: {}
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
            if (localStorage.getItem('products')) {
                self.cart = JSON.parse(localStorage.getItem('products'));
            }
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

        addToCart: function (product, qty) {
            let self = this;

            if (self.productExistsInCart(product)) {
                return;
            }

            let productToAdd = JSON.parse(JSON.stringify(product));
            productToAdd.qty = qty;
            self.cart.push(productToAdd);
            localStorage.setItem('products', JSON.stringify(self.cart));
        },

        removeFromCart(product) {
            const index = this.getIndexOfCartProduct(product);
            if (index > -1) {
                this.cart.splice(index, 1);
            }
            localStorage.setItem('products', JSON.stringify(this.cart));
        },

        changeQuantity: function (product, delta) {
            let self = this;
            self.cart.forEach(cartProduct => {
                if (cartProduct.id === product.id) {
                    cartProduct.qty += delta;
                    if (cartProduct.qty <= 0) {
                        self.removeFromCart(cartProduct)
                    }
                }
            });
            localStorage.setItem('products', JSON.stringify(self.cart));
        },

        productExistsInCart: function (product) {
            return this.getIndexOfCartProduct(product) !== -1;
        },

        onProductDetailClick: function (product) {
            this.chosenProduct = product;
        },

        getIndexOfCartProduct: function (product) {
            let self = this;
            let idx = -1;
            for (let i = 0; i < self.cart.length; i++) {
                if (self.cart[i].id === product.id) {
                    idx = i;
                }
            }
            return idx;
        },

        getImgSrc: function (productId) {
            return "/search/v1/image?productId=" + productId;
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

        calcOrderPrice: function () {
            return calculator.calculateOrderPrice(this.cart);
        },

        calcDiscountPrice: function (product) {
            return calculator.calculateDiscountPrice(product.price, product.discountPercent);
        }

    }
});
