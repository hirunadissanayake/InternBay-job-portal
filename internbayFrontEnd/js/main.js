/*

    // Initialize page
    document.addEventListener('DOMContentLoaded', function() {
    // Initialize fade-in animations
    initFadeInAnimations();

    // Initialize navbar scroll effect
    initNavbarScrollEffect();

    // Initialize form validations
    initFormValidations();

    // Initialize resume button toggle
    initResumeButtonToggle();

    // Initialize file uploads with jQuery
    initCloudinaryUploads();
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
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
    registerForm.addEventListener('submit', function(e) {
    e.preventDefault();

    // Get form data
    const firstName = document.getElementById('regFirstName').value.trim();
    const lastName = document.getElementById('regLastName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;
    const confirmPassword = document.getElementById('regConfirmPassword').value;
    const phone = document.getElementById('regPhone').value.trim();

    // Get role from radio buttons
    const studentRadio = document.getElementById('studentAccount');
    const employerRadio = document.getElementById('employerAccount');
    const role = studentRadio.checked ? 'CANDIDATE' : 'EMPLOYEE';

    // Get Cloudinary URLs
    const profilePicUrl = document.getElementById('profilePicUrl').value || null;
    const resumeUrl = document.getElementById('resumeUrl').value || null;

    // Validation
    if (!firstName || !lastName || !email || !password || !phone) {
    showNotification('Please fill in all required fields.', 'error');
    return;
}

    if (password !== confirmPassword) {
    showNotification('Passwords do not match.', 'error');
    return;
}

    if (password.length < 6) {
    showNotification('Password must be at least 6 characters long.', 'error');
    return;
}

    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
    showNotification('Please enter a valid email address.', 'error');
    return;
}

    // Check if student selected but no resume uploaded
    if (role === 'CANDIDATE' && !resumeUrl) {
    showNotification('Please upload your resume.', 'error');
    return;
}

    // Prepare user data according to UserDTO structure
    const userData = {
    firstName: firstName,
    lastName: lastName,
    email: email,
    passwordHash: password, // Backend should hash this
    role: role,
    phone: phone,
    profilePic: profilePicUrl,
    resume: resumeUrl
};

    // Save user via AJAX
    saveUser(userData);
});
}
}

    function saveUser(userData) {
    // Show loading state
    const submitBtn = document.querySelector('#registerForm button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Creating Account...';
    submitBtn.disabled = true;

    $.ajax({
    url: 'http://localhost:8080/api/v1/user/register', // Adjust this URL to match your backend endpoint
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify(userData),
    success: function(response) {
    showNotification('Account created successfully! Welcome to InternBay!', 'success');

    // Reset form
    document.getElementById('registerForm').reset();

    // Clear file upload statuses
    document.getElementById('profilePicName').textContent = '';
    document.getElementById('resumeName').textContent = '';
    document.getElementById('profilePicUrl').value = '';
    document.getElementById('resumeUrl').value = '';

    // Close modal after 2 seconds
    setTimeout(() => {
    const modal = bootstrap.Modal.getInstance(document.getElementById('registerModal'));
    if (modal) {
    modal.hide();
}
}, 2000);
},
    error: function(xhr, status, error) {
    let errorMessage = 'Registration failed. Please try again.';

    // Handle specific error responses
    if (xhr.responseJSON && xhr.responseJSON.message) {
    errorMessage = xhr.responseJSON.message;
} else if (xhr.status === 400) {
    errorMessage = 'Invalid data provided. Please check your inputs.';
} else if (xhr.status === 409) {
    errorMessage = 'Email already exists. Please use a different email.';
} else if (xhr.status === 500) {
    errorMessage = 'Server error. Please try again later.';
}

    showNotification(errorMessage, 'error');
},
    complete: function() {
    // Restore button state
    submitBtn.innerHTML = originalText;
    submitBtn.disabled = false;
}
});
}



    // Resume button toggle
    function initResumeButtonToggle() {
    const studentRadio = document.getElementById("studentAccount");
    const employerRadio = document.getElementById("employerAccount");
    const resumeBtn = document.getElementById("resumeUpload");
    const resumeProgress = document.getElementById("resumeProgress");
    const resumeName = document.getElementById("resumeName");

    if (!studentRadio || !employerRadio || !resumeBtn) return;

    function toggleResumeButton() {
    const isEmployer = employerRadio.checked;
    resumeBtn.style.display = isEmployer ? "none" : "block";
    if (resumeProgress) resumeProgress.style.display = isEmployer ? "none" : "none";
    if (resumeName && isEmployer) {
    resumeName.textContent = "";
    resumeName.className = "file-upload-status";
}
}

    // Run on page load
    toggleResumeButton();

    // Add listeners
    studentRadio.addEventListener("change", toggleResumeButton);
    employerRadio.addEventListener("change", toggleResumeButton);
}

    // Initialize Cloudinary uploads with proper error handling
    function initCloudinaryUploads() {
    // Open file picker on button click
    $('#uploadProfilePic').click(() => $('#profilePicInput').click());
    $('#resumeUpload').click(() => $('#resumeInput').click());

    // Profile Picture Upload
    $('#profilePicInput').change(function () {
    const file = this.files[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith('image/')) {
    showNotification('Please select an image file (JPG, PNG, etc.)', 'error');
    return;
}

    // Validate file size (5MB limit)
    if (file.size > 5 * 1024 * 1024) {
    showNotification('Image size must be less than 5MB', 'error');
    return;
}

    uploadToCloudinary(file, 'profilePic');
});

    // Resume Upload
    $('#resumeInput').change(function () {
    const file = this.files[0];
    if (!file) return;

    // Validate file type
    const validTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
    if (!validTypes.includes(file.type) && !file.name.toLowerCase().match(/\.(pdf|doc|docx)$/)) {
    showNotification('Please select a PDF or Word document', 'error');
    return;
}

    // Validate file size (10MB limit)
    if (file.size > 10 * 1024 * 1024) {
    showNotification('Resume size must be less than 10MB', 'error');
    return;
}

    uploadToCloudinary(file, 'resume');
});
}


    function uploadToCloudinary(file, type) {
    const isProfilePic = type === 'profilePic';
    const urlElement = $(isProfilePic ? '#profilePicUrl' : '#resumeUrl');
    const statusElement = $(isProfilePic ? '#profilePicName' : '#resumeName');
    const buttonElement = $(isProfilePic ? '#uploadProfilePic' : '#resumeUpload');

    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", "hiruna"); // Use your unsigned preset

    const endpoint = isProfilePic
    ? "https://api.cloudinary.com/v1_1/ddhlsfx3z/image/upload"
    : "https://api.cloudinary.com/v1_1/ddhlsfx3z/raw/upload";

    $.ajax({
    url: endpoint,
    type: "POST",
    data: formData,
    processData: false,
    contentType: false,
    success: function (response) {
    urlElement.val(response.secure_url);
    statusElement.text(`✓ Upload successful: ${file.name}`).removeClass().addClass('file-upload-status success');
},
    error: function (xhr, status, error) {
    statusElement.text(`✗ Upload failed: ${error}`).removeClass().addClass('file-upload-status error');
},
    complete: function() {
    buttonElement.prop('disabled', false);
    buttonElement.html(isProfilePic
    ? '<i class="fas fa-camera me-2"></i>Upload Profile Picture'
    : '<i class="fas fa-file-upload me-2"></i>Upload Resume'
    );
}
});
}




    // Utility function to format file size
    function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

    // Modal functions
    function showLoginModal() {
    new bootstrap.Modal(document.getElementById('loginModal')).show();
}

    function showRegisterModal() {
    new bootstrap.Modal(document.getElementById('registerModal')).show();
}

    function switchToRegister() {
    bootstrap.Modal.getInstance(document.getElementById('loginModal')).hide();
    setTimeout(() => showRegisterModal(), 300);
}

    function switchToLogin() {
    bootstrap.Modal.getInstance(document.getElementById('registerModal')).hide();
    setTimeout(() => showLoginModal(), 300);
}

    // Page navigation functions
    function showJobsPage() {
    showNotification('Navigating to Jobs page...', 'info');
}

    function showJobDetails() {
    new bootstrap.Modal(document.getElementById('jobDetailsModal')).show();
}

    function showResumeChecker() {
    new bootstrap.Modal(document.getElementById('resumeCheckerModal')).show();
}

    // Utility functions
    function showNotification(message, type = 'info') {
    // Remove existing notifications
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

    // Job card click handlers
    document.querySelectorAll('.job-card').forEach(card => {
    card.style.cursor = 'pointer';
    card.addEventListener('click', function() {
    showJobDetails();
});
});



*/
// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    // Initialize fade-in animations
    initFadeInAnimations();

    // Initialize navbar scroll effect
    initNavbarScrollEffect();

    // Initialize form validations
    initFormValidations();

    // Initialize resume button toggle
    initResumeButtonToggle();

    // Initialize file uploads with jQuery
    initCloudinaryUploads();

    // Initialize login form validation
    initLoginFormValidation();

    // Check user auth status and update navbar
    checkUserAuthStatus();
});

