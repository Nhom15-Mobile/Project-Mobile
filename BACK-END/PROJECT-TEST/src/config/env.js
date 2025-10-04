// src/config/env.js
require('dotenv').config();


const config = {
env: process.env.NODE_ENV || 'development',
port: process.env.PORT || 4000,
dbUrl: process.env.DATABASE_URL,
jwt: {
secret: process.env.JWT_SECRET,
expires: process.env.JWT_EXPIRES || '7d'
},
stripe: {
secret: process.env.STRIPE_SECRET,
webhookSecret: process.env.STRIPE_WEBHOOK_SECRET
},
vnpay: {
tmnCode: process.env.VNP_TMN_CODE,
hashSecret: process.env.VNP_HASH_SECRET,
url: process.env.VNP_URL,
returnUrl: process.env.VNP_RETURN_URL
},
momo: {
partnerCode: process.env.MOMO_PARTNER_CODE,
accessKey: process.env.MOMO_ACCESS_KEY,
secretKey: process.env.MOMO_SECRET_KEY,
endpoint: process.env.MOMO_ENDPOINT
}
};


module.exports = config;