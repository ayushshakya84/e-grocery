let search = {
    loadRootCategoriesAndProducts: function () {
        let cataloguePromise = axios.get('/search/v1/search/all');
        let categoriesPromise = axios.get('/products/v1/category/root-categories');
        return Promise.all([cataloguePromise, categoriesPromise])
            .then((data) => {
                return {
                    products: this._toJsonArray(data[0].data),
                    categories: data[1].data
                }
            })
            .catch((err) => {
                toastr.error(err.message);
            });
    },

    onCategoryClick: function (category, breadcrumbs) {
        if (breadcrumbs.indexOf(category) === -1) {
            breadcrumbs.push(category);
        } else {
            breadcrumbs = breadcrumbs.slice(0, breadcrumbs.indexOf(category) + 1);
        }

        let productsPromise = axios.get('/search/v1/search/by-category?category=' + category.name)
        let subCategoriesPromise = axios.get('/products/v1/category/sub-categories?parentId=' + category.id)
        return Promise.all([productsPromise, subCategoriesPromise])
            .then((data) => {
                return {
                    products: this._toJsonArray(data[0].data),
                    categories: data[1].data,
                    breadcrumbs: breadcrumbs
                }
            })
            .catch((err) => {
                toastr.error(err.message);
            });
    },

    fullTextSearch: function (val) {
        let self = this;
        return Promise.all([axios.post('/search/v1/search/by-text', val)])
            .then(function (response) {
                if (response[0].data.length === 0) {
                    toastr.warning("No products found ;(")
                    return [];
                }
                return self._toJsonArray(response[0].data);
            })
            .catch(function (error) {
                if (error.message === null || error.message.length === 0) {
                    toastr.error(error.response.data);
                } else {
                    toastr.error(error.message)
                }
            });
    },

    _toJsonArray: function (array) {
        let jsonArray = [];
        array.forEach(el => {
            jsonArray.push(JSON.parse(el))
        });
        return jsonArray;
    }
}

export default search