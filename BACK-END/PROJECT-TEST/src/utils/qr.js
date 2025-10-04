// src/utils/qr.js
const QRCode = require('qrcode');


async function generateAppointmentQR(payload) {
// payload: { appointmentId, patientName, doctorName, scheduledAt }
const text = JSON.stringify(payload);
return QRCode.toDataURL(text); // returns data:image/png;base64,...
}


module.exports = { generateAppointmentQR };