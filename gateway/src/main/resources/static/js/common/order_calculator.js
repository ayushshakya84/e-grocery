let calculator = {
    calculateOrderPrice: function (order) {
        let sumPrice = 0;
        order.forEach(product => {
            let discountPrice = this.calculateDiscountPrice(product.price, product.discountPercent);
            sumPrice += (discountPrice * product.qty);
        });

        return sumPrice.toFixed(2);
    },

    calculateDiscountPrice: function (price, discountPercent) {
        let discount = price * discountPercent / 100;
        return (price - discount).toFixed(2);
    }
}

export default calculator