// Login form validation and submission
function initLoginFormValidation() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();

            // Get form data
            const email = document.getElementById('loginEmail').value.trim();
            const password = document.getElementById('loginPassword').value;

            // Validation
            if (!email || !password) {
                showNotification('Please fill in all fields.', 'error');
                return;
            }

            // Email validation
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                showNotification('Please enter a valid email address.', 'error');
                return;
            }

            // Prepare login data
            const loginData = {
                email: email,
                passwordHash: password // Backend expects passwordHash field
            };

            // Perform login
            performLogin(loginData);
        });
    }
}

function performLogin(loginData) {
    // Show loading state
    const submitBtn = document.querySelector('#loginForm button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Logging In...';
    submitBtn.disabled = true;

    $.ajax({
        url: 'http://localhost:8080/api/v1/user/login',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(loginData),
        success: function(response) {
            showNotification('Login successful! Welcome back!', 'success');

            // Store user data and token in sessionStorage
            if (response.data && response.data.token) {
                sessionStorage.setItem('authToken', response.data.token);
                sessionStorage.setItem('userEmail', response.data.email);
                sessionStorage.setItem('userRole', response.data.role);
            }

            // Reset form
            document.getElementById('loginForm').reset();

            // Close modal after 1.5 seconds
            setTimeout(() => {
                const modal = bootstrap.Modal.getInstance(document.getElementById('loginModal'));
                if (modal) {
                    modal.hide();
                }

                // Update navbar and handle post-login actions
                updateNavbarForLoggedInUser(response.data.role, response.data.email);
                handlePostLoginActions(response.data);
            }, 1500);
        },
        error: function(xhr, status, error) {
            let errorMessage = 'Login failed. Please try again.';

            // Handle specific error responses
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            } else if (xhr.status === 401) {
                errorMessage = 'Invalid email or password.';
            } else if (xhr.status === 406) {
                errorMessage = 'Account not found. Please check your email.';
            } else if (xhr.status === 500) {
                errorMessage = 'Server error. Please try again later.';
            }

            showNotification(errorMessage, 'error');
        },
        complete: function() {
            // Restore button state
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        }
    });
}

