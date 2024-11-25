document.addEventListener('DOMContentLoaded', function() {
    const createBoardForm = document.querySelector('#createBoardModal form');

    const getCsrfToken = () => {
        const token = document.querySelector("meta[name='_csrf']");
        const header = document.querySelector("meta[name='_csrf_header']");
        return {
            token: token ? token.content : null,
            header: header ? header.content : null
        };
    };

    const getUserId = () => {
        const userIdContainer = document.getElementById('userIdContainer');
        if (!userIdContainer || !userIdContainer.getAttribute('user-id')) {
            console.error('User ID not found. Make sure the userIdContainer element exists with a user-id attribute.');
            return null;
        }
        return userIdContainer.getAttribute('user-id');
    };

    const showError = (message, form) => {
        const existingAlert = form.querySelector('.alert');
        if (existingAlert) {
            existingAlert.remove();
        }

        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-danger alert-dismissible fade show';
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        form.insertBefore(alertDiv, form.firstChild);
    };

    createBoardForm.addEventListener('submit', async function (e) {
        e.preventDefault();

        try {
            const csrfInfo = getCsrfToken();
            const userId = getUserId();

            if (!userId) {
                throw new Error('User ID not found. Please try logging in again.');
            }

            if (!csrfInfo.token || !csrfInfo.header) {
                throw new Error('Security token not found. Please refresh the page.');
            }

            const formData = new FormData(this);

            // Validate required fields
            const name = formData.get('name');
            if (!name) {
                throw new Error('Board name is required.');
            }

            // Create board data
            const boardData = {
                name: name,
                description: formData.get('description') || '',
                isPrivate: formData.get('private') === 'on',
                userId: parseInt(userId)
            };

            console.log('Sending board data:', boardData);

            // Create board
            const boardResponse = await fetch('/api/boards', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    [csrfInfo.header]: csrfInfo.token
                },
                body: JSON.stringify(boardData)
            });

            let responseText;
            try {
                responseText = await boardResponse.text();
                console.log('Server response:', responseText);

                if (!boardResponse.ok) {
                    throw new Error(responseText || 'Failed to create board');
                }

                // Try to parse the response as JSON
                const createdBoard = JSON.parse(responseText);
                console.log('Created board:', createdBoard);

                // Handle image upload if present
                const imageFile = formData.get('image');
                if (imageFile && imageFile.size > 0) {
                    const imageFormData = new FormData();
                    imageFormData.append('image', imageFile);

                    const imageResponse = await fetch(`/api/boards/${createdBoard.id}/image`, {
                        method: 'POST',
                        headers: {
                            [csrfInfo.header]: csrfInfo.token
                        },
                        body: imageFormData
                    });

                    if (!imageResponse.ok) {
                        console.warn('Image upload failed, but board was created');
                    }
                }

                // Success handling
                const modal = bootstrap.Modal.getInstance(document.querySelector('#createBoardModal'));
                if (modal) {
                    modal.hide();
                }

                // Clear form
                this.reset();

                // Redirect with success message
                window.location.href = '/dashboard?success=Board created successfully';

            } catch (parseError) {
                console.error('Response parsing error:', parseError);
                console.error('Raw response:', responseText);
                throw new Error('Invalid response from server');
            }

        } catch (error) {
            console.error('Error creating board:', error);
            showError(error.message || 'An unexpected error occurred', this);
        }
    });
        document.addEventListener('DOMContentLoaded', function() {
        // Función para manejar errores de carga de imagen
        function handleImageError(img) {
            const container = img.parentElement;
            // Reemplazar con un placeholder
            container.innerHTML = `
            <div class="d-flex align-items-center justify-content-center bg-light" style="height: 200px;">
                <i class="bi bi-image text-muted" style="font-size: 2rem;"></i>
            </div>
        `;
        }

        // Añadir manejador de errores a todas las imágenes de tableros
        document.querySelectorAll('.board-image').forEach(img => {
        img.addEventListener('error', () => handleImageError(img));
    });
    });

    // Image preview handling
    const imageInput = createBoardForm.querySelector('input[type="file"]');
    const imagePreview = document.getElementById('imagePreview');

    if (imageInput && imagePreview) {
        imageInput.addEventListener('change', function (e) {
            const file = this.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function (e) {
                    const img = imagePreview.querySelector('img');
                    if (img) {
                        img.src = e.target.result;
                        imagePreview.classList.remove('d-none');
                    }
                };
                reader.readAsDataURL(file);
            } else {
                imagePreview.classList.add('d-none');
            }
        });
    }
});
console.log('createboard.js loaded successfully');
