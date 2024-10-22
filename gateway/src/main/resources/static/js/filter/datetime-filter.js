Vue.filter('datetimeFilter', function (time) {
    return moment(time).format("MMM Do HH:mm")
});
