// Initialize the editor
const editor = new Quill('#editor', {
    theme: 'snow'
});

// Handle form submission
document.getElementById('myForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent form submission

    // Get the text from the editor
    const editorContent = editor.root.innerHTML;
    const title = document.getElementById('title').value;

    console.log('Title:', title);
    console.log('Content:', editorContent);

    // Here, you can send `title` and `editorContent` to your server
});