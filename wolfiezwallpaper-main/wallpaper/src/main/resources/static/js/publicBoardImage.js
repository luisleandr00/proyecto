// Constantes para configuración
const CONFIG = {
    IMAGE_ENDPOINT: '/api/boards',
    DEFAULT_PLACEHOLDER: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgZmlsbD0iI2YwZjBmMCIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LWZhbWlseT0iQXJpYWwiIGZvbnQtc2l6ZT0iMjAiIGZpbGw9IiM5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj5TaW4gaW1hZ2VuPC90ZXh0Pjwvc3ZnPg=='
};

// Función para verificar si una respuesta es una imagen válida
function isValidImageResponse(response) {
    const contentType = response.headers.get('content-type');
    return contentType && contentType.startsWith('image/');
}

// Función para crear un placeholder
function createPlaceholder() {
    const img = document.createElement('img');
    img.src = CONFIG.DEFAULT_PLACEHOLDER;
    img.className = 'card-img-top placeholder-img';
    img.alt = 'Sin imagen';
    return img;
}

// Función para cargar la imagen de un tablero específico con retry
async function loadBoardImage(boardId, imageContainer, retryCount = 0) {
    const maxRetries = 2;

    try {
        // Mostrar placeholder mientras carga
        imageContainer.innerHTML = '';
        imageContainer.appendChild(createPlaceholder());

        // Intentar cargar la imagen
        const response = await fetch(`${CONFIG.IMAGE_ENDPOINT}/${boardId}/image`, {
            method: 'GET',
            headers: {
                'Accept': 'image/jpeg, image/png, image/gif',
                'Cache-Control': 'no-cache'
            }
        });

        // Verificar si la respuesta es válida
        if (response.ok && isValidImageResponse(response)) {
            const blob = await response.blob();

            // Verificar si el blob tiene contenido
            if (blob.size > 0) {
                const imageUrl = URL.createObjectURL(blob);
                const img = new Image();

                img.onload = () => {
                    URL.revokeObjectURL(imageUrl); // Liberar memoria
                    const finalImg = document.createElement('img');
                    finalImg.src = imageUrl;
                    finalImg.className = 'card-img-top';
                    finalImg.alt = 'Board Cover';

                    imageContainer.innerHTML = '';
                    imageContainer.appendChild(finalImg);
                };

                img.onerror = () => {
                    URL.revokeObjectURL(imageUrl);
                    imageContainer.innerHTML = '';
                    imageContainer.appendChild(createPlaceholder());
                };

                img.src = imageUrl;
            } else {
                throw new Error('Empty image blob');
            }
        } else {
            throw new Error(`Invalid response: ${response.status}`);
        }
    } catch (error) {
        console.warn(`Error loading image for board ${boardId}:`, error);

        // Intentar retry si no hemos alcanzado el máximo
        if (retryCount < maxRetries) {
            setTimeout(() => {
                loadBoardImage(boardId, imageContainer, retryCount + 1);
            }, 1000 * (retryCount + 1)); // Espera exponencial
        } else {
            imageContainer.innerHTML = '';
            imageContainer.appendChild(createPlaceholder());
        }
    }
}

// Función principal para inicializar la carga de imágenes
function initializeBoardImages() {
    // Verificar soporte de IntersectionObserver
    if (!('IntersectionObserver' in window)) {
        console.warn('IntersectionObserver not supported, loading all images immediately');
        document.querySelectorAll('.card-img-top-wrapper').forEach(container => {
            const boardId = container.closest('[data-board-id]')?.dataset.boardId;
            if (boardId) {
                loadBoardImage(boardId, container);
            }
        });
        return;
    }

    // Configurar el observador de intersección
    const imageObserver = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const container = entry.target;
                const boardCard = container.closest('[data-board-id]');
                const boardId = boardCard?.dataset.boardId;

                if (boardId) {
                    loadBoardImage(boardId, container);
                    observer.unobserve(container);
                }
            }
        });
    }, {
        root: null,
        rootMargin: '50px',
        threshold: 0.1
    });

    // Observar cada contenedor de imagen
    document.querySelectorAll('.card-img-top-wrapper').forEach(container => {
        imageObserver.observe(container);
    });
}

// Estilos CSS mejorados
const styles = `
    .card-img-top-wrapper {
        position: relative;
        width: 100%;
        padding-top: 75%;
        background-color: #f8f9fa;
        overflow: hidden;
    }

    .card-img-top {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: opacity 0.3s ease;
    }

    .placeholder-img {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: #f8f9fa;
        object-fit: contain;
        padding: 1rem;
    }

    .card {
        transition: transform 0.2s ease;
    }

    .card:hover {
        transform: translateY(-5px);
    }
`;

// Agregar los estilos al documento
const styleSheet = document.createElement('style');
styleSheet.textContent = styles;
document.head.appendChild(styleSheet);

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', initializeBoardImages);

// Reintentar cargar imágenes cuando la página vuelve a estar visible
document.addEventListener('visibilitychange', () => {
    if (!document.hidden) {
        document.querySelectorAll('.placeholder-img').forEach(placeholder => {
            const container = placeholder.closest('.card-img-top-wrapper');
            const boardId = container?.closest('[data-board-id]')?.dataset.boardId;
            if (boardId) {
                loadBoardImage(boardId, container);
            }
        });
    }
});