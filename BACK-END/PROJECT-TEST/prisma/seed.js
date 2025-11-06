// prisma/seed.js
const { PrismaClient } = require('@prisma/client');
const bcrypt = require('bcryptjs');
const { addMinutes } = require('date-fns');


const prisma = new PrismaClient();


async function main() {
// Admin
const admin = await prisma.user.upsert({
where: { email: 'admin@demo.local' },
update: {},
create: {
email: 'admin@demo.local',
password: await bcrypt.hash('Admin@123', 10),
fullName: 'System Admin',
role: 'ADMIN'
}
});


// Doctor
const doctorUser = await prisma.user.upsert({
where: { email: 'doctor@demo.local' },
update: {},
create: {
email: 'doctor@demo.local',
password: await bcrypt.hash('Doctor@123', 10),
fullName: 'Dr. John Doe',
role: 'DOCTOR'
}
});


const doctorProfile = await prisma.doctorProfile.upsert({
where: { userId: doctorUser.id },
update: {},
create: {
userId: doctorUser.id,
specialty: 'Cardiology',
bio: 'Heart specialist with 10+ years experience.',
yearsExperience: 10,
clinicName: 'Demo Heart Clinic'
}
});


// Create some free slots today
const now = new Date();
const slots = [];
let cursor = addMinutes(now, 30);
for (let i = 0; i < 6; i++) {
const start = cursor;
const end = addMinutes(start, 30);
slots.push({ doctorId: doctorProfile.id, start, end });
cursor = end;
}
await prisma.doctorSlot.createMany({ data: slots });

}

main()
  .catch(e => {
	console.error(e);
	process.exit(1);
  })
  .finally(async () => {
	await prisma.$disconnect();
  });