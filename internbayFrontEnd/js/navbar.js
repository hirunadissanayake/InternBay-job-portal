// navbar.js - Include this file on every page
document.addEventListener('DOMContentLoaded', function() {
    checkUserAuthStatus();
    setActiveNavLink();
});

// Check if user is already logged in on page load
function checkUserAuthStatus() {
    const token = sessionStorage.getItem('authToken');
    const email = sessionStorage.getItem('userEmail');
    const role = sessionStorage.getItem('userRole');

    if (token && email && role) {
        // User is logged in, update navbar
        updateNavbarForLoggedInUser(role, email);
    }
}

// Update navbar based on user role
function updateNavbarForLoggedInUser(userRole, userEmail) {
    const navbar = document.querySelector('.navbar .container');

    if (!navbar) return;

    // Get the navbar collapse div
    const navbarCollapse = navbar.querySelector('.navbar-collapse');

    if (!navbarCollapse) return;

    if (userRole === 'CANDIDATE') {
        // Update navbar for CANDIDATE
        navbarCollapse.innerHTML = `
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="findJob.html">Find Internships</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="myApplications.html">My Applications</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="aboutUs.html">About Us</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="contactUs.html">Contact Us</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="resumeChecker.html">Resume Checker</a>
                </li>
            </ul>
            <div class="d-flex gap-2">
                <button class="btn btn-outline-primary" onclick="showNotifications()">
                    <i class="fas fa-bell me-1"></i>Notifications
                </button>
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                        <i class="fas fa-user me-1"></i>Profile
                    </button>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="candidateProfile.html"><i class="fas fa-user me-2"></i>My Profile</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#" onclick="performLogout()"><i class="fas fa-sign-out-alt me-2"></i>Sign Out</a></li>
                    </ul>
                </div>
            </div>
        `;
    } else if (userRole === 'EMPLOYEE') {
        // Update navbar for EMPLOYEE
        navbarCollapse.innerHTML = `
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="findJob.html">Find Internships</a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="jobDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Post Job
                    </a>
                    <ul class="dropdown-menu" aria-labelledby="jobDropdown">
                        <li><a class="dropdown-item" href="jobPosting.html">
                            <i class="fas fa-plus me-2"></i>Post Job
                        </a></li>
                        <li><a class="dropdown-item" href="manageJobPosting.html">
                            <i class="fas fa-cog me-2"></i>Manage Jobs
                        </a></li>
                    </ul>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="aboutUs.html">About Us</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="contactUs.html">Contact Us</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="resumeChecker.html">Resume Checker</a>
                </li>
            </ul>
            <div class="d-flex gap-2">
                <button class="btn btn-outline-primary" onclick="showNotifications()">
                    <i class="fas fa-bell me-1"></i>Notifications
                </button>
                <div class="dropdown">
                    <button class="btn btn-primary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                        <i class="fas fa-user me-1"></i>Profile
                    </button>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="employerProfile.html"><i class="fas fa-user me-2"></i>My Profile</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="#" onclick="performLogout()"><i class="fas fa-sign-out-alt me-2"></i>Sign Out</a></li>
                    </ul>
                </div>
            </div>
        `;
    }

    // Re-initialize dropdown functionality
    initializeDropdowns();

    // Set active nav link after updating navbar
    setActiveNavLink();
}

// Initialize Bootstrap dropdowns
function initializeDropdowns() {
    const dropdownElementList = [].slice.call(document.querySelectorAll('.dropdown-toggle'));
    const dropdownList = dropdownElementList.map(function (dropdownToggleEl) {
        return new bootstrap.Dropdown(dropdownToggleEl);
    });
}

// Set active nav link based on current page
function setActiveNavLink() {
    const currentPage = window.location.pathname.split('/').pop() || 'main.html';
    const navLinks = document.querySelectorAll('.nav-link');

    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && href.includes(currentPage)) {
            link.classList.add('active');
        } else {
            link.classList.remove('active');
        }
    });
}

// Logout function
function performLogout() {
    // Clear stored data
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('userEmail');
    sessionStorage.removeItem('userRole');

    // Show notification
    showNotification('Logged out successfully!', 'success');

    // Redirect to main page
    setTimeout(() => {
        window.location.href = 'main.html';
    }, 1000);
}

// Additional functions for navbar actions
function showNotifications() {
    showNotification('Notifications feature coming soon!', 'info');
}

function showEmployerDashboard() {
    showNotification('Redirecting to dashboard...', 'info');
    setTimeout(() => {
        window.location.href = 'employerDashboard.html';
    }, 1000);
}

// Utility functions
function showNotification(message, type = 'info') {
    // Remove existing notifications
    if (typeof $ !== 'undefined') {
        $('.notification-toast').remove();

        const alertClass = type === 'error' ? 'danger' : type;
        const iconClass = type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle';

        const toast = $(`
            <div class="alert alert-${alertClass} position-fixed notification-toast" style="top: 20px; right: 20px; z-index: 9999; min-width: 300px;">
                <div class="d-flex align-items-center">
                    <i class="fas fa-${iconClass} me-2"></i>
                    <span>${message}</span>
                    <button type="button" class="btn-close ms-auto" onclick="$(this).closest('.notification-toast').remove()"></button>
                </div>
            </div>
        `);

        $('body').append(toast);

        setTimeout(() => {
            toast.fadeOut(() => toast.remove());
        }, 5000);
    } else {
        // Fallback if jQuery is not loaded
        console.log(type.toUpperCase() + ': ' + message);
    }
}

// Modal functions
function showLoginModal() {
    if (document.getElementById('loginModal')) {
        new bootstrap.Modal(document.getElementById('loginModal')).show();
    } else {
        // Redirect to main page if modal doesn't exist
        window.location.href = 'main.html';
    }
}

function showRegisterModal() {
    if (document.getElementById('registerModal')) {
        new bootstrap.Modal(document.getElementById('registerModal')).show();
    } else {
        // Redirect to main page if modal doesn't exist
        window.location.href = 'main.html';
    }
}