// Handle actions after successful login
function handlePostLoginActions(userData) {
    // Show role-specific welcome message
    if (userData.role === 'CANDIDATE') {
        showNotification('Welcome! You can now browse internships and track your applications.', 'info');
    } else if (userData.role === 'EMPLOYEE') {
        showNotification('Welcome! You can now post jobs and manage applications.', 'info');
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
}

// Initialize Bootstrap dropdowns
function initializeDropdowns() {
    // Re-initialize any Bootstrap dropdowns that were added dynamically
    const dropdownElementList = [].slice.call(document.querySelectorAll('.dropdown-toggle'));
    const dropdownList = dropdownElementList.map(function (dropdownToggleEl) {
        return new bootstrap.Dropdown(dropdownToggleEl);
    });
}

// Restore default navbar (for logout)
function restoreDefaultNavbar() {
    const navbar = document.querySelector('.navbar .container');

    if (!navbar) return;

    // Get the navbar collapse div
    const navbarCollapse = navbar.querySelector('.navbar-collapse');

    if (!navbarCollapse) return;

    // Restore original navbar
    navbarCollapse.innerHTML = `
        <ul class="navbar-nav me-auto">
            <li class="nav-item">
                <a class="nav-link" href="findJob.html">Find Internships</a>
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
            <button class="btn btn-outline-primary" onclick="showLoginModal()">
                <i class="fas fa-sign-in-alt me-1"></i>Sign In
            </button>
            <button class="btn btn-primary" onclick="showRegisterModal()">
                <i class="fas fa-user-plus me-1"></i>Sign Up
            </button>
        </div>
    `;
}

// Logout function
function performLogout() {
    // Clear stored data
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('userEmail');
    sessionStorage.removeItem('userRole');

    // Show notification
    showNotification('Logged out successfully!', 'success');

    // Restore default navbar
    restoreDefaultNavbar();

    // Optionally redirect to home page
    setTimeout(() => {
        window.location.href = 'main.html';
    }, 1000);
}

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

// Additional functions for navbar actions
function showNotifications() {
    showNotification('Notifications feature coming soon!', 'info');
    // You can implement a notifications modal or redirect to notifications page
}

function showEmployerDashboard() {
    showNotification('Redirecting to dashboard...', 'info');
    // Redirect to employer dashboard
    setTimeout(() => {
        window.location.href = 'employerDashboard.html';
    }, 1000);
}

// Registration success handler (update this in your existing saveUser function)
function handleRegistrationSuccess(response) {
    showNotification('Account created successfully! Welcome to InternBay!', 'success');

    // Note: Registration doesn't return token, so navbar stays the same
    // User needs to log in to get the role-specific navbar

    // Reset form
    document.getElementById('registerForm').reset();

    // Clear file upload statuses
    document.getElementById('profilePicName').textContent = '';
    document.getElementById('resumeName').textContent = '';
    document.getElementById('profilePicUrl').value = '';
    document.getElementById('resumeUrl').value = '';

    // Close modal after 2 seconds
    setTimeout(() => {
        const modal = bootstrap.Modal.getInstance(document.getElementById('registerModal'));
        if (modal) {
            modal.hide();
        }

        // Show login suggestion
        showNotification('Please log in to access all features!', 'info');
    }, 2000);
}

// Update your existing saveUser function success handler
function saveUser(userData) {
    // Show loading state
    const submitBtn = document.querySelector('#registerForm button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Creating Account...';
    submitBtn.disabled = true;

    $.ajax({
        url: 'http://localhost:8080/api/v1/user/register',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(userData),
        success: function(response) {
            handleRegistrationSuccess(response); // Use the new handler
        },
        error: function(xhr, status, error) {
            let errorMessage = 'Registration failed. Please try again.';

            // Handle specific error responses
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            } else if (xhr.status === 400) {
                errorMessage = 'Invalid data provided. Please check your inputs.';
            } else if (xhr.status === 409) {
                errorMessage = 'Email already exists. Please use a different email.';
            } else if (xhr.status === 500) {
                errorMessage = 'Server error. Please try again later.';
            }

            showNotification(errorMessage, 'error');
        },
        complete: function() {
            // Restore button state
            submitBtn.innerHTML = originalText;
            submitBtn.disabled = false;
        }
    });
}

// Utility function to get auth headers for API calls
function getAuthHeaders() {
    const token = sessionStorage.getItem('authToken');
    return token ? {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
    } : {
        'Content-Type': 'application/json'
    };
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

// Call setActiveNavLink on page load
document.addEventListener('DOMContentLoaded', function() {
    setActiveNavLink();
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
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();

            // Get form data
            const firstName = document.getElementById('regFirstName').value.trim();
            const lastName = document.getElementById('regLastName').value.trim();
            const email = document.getElementById('regEmail').value.trim();
            const password = document.getElementById('regPassword').value;
            const confirmPassword = document.getElementById('regConfirmPassword').value;
            const phone = document.getElementById('regPhone').value.trim();

            // Get role from radio buttons
            const studentRadio = document.getElementById('studentAccount');
            const employerRadio = document.getElementById('employerAccount');
            const role = studentRadio.checked ? 'CANDIDATE' : 'EMPLOYEE';

            // Get Cloudinary URLs
            const profilePicUrl = document.getElementById('profilePicUrl').value || null;
            const resumeUrl = document.getElementById('resumeUrl').value || null;

            // Validation
            if (!firstName || !lastName || !email || !password || !phone) {
                showNotification('Please fill in all required fields.', 'error');
                return;
            }

            if (password !== confirmPassword) {
                showNotification('Passwords do not match.', 'error');
                return;
            }

            if (password.length < 6) {
                showNotification('Password must be at least 6 characters long.', 'error');
                return;
            }

            // Email validation
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                showNotification('Please enter a valid email address.', 'error');
                return;
            }

            // Check if student selected but no resume uploaded
            if (role === 'CANDIDATE' && !resumeUrl) {
                showNotification('Please upload your resume.', 'error');
                return;
            }

            // Prepare user data according to UserDTO structure
            const userData = {
                firstName: firstName,
                lastName: lastName,
                email: email,
                passwordHash: password, // Backend should hash this
                role: role,
                phone: phone,
                profilePic: profilePicUrl,
                resume: resumeUrl
            };

            // Save user via AJAX
            saveUser(userData);
        });
    }
}

