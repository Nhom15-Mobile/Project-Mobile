// src/config/env.js
require('dotenv').config();

const config = {
  env:  process.env.NODE_ENV || 'development',
  port: Number(process.env.PORT || 4000),
  dbUrl: process.env.DATABASE_URL,

  jwt: {
    secret:  process.env.JWT_SECRET,
    expires: process.env.JWT_EXPIRES || '7d'
  },

  // MoMo (để sẵn, chưa dùng cũng không sao)
  momo: {
    partnerCode: process.env.MOMO_PARTNER_CODE || '',
    accessKey:   process.env.MOMO_ACCESS_KEY   || '',
    secretKey:   process.env.MOMO_SECRET_KEY   || '',
    endpoint:    process.env.MOMO_ENDPOINT     || 'https://test-payment.momo.vn/v2/gateway/api',
    returnUrl:   process.env.MOMO_RETURN_URL   || '',
    notifyUrl:   process.env.MOMO_NOTIFY_URL   || ''
  },

  // ===== 10 chuyên khoa cố định + phí hiển thị =====
  specialties: [
    { name: 'BỆNH LÝ CỘT SỐNG',            fee: 150000 },
    { name: 'DA LIỄU',                    fee: 150000 },
    { name: 'HUYẾT HỌC',                  fee: 150000 },
    { name: 'MẮT',                        fee: 150000 },
    { name: 'NGOẠI THẦN KINH',            fee: 150000 },
    { name: 'TAI MŨI HỌNG',               fee: 150000 },
    { name: 'THẦN KINH',                  fee: 150000 },
    { name: 'TIM MẠCH',                   fee: 150000 },
    { name: 'TƯ VẤN TÂM LÝ',              fee: 150000 },
    { name: 'KHÁM VÀ TƯ VẤN DINH DƯỠNG',  fee: 150000 }
  ],

  // Fallback chung (nếu cần dùng nơi khác)
  fees: {
    defaultSpecialtyFee: 150000
  }
};

module.exports = config;