// src/utils/invoicePdf.js
const PDFDocument = require('pdfkit');
const path = require('path');

function formatVND(amount) {
  if (amount == null) return '0';
  return Number(amount).toLocaleString('vi-VN');
}

function formatDate(d) {
  if (!d) return '';
  return new Date(d).toLocaleDateString('vi-VN');
}

function formatTime(d) {
  if (!d) return '';
  return new Date(d).toLocaleTimeString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  });
}

/**
 * generateInvoicePdf({ appointment, payment }) -> Buffer
 */
function generateInvoicePdf({ appointment = {}, payment = {} }) {
  return new Promise((resolve, reject) => {
    const doc = new PDFDocument({ size: 'A4', margin: 50 });

    const chunks = [];
    doc.on('data', (c) => chunks.push(c));
    doc.on('end', () => resolve(Buffer.concat(chunks)));
    doc.on('error', reject);

    // ====== FONT ======
    try {
      const fontDir = path.join(__dirname, '..', 'assets', 'fonts');
      doc.registerFont('Body', path.join(fontDir, 'NotoSans-Regular.ttf'));
      doc.registerFont('Body-Bold', path.join(fontDir, 'NotoSans-Bold.ttf'));
      doc.font('Body');
    } catch (e) {
      console.error('Invoice PDF font error, fallback to default:', e);
    }

    const clinicName = 'UIT HealthCare';
    const headerLines = [
      'Phòng khám đa khoa UIT HealthCare',
      'Địa chỉ: Khu phố 6, phường Linh Trung, TP Thủ Đức, TP.HCM',
      'Email: uithealthcare111@gmail.com',
      'Điện thoại: 0123 456 789',
    ];

    const invoiceNumber = payment.id || appointment.id || 'N/A';
    const invoiceDate =
      formatDate(
        payment.paidAt ||
          payment.updatedAt ||
          appointment.updatedAt ||
          new Date(),
      ) || 'N/A';

    const patientName =
      appointment.careProfile?.fullName ||
      appointment.patient?.fullName ||
      'N/A';
    const patientEmail = appointment.patient?.email || 'N/A';

    const doctorName = appointment.doctor?.fullName || 'N/A';
    const doctorSpecialty =
      appointment.doctor?.specialty ||
      appointment.doctorProfile?.specialty ||
      'N/A';

    const slotStart = appointment.slot?.start || appointment.scheduledAt;
    const slotEnd = appointment.slot?.end || null;

    const description =
      appointment.service && doctorName
        ? `${appointment.service} - Bác sĩ: ${doctorName} (${doctorSpecialty})`
        : appointment.service || 'Khám tại UIT HealthCare';

    const qty = 1;
    const price = payment.amount || 0;
    const subTotal = price * qty;
    const vatPercent = 0;
    const vatAmount = 0;
    const total = subTotal + vatAmount;

    // ====== HEADER ======
    const leftX = 50;
    const topY = 50;
    const rightX = 320;

    // Logo / clinic name
    doc.font('Body-Bold').fontSize(22).text(clinicName, leftX, topY);

    doc.moveDown(0.3);
    doc.font('Body').fontSize(10);
    headerLines.forEach((line) => doc.text(line, leftX));

    // Title: INVOICE
    doc.font('Body-Bold')
      .fontSize(24)
      .text('INVOICE', rightX, topY, { align: 'right' });

    // Right info block
    let rightY = topY + 32;
    doc.font('Body').fontSize(10);
    // doc.text(`Invoice Number: ${invoiceNumber}`, rightX, rightY, {
    //   align: 'right',
    // });
    // rightY += 14;
    doc.text(`Invoice Date: ${invoiceDate}`, rightX, rightY, {
      align: 'right',
    });
    rightY += 14;
    doc.text('Bill To (Email):', rightX, rightY, { align: 'right' });
    rightY += 12;
    doc.text(patientEmail, rightX, rightY, { align: 'right' });

    // Line
    const lineY = rightY + 25;
    doc.moveTo(leftX, lineY).lineTo(545, lineY).stroke();

    // ====== BILLING BLOCK ======
    let y = lineY + 20;

    // Left: send payment to
    doc.font('Body-Bold')
      .fontSize(12)
      .text('Please send payment to:', leftX, y);
    y += 16;

    doc.font('Body').fontSize(10);
    doc.text('UIT HealthCare', leftX, y);
    y += 12;
    doc.text('UIT Campus', leftX, y);
    y += 12;
    doc.text('Linh Trung, Thủ Đức, TP.HCM', leftX, y);
    y += 12;
    doc.text('Việt Nam', leftX, y);

    // Right: Bill to
    let billX = 320;
    let billY = lineY + 20;

    doc.font('Body-Bold')
      .fontSize(12)
      .text('Bill to:', billX, billY);
    billY += 16;

    doc.font('Body').fontSize(10);
    doc.text(patientName, billX, billY);
    billY += 12;
    doc.text(patientEmail, billX, billY);

    // ====== TABLE HEADER ======
    y = Math.max(y, billY) + 30;

    doc.font('Body-Bold').fontSize(11);

    const colStartX = leftX;
    const colEndX = 120;
    const colDescX = 190;
    const colQtyX = 380;
    const colPriceX = 430;
    const colAmountX = 500;

    doc.text('Start', colStartX, y);
    doc.text('End', colEndX, y);
    doc.text('Description', colDescX, y);
    doc.text('Qty', colQtyX, y, { width: 40, align: 'right' });
    doc.text('Price', colPriceX, y, { width: 60, align: 'right' });
    doc.text('Amount', colAmountX, y, { width: 60, align: 'right' });

    y += 18;
    doc.moveTo(leftX, y).lineTo(545, y).stroke();
    y += 8;

    // ====== TABLE ROW (1 dòng) ======
    doc.font('Body').fontSize(10);

    doc.text(formatTime(slotStart), colStartX, y);
    doc.text(formatTime(slotEnd), colEndX, y);
    doc.text(description, colDescX, y, { width: 180 });

    doc.text(String(qty), colQtyX, y, { width: 40, align: 'right' });
    doc.text(formatVND(price), colPriceX, y, { width: 60, align: 'right' });
    doc.text(formatVND(total), colAmountX, y, { width: 60, align: 'right' });

    // Date row (dưới time)
    const dateY = y + 18;
    const dateStr = formatDate(slotStart);
    if (dateStr) {
      doc.text(dateStr, colStartX, dateY);
      doc.text(dateStr, colEndX, dateY);
    }

    // ====== SUMMARY (Sub total / VAT / Total) ======
    let summaryY = dateY + 40;

    // 2 cột: label + value, không dùng align:right cho label để tránh chồng
    const sumLabelX = 330; // cột chữ
    const sumValueX = 480; // cột số (căn phải trong ô 80px)

    doc.font('Body').fontSize(10);

    // Sub Total
    doc.text('Sub Total:', sumLabelX, summaryY);
    doc.text(formatVND(subTotal), sumValueX, summaryY, {
      width: 80,
      align: 'right',
    });

    summaryY += 16;

    // VAT
    doc.text(`VAT (${vatPercent}%):`, sumLabelX, summaryY);
    doc.text(`${formatVND(vatAmount)} VND`, sumValueX, summaryY, {
      width: 80,
      align: 'right',
    });

    summaryY += 20;

    // Total
    doc.font('Body-Bold').fontSize(12);
    doc.text('Total:', sumLabelX, summaryY);
    doc.text(`${formatVND(total)} VND`, sumValueX, summaryY, {
      width: 80,
      align: 'right',
    });

    // ====== PAYMENT INFO ======
    summaryY += 40;
    doc.font('Body-Bold')
      .fontSize(12)
      .text('Payment Information', leftX, summaryY);

    summaryY += 18;
    doc.font('Body').fontSize(10);
    doc.text(`Phương thức: ${payment.provider || 'MoMo'}`, leftX, summaryY);
    summaryY += 14;
    doc.text(`Trạng thái: ${payment.status || 'PAID'}`, leftX, summaryY);
    summaryY += 14;
    if (payment.providerRef) {
      doc.text(`Mã giao dịch: ${payment.providerRef}`, leftX, summaryY);
      summaryY += 14;
    }

    summaryY += 30;
    doc.font('Body').fontSize(9).fillColor('#555555');
    doc.text(
      'Lưu ý: Hóa đơn này được tạo tự động bởi hệ thống UIT HealthCare sau khi thanh toán thành công.',
      leftX,
      summaryY,
      { width: 480 },
    );

    doc.end();
  });
}

module.exports = { generateInvoicePdf };
