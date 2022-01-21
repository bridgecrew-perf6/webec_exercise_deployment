(function() {
    let Booklist = {
        init: function() {
            this.initBookDetailDeleteForm();
        },

        initBookDetailDeleteForm: function () {
            document.querySelectorAll('.book-detail__delete-form').forEach((deleteFormElement) => {
                deleteFormElement.onsubmit = () => {
                    return confirm('Do you really want to delete this book?');
                };
            });
        }
    };

    document.addEventListener('DOMContentLoaded', () => Booklist.init());
})();
