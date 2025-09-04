
    // Initialize page
    document.addEventListener('DOMContentLoaded', function() {
    // Initialize fade-in animations
    initFadeInAnimations();

    // Initialize navbar scroll effect
    initNavbarScrollEffect();

    // Initialize form validations
    initFormValidations();
});

    // Fade-in animation on scroll
    function initFadeInAnimations() {
    const fadeElements = document.querySelectorAll('.fade-in');

    const fadeInObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
    if (entry.isIntersecting) {
    entry.target.classList.add('visible');
}
});
}, { threshold: 0.1 });

    fadeElements.forEach(element => {
    fadeInObserver.observe(element);
});
}

    // Navbar scroll effect
    function initNavbarScrollEffect() {
    const navbar = document.querySelector('.navbar');

    window.addEventListener('scroll', () => {
    if (window.scrollY > 100) {
    navbar.classList.add('shadow-sm');
} else {
    navbar.classList.remove('shadow-sm');
}
});
}

    // Form validations
    function initFormValidations() {
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
    form.addEventListener('submit', function(e) {
    e.preventDefault();
    showNotification('Form submitted successfully!', 'success');
    this.reset();
});
});
}

    // Modal functions (placeholders)
    function showLoginModal() {
    // Implement modal display logic if needed
}

    function showRegisterModal() {
    // Implement modal display logic if needed
}

    // Utility functions
    function showNotification(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type} position-fixed top-0 end-0 m-3`;
    toast.style.zIndex = '9999';
    toast.innerHTML = `
            ${message}
            <button type="button" class="btn-close" onclick="this.parentElement.remove()"></button>
        `;
    document.body.appendChild(toast);

    setTimeout(() => {
    if (toast.parentElement) {
    toast.remove();
}
}, 5000);
}

    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});