// Resume button toggle
function initResumeButtonToggle() {
    const studentRadio = document.getElementById("studentAccount");
    const employerRadio = document.getElementById("employerAccount");
    const resumeBtn = document.getElementById("resumeUpload");
    const resumeProgress = document.getElementById("resumeProgress");
    const resumeName = document.getElementById("resumeName");

    if (!studentRadio || !employerRadio || !resumeBtn) return;

    function toggleResumeButton() {
        const isEmployer = employerRadio.checked;
        resumeBtn.style.display = isEmployer ? "none" : "block";
        if (resumeProgress) resumeProgress.style.display = isEmployer ? "none" : "none";
        if (resumeName && isEmployer) {
            resumeName.textContent = "";
            resumeName.className = "file-upload-status";
        }
    }

    // Run on page load
    toggleResumeButton();

    // Add listeners
    studentRadio.addEventListener("change", toggleResumeButton);
    employerRadio.addEventListener("change", toggleResumeButton);
}

// Initialize Cloudinary uploads with proper error handling
function initCloudinaryUploads() {
    // Open file picker on button click
    $('#uploadProfilePic').click(() => $('#profilePicInput').click());
    $('#resumeUpload').click(() => $('#resumeInput').click());

    // Profile Picture Upload
    $('#profilePicInput').change(function () {
        const file = this.files[0];
        if (!file) return;

        // Validate file type
        if (!file.type.startsWith('image/')) {
            showNotification('Please select an image file (JPG, PNG, etc.)', 'error');
            return;
        }

        // Validate file size (5MB limit)
        if (file.size > 5 * 1024 * 1024) {
            showNotification('Image size must be less than 5MB', 'error');
            return;
        }

        uploadToCloudinary(file, 'profilePic');
    });

    // Resume Upload
    $('#resumeInput').change(function () {
        const file = this.files[0];
        if (!file) return;

        // Validate file type
        const validTypes = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'];
        if (!validTypes.includes(file.type) && !file.name.toLowerCase().match(/\.(pdf|doc|docx)$/)) {
            showNotification('Please select a PDF or Word document', 'error');
            return;
        }

        // Validate file size (10MB limit)
        if (file.size > 10 * 1024 * 1024) {
            showNotification('Resume size must be less than 10MB', 'error');
            return;
        }

        uploadToCloudinary(file, 'resume');
    });
}

