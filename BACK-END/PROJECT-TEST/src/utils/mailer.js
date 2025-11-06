// src/utils/mailer.js
const nodemailer = require('nodemailer');
const config = require('../config/env');

let transporter;

/**
 * Khởi tạo transporter 1 lần, dùng lại cho các lần gửi sau
 */
function getTransporter() {
  if (transporter) return transporter;

  transporter = nodemailer.createTransport({
    host: config.smtp.host,
    port: config.smtp.port,
    secure: !!config.smtp.secure, // TLS qua 587 -> false; SMTPS 465 -> true
    auth: {
      user: config.smtp.user,
      pass: config.smtp.pass
    }
  });

  return transporter;
}

/**
 * Gửi email
 * @param {Object} p
 * @param {string|string[]} p.to
 * @param {string} p.subject
 * @param {string} [p.text]
 * @param {string} [p.html]
 */
async function sendMail({ to, subject, text, html }) {
  const tp = getTransporter();

  // from hiển thị đẹp: 'UIT Healthcare <uithealthcare111@gmail.com>'
  const fromName = config.mail.fromName || 'UIT Healthcare';
  const fromEmail = config.mail.fromEmail || config.smtp.user;
  const from = `${fromName} <${fromEmail}>`;

  return tp.sendMail({ from, to, subject, text, html });
}

module.exports = { sendMail };
