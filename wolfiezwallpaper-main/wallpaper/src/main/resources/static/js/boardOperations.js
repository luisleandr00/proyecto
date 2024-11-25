document.addEventListener('DOMContentLoaded', function() {
    const csrfInfo = getCsrfToken();

    // Función para obtener el token CSRF
    function getCsrfToken() {
        const token = document.querySelector("meta[name='_csrf']");
        const header = document.querySelector("meta[name='_csrf_header']");
        return {
            token: token ? token.content : null,
            header: header ? header.content : null
        };
    }

    // Función para mostrar mensajes de error o éxito
    function showAlert(message, type = 'success') {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
        alertDiv.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        document.querySelector('.container').insertBefore(alertDiv, document.querySelector('.row'));

        // Auto-cerrar después de 5 segundos
        setTimeout(() => {
            alertDiv.remove();
        }, 5000);
    }

    // Función para editar un board
    async function editBoard(boardId, boardData) {
        try {
            const response = await fetch(`/api/boards/${boardId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    [csrfInfo.header]: csrfInfo.token
                },
                body: JSON.stringify(boardData)
            });

            if (!response.ok) throw new Error('Failed to update board');

            showAlert('Board updated successfully');
            window.location.reload();
        } catch (error) {
            showAlert(error.message, 'danger');
        }
    }

    // Función para eliminar un board
    async function deleteBoard(boardId) {
        if (!confirm('Are you sure you want to delete this board?')) return;

        try {
            const response = await fetch(`/api/boards/${boardId}`, {
                method: 'DELETE',
                headers: {
                    [csrfInfo.header]: csrfInfo.token
                }
            });

            if (!response.ok) throw new Error('Failed to delete board');

            showAlert('Board deleted successfully');
            // Remover el elemento del DOM o recargar la página
            const boardElement = document.querySelector(`[data-board-id="${boardId}"]`);
            if (boardElement) boardElement.remove();
        } catch (error) {
            showAlert(error.message, 'danger');
        }
    }

    // Función para ver detalles de un board
    async function viewBoardDetails(boardId) {
        try {
            const response = await fetch(`/api/boards/${boardId}/details`);
            if (!response.ok) throw new Error('Failed to fetch board details');

            const boardDetails = await response.json();
            showBoardDetailsModal(boardDetails);
        } catch (error) {
            showAlert(error.message, 'danger');
        }
    }

    // Función para mostrar el modal de detalles
    function showBoardDetailsModal(board) {
        const modal = new bootstrap.Modal(document.getElementById('boardDetailsModal'));
        const modalContent = document.querySelector('#boardDetailsModal .modal-body');

        modalContent.innerHTML = `
            <div class="board-details">
                <img src="/api/boards/${board.id}/image" 
                     class="img-fluid mb-3" 
                     onerror="this.style.display='none'"
                     alt="Board image">
                <h5>${board.name}</h5>
                <p>${board.description || 'No description'}</p>
                <p><small class="text-muted">Created on: ${new Date(board.createdAt).toLocaleDateString()}</small></p>
                <p><small class="text-muted">Pins: ${board.pinsCount}</small></p>
                <p><small class="text-muted">${board.private ? 'Private' : 'Public'} board</small></p>
            </div>
        `;

        modal.show();
    }

    // Event listeners
    document.addEventListener('click', function(e) {
        // Edit button handler
        if (e.target.matches('.edit-board-btn')) {
            const boardId = e.target.dataset.boardId;
            const boardCard = e.target.closest('.board-card');
            const currentName = boardCard.querySelector('.card-title').textContent;
            const currentDesc = boardCard.querySelector('.card-text')?.textContent || '';
            const isPrivate = boardCard.querySelector('.private-badge') !== null;

            // Aquí podrías mostrar un modal de edición con los valores actuales
            // y llamar a editBoard() cuando se envíe el formulario
        }

        // Delete button handler
        if (e.target.matches('.delete-board-btn')) {
            const boardId = e.target.dataset.boardId;
            deleteBoard(boardId);
        }

        // View details handler
        if (e.target.matches('.view-board-btn')) {
            const boardId = e.target.dataset.boardId;
            viewBoardDetails(boardId);
        }
    });
});