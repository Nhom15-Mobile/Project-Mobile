// src/utils/invoiceEmail.js
const nodemailer = require('nodemailer');

const {
  SMTP_HOST,
  SMTP_PORT,
  SMTP_SECURE,
  SMTP_USER,
  SMTP_PASS,
  SMTP_FROM,
} = process.env;

const transporter = nodemailer.createTransport({
  host: SMTP_HOST,
  port: Number(SMTP_PORT || 587),
  secure: SMTP_SECURE === 'true',
  auth: {
    user: SMTP_USER,
    pass: SMTP_PASS,
  },
});

/**
 * Send invoice email
 * Required: to, pdfBuffer
 * Optional: appointment, payment, filename
 */
async function sendInvoiceEmail({
  to,
  pdfBuffer,
  filename,
  appointment = {},
  payment = {},
}) {
  if (!to) {
    console.warn('sendInvoiceEmail: missing recipient email');
    return;
  }

  const apptId = appointment.id || 'N/A';

  const patientName =
    appointment.careProfile?.fullName ||
    appointment.patient?.fullName ||
    'bạn';

  const apptService =
    appointment.service ||
    `lịch hẹn khám tại UIT HealthCare`;

  const apptTimeRaw =
    appointment.scheduledAt || appointment.slot?.start || null;

  const apptTime = apptTimeRaw
    ? new Date(apptTimeRaw).toLocaleString('vi-VN')
    : 'N/A';

  const amountRaw =
    payment.amount ??
    appointment.payment?.amount ??
    null;

  const paymentAmount = amountRaw != null
    ? Number(amountRaw).toLocaleString('vi-VN')
    : 'N/A';

  const paymentCurrency =
    payment.currency || appointment.payment?.currency || 'VND';

  const mailOptions = {
    from: SMTP_FROM || `"UIT HealthCare" <${SMTP_USER}>`,
    to,
    subject: `Hóa đơn thanh toán lịch hẹn #${apptId}`,
    text: `Chào ${patientName},

Đây là hóa đơn thanh toán cho ${apptService} tại UIT HealthCare.

- Mã lịch hẹn: ${apptId}
- Thời gian khám: ${apptTime}
- Số tiền: ${paymentAmount} ${paymentCurrency}

File hóa đơn chi tiết được đính kèm trong email này.

Trân trọng,
UIT HealthCare
`,
    attachments: [
      {
        filename: filename || `invoice-${apptId}.pdf`,
        content: pdfBuffer,
        contentType: 'application/pdf',
      },
    ],
  };

  await transporter.sendMail(mailOptions);
  console.log(`Invoice email sent to ${to} (appointment ${apptId})`);
}

module.exports = { sendInvoiceEmail };