function uploadToCloudinary(file, type) {
    const isProfilePic = type === 'profilePic';
    const urlElement = $(isProfilePic ? '#profilePicUrl' : '#resumeUrl');
    const statusElement = $(isProfilePic ? '#profilePicName' : '#resumeName');
    const buttonElement = $(isProfilePic ? '#uploadProfilePic' : '#resumeUpload');

    const formData = new FormData();
    formData.append("file", file);
    formData.append("upload_preset", "hiruna");

    const endpoint = isProfilePic
        ? "https://api.cloudinary.com/v1_1/ddhlsfx3z/image/upload"
        : "https://api.cloudinary.com/v1_1/ddhlsfx3z/raw/upload";

    $.ajax({
        url: endpoint,
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (response) {
            urlElement.val(response.secure_url);
            statusElement.text(`✓ Upload successful: ${file.name}`).removeClass().addClass('file-upload-status success');
        },
        error: function (xhr, status, error) {
            statusElement.text(`✗ Upload failed: ${error}`).removeClass().addClass('file-upload-status error');
        },
        complete: function() {
            buttonElement.prop('disabled', false);
            buttonElement.html(isProfilePic
                ? '<i class="fas fa-camera me-2"></i>Upload Profile Picture'
                : '<i class="fas fa-file-upload me-2"></i>Upload Resume'
            );
        }
    });
}

