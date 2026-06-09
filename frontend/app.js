const BASE_URL = "http://localhost:8088/api/patient";

// Basic Authorization header using your active 8085 runtime security key
// 1. Define the raw credentials string
const credentials = "admin:HospitalAdmin2026!";
// 2. Encode to Base64 and add the mandatory single space after "Basic "
const AUTH_HEADER = "Basic " + btoa(credentials);
document.getElementById("btnLoadSchedules").addEventListener("click", loadSchedules);
document.getElementById("bookingForm").addEventListener("submit", bookAppointment);
document.getElementById("btnResetSchedules").addEventListener("click", resetAllSystemSlots);

// This runs automatically when index.html first opens in the browser
loadSchedules();
loadBookedSchedules(); 


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
            
            // Lock down the appointment inputs if nothing is open to book
            document.getElementById("scheduleId").disabled = true;
            document.getElementById("patientEmail").disabled = true;
            document.querySelector("#bookingForm button[type='submit']").disabled = true;
            return;
        }
        
        // Re-enable input capabilities as long as open slots are present
        document.getElementById("scheduleId").disabled = false;
        document.getElementById("patientEmail").disabled = false;
        document.querySelector("#bookingForm button[type='submit']").disabled = false;
        schedules.forEach(slot => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td><strong>${slot.id}</strong></td>
                <td>${slot.doctorName}</td>
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
    const scheduleId = document.getElementById("scheduleId").value;
    const email = document.getElementById("patientEmail").value;
    const msgBox = document.getElementById("messageBox");
    msgBox.classList.add("hidden");
    
    try {
        // Correct query mapping pointing to your Java Controller parameters: scheduleId & email
        const response = await fetch(`${BASE_URL}/book?scheduleId=${scheduleId}&email=${encodeURIComponent(email)}`, {
            method: "POST",
            headers: { "Authorization": AUTH_HEADER }
        });

        // Parse our custom structured Java ApiResponse object
        const data = await response.json(); 
        msgBox.classList.remove("hidden", "success", "error");

        if (response.ok) {
            msgBox.classList.add("success");
            msgBox.innerText = data.message || "Appointment booked successfully!";
            document.getElementById("bookingForm").reset();
            loadSchedules();
            loadBookedSchedules();
        } else {
            msgBox.classList.add("error");
            // Pulls the explicit exception message string from GlobalExceptionHandler
            msgBox.innerText = data.message || "Booking failed.";
        }
    } catch (error) {
        msgBox.classList.remove("hidden", "success", "error");
        msgBox.classList.add("error");
        msgBox.innerText = "Connection lost: Backend server might be offline.";
    }
}



async function loadBookedSchedules() {
    const tbody = document.getElementById("bookedSchedulesBody");
    try {
        const response = await fetch(`${BASE_URL}/booked`, {
            method: "GET"
        });
        if (!response.ok) throw new Error("Could not fetch booked records.");
        
        const bookedSlots = await response.json();
        tbody.innerHTML = ""; 
        
        if (bookedSlots.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted py-3">No appointments booked yet.</td></tr>`;
            return;
        }
        
        bookedSlots.forEach(slot => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td><strong>${slot.id}</strong></td>
                <td>${slot.doctorName}</td>
                <td>${slot.availableTime}</td>
                <td class="text-primary fw-semibold">${slot.patientEmail || 'Reserved'}</td>
            `;
            tbody.appendChild(row);
        });
    } catch (error) {
        tbody.innerHTML = `<tr><td colspan="4" class="text-center text-danger py-3">Error: ${error.message}</td></tr>`;
    }



    
}

async function resetAllSystemSlots() {
    if (!confirm("Are you sure you want to clear all appointment records and reset the system?")) return;
    
    try {
        const response = await fetch(`${BASE_URL}/reset`, {
            method: "PUT",
            headers: { "Authorization": AUTH_HEADER } // Passes your active security token
        });
        
        if (!response.ok) throw new Error("Failed to clear database slots from backend server.");
        
        alert("System Reset Complete!");
        
        // Refresh both tables instantly
        loadSchedules();      
        loadBookedSchedules(); 
    } catch (error) {
        alert("Reset Error: " + error.message);
    }
}