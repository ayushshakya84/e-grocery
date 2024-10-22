new Vue({
    el: '#app',
    data: {
        order: null,
    },

    mounted: function () {
        this.order = JSON.parse(localStorage.getItem('order'));
    },

    methods: {
        onOrderClick: function () {
            localStorage.clear();
            window.location.href = '/order';
        },

        onCatalogueClick: function () {
            localStorage.clear();
            window.location.href = '/product-catalogue';
        },
    }
});
