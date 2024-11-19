// Activar todos los tooltips
document.addEventListener('DOMContentLoaded', function() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl)
    });

    // Inicializar carousels
    var carouselList = [].slice.call(document.querySelectorAll('.carousel'))
    carouselList.map(function (carousel) {
        return new bootstrap.Carousel(carousel, {
            interval: 3000
        });
    });
});

// Función para previsualizar imágenes antes de subirlas
function previewImage(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        
        reader.onload = function(e) {
            document.getElementById('imagePreview').src = e.target.result;
            document.getElementById('imagePreview').style.display = 'block';
        }
        
        reader.readAsDataURL(input.files[0]);
    }
}

// Validación del formulario
function validateForm() {
    var nombre = document.getElementById('nombre').value;
    var descripcion = document.getElementById('descripcion').value;
    var tiempoCoccion = document.getElementById('tiempoCoccion').value;
    
    if (nombre.trim() === '') {
        alert('El nombre de la receta es obligatorio');
        return false;
    }
    
    if (descripcion.trim() === '') {
        alert('La descripción es obligatoria');
        return false;
    }
    
    if (tiempoCoccion <= 0) {
        alert('El tiempo de cocción debe ser mayor a 0');
        return false;
    }
    
    return true;
}