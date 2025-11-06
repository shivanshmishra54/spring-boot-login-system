// Wait for the document to be fully loaded before running the script
document.addEventListener("DOMContentLoaded", () => {

    // --- Get All Elements ---
    const emailInput = document.getElementById('email');
    const sendOtpBtn = document.getElementById('send-otp-btn');
    const otpSection = document.getElementById('otp-section');
    const otpSentMessage = document.getElementById('otp-sent-message');

    // --- New Elements ---
    const otpInput = document.getElementById('otp');
    const verifyOtpBtn = document.getElementById('verify-otp-btn');
    const verifySuccessMessage = document.getElementById('verify-success-message');
    const createAccountBtn = document.getElementById('create-account-btn');

    // --- CSRF Token Setup ---
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // Make sure all elements exist
    if (!emailInput || !sendOtpBtn || !otpSection || !otpSentMessage || !otpInput || !verifyOtpBtn || !verifySuccessMessage || !createAccountBtn) {
        console.error("Error: Not all required page elements were found.");
        return;
    }

    // --- Flow 1: Show 'Send OTP' button ---
    emailInput.addEventListener('input', () => {
        if (emailInput.value.length > 5 && emailInput.value.includes('@')) {
            sendOtpBtn.style.display = 'block';
        } else {
            sendOtpBtn.style.display = 'none';
        }
    });

    // --- Flow 2: Click 'Send OTP' button ---
    sendOtpBtn.addEventListener('click', () => {
        const email = emailInput.value;
        sendOtpBtn.disabled = true;
        sendOtpBtn.textContent = 'Sending...';

        fetch('/loginsystem/send-otp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [header]: token
            },
            body: 'email=' + encodeURIComponent(email)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                otpSection.style.display = 'block';
                otpSentMessage.style.display = 'block';
                sendOtpBtn.textContent = 'OTP Sent!';
                emailInput.readOnly = true; // Lock email
            } else {
                alert(data.message || 'Error sending OTP.');
                sendOtpBtn.disabled = false;
                sendOtpBtn.textContent = 'Send OTP';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred. Please try again.');
            sendOtpBtn.disabled = false;
            sendOtpBtn.textContent = 'Send OTP';
        });
    });

    // --- NEW Flow 3: Click 'Verify OTP' button ---
    verifyOtpBtn.addEventListener('click', () => {
        const otp = otpInput.value;
        verifyOtpBtn.disabled = true;
        verifyOtpBtn.textContent = 'Verifying...';

        fetch('/loginsystem/verify-otp', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                [header]: token
            },
            body: 'otp=' + encodeURIComponent(otp)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // --- SUCCESS ---
                verifySuccessMessage.style.display = 'block';
                createAccountBtn.disabled = false; // Enable the final submit button

                // Hide the OTP section
                otpSection.style.display = 'none';
                otpSentMessage.style.display = 'none';
                sendOtpBtn.style.display = 'none';
            } else {
                alert(data.message || 'Invalid OTP.');
                verifyOtpBtn.disabled = false;
                verifyOtpBtn.textContent = 'Verify OTP';
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred. Please try again.');
            verifyOtpBtn.disabled = false;
            verifyOtpBtn.textContent = 'Verify OTP';
        });
    });
});