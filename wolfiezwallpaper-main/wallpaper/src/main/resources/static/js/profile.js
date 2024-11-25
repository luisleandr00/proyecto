document.addEventListener('DOMContentLoaded', function() {
    const profileForm = document.getElementById('profileForm');
    const imageInput = document.getElementById('profileImage');
    const profileImageDisplay = document.getElementById('profileImageDisplay');

    // Get CSRF token
    const token = document.querySelector('meta[name="_csrf"]').content;
    const headerName = document.querySelector('meta[name="_csrf_header"]').content;

    if (profileForm) {
        profileForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const formData = new FormData(this);

            // Ensure CSRF token is in FormData
            if (!formData.has('_csrf')) {
                formData.append('_csrf', token);
            }

            fetch('/profile/update', {
                method: 'POST',
                body: formData,
                credentials: 'same-origin', // Important for CSRF
                headers: {
                    [headerName]: token
                }
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(data => {
                            throw new Error(data.message || 'Error updating profile');
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        if (data.profileImageUrl) {
                            updateProfileImage(data.profileImageUrl);
                        }
                        showMessage(data.message, 'success');

                        // Clear password fields
                        const passwordFields = profileForm.querySelectorAll('input[type="password"]');
                        passwordFields.forEach(field => field.value = '');
                    } else {
                        showMessage(data.message || 'Error updating profile', 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showMessage(error.message || 'Error updating profile', 'error');
                });
        });
    }

    function updateProfileImage(imageUrl) {
        const profileImages = document.querySelectorAll('.profile-image');
        profileImages.forEach(img => {
            img.src = imageUrl + '?t=' + new Date().getTime();
        });
    }

    function showMessage(message, type) {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type === 'success' ? 'success' : 'danger'} alert-dismissible fade show`;
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;

        const container = document.querySelector('.container');
        container.insertBefore(alertDiv, container.firstChild);

        setTimeout(() => {
            alertDiv.remove();
        }, 5000);
    }
});