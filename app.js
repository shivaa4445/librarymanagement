document.addEventListener("DOMContentLoaded", function () {
    fetchBooks();

    function fetchBooks() {
        fetch('BookServlet')
            .then(response => response.text())
            .then(data => {
                document.getElementById('book-list').innerHTML = data;
            })
            .catch(error => console.log('Error fetching books:', error));
    }
});