// Utility function to format file size
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// Modal functions
function showLoginModal() {
    new bootstrap.Modal(document.getElementById('loginModal')).show();
}

function showRegisterModal() {
    new bootstrap.Modal(document.getElementById('registerModal')).show();
}

function switchToRegister() {
    bootstrap.Modal.getInstance(document.getElementById('loginModal')).hide();
    setTimeout(() => showRegisterModal(), 300);
}

function switchToLogin() {
    bootstrap.Modal.getInstance(document.getElementById('registerModal')).hide();
    setTimeout(() => showLoginModal(), 300);
}

// Page navigation functions
function showJobsPage() {
    showNotification('Navigating to Jobs page...', 'info');
}

function showJobDetails() {
    new bootstrap.Modal(document.getElementById('jobDetailsModal')).show();
}

function showResumeChecker() {
    new bootstrap.Modal(document.getElementById('resumeCheckerModal')).show();
}

// Utility functions
function showNotification(message, type = 'info') {
    // Remove existing notifications
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

// Job card click handlers
document.querySelectorAll('.job-card').forEach(card => {
    card.style.cursor = 'pointer';
    card.addEventListener('click', function() {
        showJobDetails();
    });
});

// Load featured jobs
async function loadFeaturedJobs() {
    try {
        // Get featured jobs (latest jobs with limit)
        const response = await fetch('http://localhost:8080/api/v1/jobs/search?page=0&size=6&sort=datePosted,desc', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json();
            if (data.data && data.data.content) {
                displayFeaturedJobs(data.data.content);
            } else {
                throw new Error('Invalid jobs data format');
            }
        } else {
            throw new Error('Failed to fetch jobs');
        }
    } catch (error) {
        console.error('Error loading featured jobs:', error);
        displayFallbackJobs();
    }
}

// Display featured jobs
function displayFeaturedJobs(jobs) {
    const container = document.getElementById('featuredJobsContainer');
    if (!container) return;

    let html = '';
    jobs.forEach(job => {
        html += createJobCard(job);
    });

    container.innerHTML = html;

    // Re-initialize animations
    setTimeout(() => {
        const newElements = container.querySelectorAll('.fade-in');
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                }
            });
        }, { threshold: 0.1 });

        newElements.forEach(el => observer.observe(el));
    }, 100);
}

