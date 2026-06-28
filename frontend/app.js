const BASE_URL = "http://localhost:8088/api/patient";

// Basic Authorization header credentials
const credentials = "admin:HospitalAdmin2026!";
const AUTH_HEADER = "Basic " + btoa(credentials);

// 🌟 Safe event listener wrappers to support multiple pages without crashing
if (document.getElementById("btnLoadSchedules")) {
    document.getElementById("btnLoadSchedules").addEventListener("click", loadSchedules);
}
if (document.getElementById("bookingForm")) {
    document.getElementById("bookingForm").addEventListener("submit", bookAppointment);
}
if (document.getElementById("btnResetSchedules")) {
    document.getElementById("btnResetSchedules").addEventListener("click", resetAllSystemSlots);
}

// Automatically load the appropriate view tables depending on which page is open
// 🌟 FIX: Automatically load tables safely depending on which page is open
if (document.getElementById("schedulesBody")) {
    loadSchedules();
}
if (document.getElementById("bookedSchedulesBody")) {
    loadBookedSchedules();
} 

// --- APPOINTMENT WORKFLOW FUNCTIONS ---

async function loadSchedules() {
    const tbody = document.getElementById("schedulesBody");
    tbody.innerHTML = `<tr><td colspan="4" class="text-center">Loading schedules...</td></tr>`;
    try {
        const response = await fetch(`${BASE_URL}/schedules`, {
            method: "GET",
            headers: { "Authorization": AUTH_HEADER }
        });
        if (!response.ok) throw new Error("Could not fetch records.");
        const schedules = await response.json();
        tbody.innerHTML = ""; 
        if (schedules.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">No available slots open right now!</td></tr>`;
            
            if (document.getElementById("scheduleId")) {
                document.getElementById("scheduleId").disabled = true;
                document.getElementById("patientEmail").disabled = true;
                document.querySelector("#bookingForm button[type='submit']").disabled = true;
            }
            return;
        }
        
        if (document.getElementById("scheduleId")) {
            document.getElementById("scheduleId").disabled = false;
            document.getElementById("patientEmail").disabled = false;
            document.querySelector("#bookingForm button[type='submit']").disabled = false;
        }
        
        schedules.forEach(slot => {
            // 🌟 1. Updated Doctor Specialty list for Patient View
            let role = "General Physician";
            if (slot.doctorName.includes("Ramesh Kumar")) {
                role = "Oncologist (Cancer Specialist)";
            } else if (slot.doctorName.includes("Sneha Sharma")) {
                role = "General Physician (Fever / Cold)";
            } else if (slot.doctorName.includes("Ramcar Kumar")) {
                role = "Pediatrician (Kids Fever / Cold)";
            } else if (slot.doctorName.includes("Amit Mishra")) {
                role = "Cardiologist (Heart Specialist)";
            } else if (slot.doctorName.includes("Priya Patel")) {
                role = "Dermatologist (Skin Specialist)";
            } else if (slot.doctorName.includes("Vikram Malhotra")) {
                role = "Neurologist (Brain Specialist)";
            } else if (slot.doctorName.includes("Ananya Reddy")) {
                role = "General Physician (Fever / Cold)";
            } else if (slot.doctorName.includes("Rajesh Joshi")) {
                role = "Oncologist (Cancer Specialist)";
            }

            const row = document.createElement("tr");
            row.innerHTML = `
                <td><strong>${slot.id}</strong></td>
                <td>
                    <div style="font-weight: 600;">${slot.doctorName}</div>
                    <small style="color: #64748b; font-size: 0.8rem; display: block; margin-top: 2px;">🩺 ${role}</small>
                </td>
                <td>${slot.availableTime}</td>
                <td><span class="status-badge">Available</span></td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        tbody.innerHTML = `<tr><td colspan="4" class="text-center" style="color: red;">Error: ${error.message}</td></tr>`;
    }
}

async function bookAppointment(event) {
    event.preventDefault();
    
    // Get form elements
   // Get form elements safely
        const scheduleIdField = document.getElementById("scheduleId");
        const emailField = document.getElementById("patientEmail");
        const illnessField = document.getElementById("illnessSelect");
        const msgBox = document.getElementById("messageBox");
        
        // Dynamic fallback selector to get the booking submit button safely
        const submitBtn = document.getElementById("bookingSubmitBtn") || 
                          document.querySelector("button[type='submit']") || 
                          document.querySelector(".btn-submit");
    submitBtn.innerHTML = `⏳ Processing Booking...`;
    scheduleIdField.disabled = true;
    emailField.disabled = true;
    illnessField.disabled = true;
    msgBox.classList.add("hidden");
    
    const scheduleId = scheduleIdField.value;
    const email = emailField.value;
    const illness = illnessField.value; 
    
    try {
        const url = `${BASE_URL}/book?scheduleId=${scheduleId}&email=${encodeURIComponent(email)}&illness=${encodeURIComponent(illness)}`;
        const response = await fetch(url, {
            method: "POST",
            headers: { "Authorization": AUTH_HEADER }
        });

        const data = await response.json(); 
        msgBox.classList.remove("hidden", "success", "error");

        if (response.ok) {
            msgBox.classList.add("success");
            msgBox.innerText = data.message || "Appointment booked successfully!";
            document.getElementById("bookingForm").reset();
            await loadSchedules(); // Reload data grid
        } else {
            msgBox.classList.add("error");
            msgBox.innerText = data.message || "Booking failed.";
        }
    } catch (error) {
        msgBox.classList.remove("hidden", "success", "error");
        msgBox.classList.add("error");
        msgBox.innerText = "Connection lost: Backend server might be offline.";
    } finally {
        // 🌟 UX STATE 2: Unlock inputs and restore the submit button action baseline
        submitBtn.disabled = false;
        submitBtn.innerHTML = `Confirm Secure Booking`;
        scheduleIdField.disabled = false;
        emailField.disabled = false;
        illnessField.disabled = false;
    }
}

async function loadBookedSchedules() {
    const tbody = document.getElementById("bookedSchedulesBody");
    try {
        const response = await fetch(`${BASE_URL}/booked`, {
            method: "GET",
            headers: { "Authorization": AUTH_HEADER }
        });
        if (!response.ok) throw new Error("Could not fetch booked records.");
        
        const bookedSlots = await response.json();
        tbody.innerHTML = ""; 

        // 🌟 Update our total bookings metrics counter badge
        const badge = document.getElementById("totalBookingsBadge");
        if (badge) {
            badge.innerText = `Total Bookings: ${bookedSlots.length}`;
            // Show a gray badge if there are 0 bookings, or a vibrant red alert badge if appointments exist
            badge.style.backgroundColor = bookedSlots.length > 0 ? "#ef4444" : "#64748b";
        }
        
        if (bookedSlots.length === 0) {
            tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted py-3">No appointments booked yet.</td></tr>`;
            return;
        }
        
        bookedSlots.forEach(slot => {
            // 🌟 2. Updated Doctor Specialty list for Admin/Doctor View
            let role = "General Physician";
            if (slot.doctorName.includes("Ramesh Kumar")) {
                role = "Oncologist (Cancer Specialist)";
            } else if (slot.doctorName.includes("Sneha Sharma")) {
                role = "General Physician (Fever / Cold)";
            } else if (slot.doctorName.includes("Ramcar Kumar")) {
                role = "Pediatrician (Kids Fever / Cold)";
            } else if (slot.doctorName.includes("Amit Mishra")) {
                role = "Cardiologist (Heart Specialist)";
            } else if (slot.doctorName.includes("Priya Patel")) {
                role = "Dermatologist (Skin Specialist)";
            } else if (slot.doctorName.includes("Vikram Malhotra")) {
                role = "Neurologist (Brain Specialist)";
            } else if (slot.doctorName.includes("Ananya Reddy")) {
                role = "General Physician (Fever / Cold)";
            } else if (slot.doctorName.includes("Rajesh Joshi")) {
                role = "Oncologist (Cancer Specialist)";
            }

            const row = document.createElement("tr");
            row.innerHTML = `
                <td><strong>${slot.id}</strong></td>
                <td>
                    <div style="font-weight: 600;">${slot.doctorName}</div>
                    <small style="color: #64748b; font-size: 0.8rem; display: block; margin-top: 2px;">🩺 ${role}</small>
                </td>
                <td>${slot.availableTime}</td>
                <td class="text-primary fw-semibold">${slot.patientEmail || 'Reserved'}</td>
                <td>
                    <span style="background-color: #f1f5f9; color: #1e293b; border: 1px solid #cbd5e1; padding: 4px 8px; border-radius: 4px; font-size: 0.85rem; font-weight: 500;">
                        ${slot.patientIllness || 'Not Specified'}
                    </span>
                </td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center text-danger py-3">Error: ${error.message}</td></tr>`;
    }
}

async function resetAllSystemSlots() {
    if (!confirm("Are you sure you want to clear all appointment records and reset the system?")) return;
    
    try {
        const response = await fetch(`${BASE_URL}/reset`, {
            method: "PUT",
            headers: { "Authorization": AUTH_HEADER }
        });
        
        if (!response.ok) throw new Error("Failed to clear database slots from backend server.");
        
        alert("System Reset Complete!");
        if (document.getElementById("schedulesBody")) loadSchedules();      
        if (document.getElementById("bookedSchedulesBody")) loadBookedSchedules(); 
    } catch (error) {
        alert("Reset Error: " + error.message);
    }
}


// 🌟 Real-time client-side filter function for Doctor Schedules
function filterDoctorSchedules() {
    const searchFilter = document.getElementById("doctorSearchInput").value.toLowerCase();
    // Target the rows inside your table body
    const tableBody = document.querySelector("table tbody");
    const rows = tableBody.getElementsByTagName("tr");

    // Loop through all table rows, hide those that don't match the search query
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        
        // Skip row processing if it's the "No schedules available" empty placeholder row
        if (row.cells.length < 3) continue;

        const doctorNameText = row.cells[1].innerText.toLowerCase(); // Doctor Name cell data
        const slotIdText = row.cells[0].innerText.toLowerCase();    // Slot ID cell data
        const timeWindowText = row.cells[2].innerText.toLowerCase(); // Time Window cell data

        // Check if query strings exist inside row text variables
        if (doctorNameText.includes(searchFilter) || 
            slotIdText.includes(searchFilter) || 
            timeWindowText.includes(searchFilter)) {
            row.style.display = ""; // Show matching row
        } else {
            row.style.display = "none"; // Hide non-matching row
        }
    }
}


// 🌟 Real-time client-side filter function for the Admin Doctor Dashboard
function filterAdminBookings() {
    const searchFilter = document.getElementById("adminSearchInput").value.toLowerCase();
    const tableBody = document.getElementById("bookedSchedulesBody");
    if (!tableBody) return;
    
    const rows = tableBody.getElementsByTagName("tr");

    // Loop through all table rows, hide those that don't match the search query
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        
        // Skip row processing if it's the "No active appointments" empty placeholder row
        if (row.cells.length < 5) continue;

        const slotIdText = row.cells[0].innerText.toLowerCase();     // Slot ID
        const doctorNameText = row.cells[1].innerText.toLowerCase(); // Doctor Details
        const timeWindowText = row.cells[2].innerText.toLowerCase(); // Time Window
        const patientEmailText = row.cells[3].innerText.toLowerCase(); // Patient Email
        const patientIllnessText = row.cells[4].innerText.toLowerCase(); // Patient Illness

        // Check if query strings exist inside any of our row data metrics variables
        if (slotIdText.includes(searchFilter) ||
            doctorNameText.includes(searchFilter) || 
            timeWindowText.includes(searchFilter) ||
            patientEmailText.includes(searchFilter) ||
            patientIllnessText.includes(searchFilter)) {
            row.style.display = ""; // Show matching row
        } else {
            row.style.display = "none"; // Hide non-matching row
        }
    }
}