// Create job card HTML
function createJobCard(job) {
    const formattedDate = formatDate(job.datePosted);
    const salary = job.salaryPerHour ? `$${job.salaryPerHour}/hr` : 'Negotiable';

    return `
        <div class="col-lg-6">
            <div class="job-card fade-in" onclick="showJobDetails(${job.jobId})">
                <div class="d-flex">
                    <div class="company-logo">
                        ${job.companyLogo ?
        `<img src="${job.companyLogo}" alt="${job.companyName}" style="width: 100%; height: 100%; object-fit: cover; border-radius: 8px;">` :
        `<i class="fas fa-building text-primary"></i>`
    }
                    </div>
                    <div class="flex-grow-1">
                        <h5 class="job-title">${job.title}</h5>
                        <div class="job-meta mb-3">
                            <span><i class="fas fa-building me-1"></i>${job.companyName || 'Company'}</span>
                            <span><i class="fas fa-map-marker-alt me-1"></i>${job.location}</span>
                            <span><i class="fas fa-clock me-1"></i>${formatJobType(job.jobType)}</span>
                            <span><i class="fas fa-dollar-sign me-1"></i>${salary}</span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center">
                            <small class="text-muted">${formattedDate}</small>
                            <button class="btn btn-primary btn-sm" onclick="event.stopPropagation(); applyToJob(${job.jobId})">Apply Now</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Display fallback jobs if API fails
function displayFallbackJobs() {
    const fallbackJobs = [
        {
            jobId: 1,
            title: 'Full-Stack Development Intern',
            companyName: 'TechCorp',
            location: 'Remote',
            jobType: 'FULLTIME',
            salaryPerHour: 25,
            datePosted: new Date().toISOString()
        },
        {
            jobId: 2,
            title: 'Data Science Intern',
            companyName: 'DataFlow Inc',
            location: 'New York',
            jobType: 'PARTTIME',
            salaryPerHour: 22,
            datePosted: new Date(Date.now() - 86400000 * 2).toISOString()
        },
        {
            jobId: 3,
            title: 'UI/UX Design Intern',
            companyName: 'DesignStudio',
            location: 'San Francisco',
            jobType: 'INTERNSHIP',
            salaryPerHour: 20,
            datePosted: new Date(Date.now() - 86400000 * 3).toISOString()
        },
        {
            jobId: 4,
            title: 'Marketing Intern',
            companyName: 'BrandMakers',
            location: 'Los Angeles',
            jobType: 'INTERNSHIP',
            salaryPerHour: 18,
            datePosted: new Date(Date.now() - 86400000 * 5).toISOString()
        },
        {
            jobId: 5,
            title: 'Finance Analyst Intern',
            companyName: 'FinanceHub',
            location: 'Chicago',
            jobType: 'INTERNSHIP',
            salaryPerHour: 20,
            datePosted: new Date(Date.now() - 86400000 * 7).toISOString()
        },
        {
            jobId: 6,
            title: 'Business Development Intern',
            companyName: 'GrowthCorp',
            location: 'Boston',
            jobType: 'PARTTIME',
            salaryPerHour: 19,
            datePosted: new Date(Date.now() - 86400000 * 10).toISOString()
        }
    ];

    displayFeaturedJobs(fallbackJobs);
}

// Utility functions for job display
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diffTime = Math.abs(now - date);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays === 1) return 'Posted Today';
    if (diffDays < 7) return `Posted ${diffDays} days ago`;
    if (diffDays < 30) return `Posted ${Math.ceil(diffDays / 7)} weeks ago`;
    return `Posted ${Math.ceil(diffDays / 30)} months ago`;
}

function formatJobType(jobType) {
    const types = {
        'FULLTIME': 'Full Time',
        'PARTTIME': 'Part Time',
        'INTERNSHIP': 'Internship',
        'CONTRACT': 'Contract',
        'REMOTE': 'Remote'
    };
    return types[jobType] || jobType;
}

function showJobDetails(jobId) {
    // Redirect to find job page with specific job selected
    window.location.href = `findJob.html?job=${jobId}`;
}

function applyToJob(jobId) {
    const token = sessionStorage.getItem('authToken');
    const role = sessionStorage.getItem('userRole');

    if (!token) {
        showNotification('Please log in to apply for jobs', 'error');
        showLoginModal();
        return;
    }

    if (role !== 'CANDIDATE') {
        showNotification('Only students can apply for jobs', 'error');
        return;
    }

    // Redirect to job details for application
    window.location.href = `findJob.html?job=${jobId}`;
}

// Call this function when the page loads
document.addEventListener('DOMContentLoaded', function() {
    loadFeaturedJobs();
});

// Notification system (required by applyToJob function)
function showNotification(message, type = 'info') {
    // Remove existing notifications
    const existingToasts = document.querySelectorAll('.notification-toast');
    existingToasts.forEach(toast => toast.remove());

    const alertClass = type === 'error' ? 'danger' : type;
    const iconClass = type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle';

    const toast = document.createElement('div');
    toast.className = `alert alert-${alertClass} position-fixed notification-toast`;
    toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';

    toast.innerHTML = `
        <div class="d-flex align-items-center">
            <i class="fas fa-${iconClass} me-2"></i>
            <span>${message}</span>
            <button type="button" class="btn-close ms-auto" onclick="this.closest('.notification-toast').remove()"></button>
        </div>
    `;

    document.body.appendChild(toast);

    // Auto remove after 5 seconds
    setTimeout(() => {
        if (toast.parentNode) {
            toast.remove();
        }
    }, 5000);
}

// Modal functions (required by applyToJob function)
function showLoginModal() {
    const loginModal = document.getElementById('loginModal');
    if (loginModal) {
        new bootstrap.Modal(loginModal).show();
    }
}
// Hero Section Related Scripts

// Load job statistics for hero section stats cards
async function loadJobStats() {
    try {
        // Load total jobs count
        const jobsResponse = await fetch('http://localhost:8080/api/v1/jobs/search?page=0&size=1', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (jobsResponse.ok) {
            const jobsData = await jobsResponse.json();
            const totalJobs = jobsData.data.totalElements;
            updateJobCount(totalJobs);
        } else {
            // Fallback if API fails
            updateJobCount(2500);
        }

        // Load other stats (these might come from different endpoints or be hardcoded)
        updateOtherStats();
    } catch (error) {
        console.error('Error loading job stats:', error);
        // Use fallback values
        updateJobCount(2500);
        updateOtherStats();
    }
}

// Update job count with animation
function updateJobCount(count) {
    const element = document.getElementById('totalJobs');
    if (element) {
        animateNumber(element, 0, count, 2000, '+');
    }
}

// Update other statistics in hero section
function updateOtherStats() {
    // These could be loaded from API endpoints or calculated
    const stats = {
        totalCompanies: 10,
        totalPlacements: 50,
        successRate: 95
    };

    // Animate company count
    const companiesEl = document.getElementById('totalCompanies');
    if (companiesEl) {
        animateNumber(companiesEl, 0, stats.totalCompanies, 2000, '+');
    }

    // Animate placements count
    const placementsEl = document.getElementById('totalPlacements');
    if (placementsEl) {
        animateNumber(placementsEl, 0, stats.totalPlacements, 2000, '+');
    }

    // Animate success rate
    const successEl = document.getElementById('successRate');
    if (successEl) {
        animateNumber(successEl, 0, stats.successRate, 2000, '%');
    }
}

// Animate numbers in hero stats cards
function animateNumber(element, start, end, duration, suffix = '') {
    const range = end - start;
    const increment = end > start ? 1 : -1;
    const stepTime = Math.abs(Math.floor(duration / range));
    let current = start;

    const timer = setInterval(() => {
        current += increment;
        if (suffix === 'K+' && end >= 1000) {
            element.textContent = (current / 1000).toFixed(0) + 'K+';
        } else {
            element.textContent = current.toLocaleString() + suffix;
        }

        if (current === end) {
            clearInterval(timer);
        }
    }, stepTime);
}

// Initialize animations (including hero section fade-in animations)
function initializeAnimations() {
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
            }
        });
    }, observerOptions);

    // Observe all fade-in elements (including hero section elements)
    document.querySelectorAll('.fade-in').forEach(el => {
        observer.observe(el);
    });
}

// Main initialization function - call this on page load
document.addEventListener('DOMContentLoaded', function() {
    // Initialize hero section animations
    initializeAnimations();

    // Load hero section statistics
    loadJobStats();

    // Other initialization functions...
    // checkUserAuthStatus();
    // loadCategories();
    // loadFeaturedJobs();
    // setupEventListeners();
});
