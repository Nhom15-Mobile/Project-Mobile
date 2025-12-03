// Script to create 100 test users
const { PrismaClient } = require('@prisma/client');
const bcrypt = require('bcryptjs');

const prisma = new PrismaClient();

const roles = ['PATIENT', 'DOCTOR', 'ADMIN'];
const specialties = [
  'BỆNH LÝ CỘT SỐNG',
  'Cardiology',
  'Neurology',
  'Pediatrics',
  'Orthopedics',
  'Dermatology',
  'Ophthalmology',
  'ENT',
  'General Medicine',
  'Surgery',
];

async function createTestUsers() {
  console.log('Starting to create 100 test users...\n');

  const hashedPassword = await bcrypt.hash('password123', 10);

  for (let i = 1; i <= 100; i++) {
    try {
      // Distribute roles: 70% patients, 25% doctors, 5% admins
      let role;
      if (i <= 70) role = 'PATIENT';
      else if (i <= 95) role = 'DOCTOR';
      else role = 'ADMIN';

      const email = `testuser${i}@example.com`;
      const fullName = `Test User ${i}`;
      const phone = `09${String(i).padStart(8, '0')}`;

      // Create user
      const user = await prisma.user.create({
        data: {
          email,
          password: hashedPassword,
          fullName,
          phone,
          role,
        },
      });

      console.log(`✓ Created ${role}: ${fullName} (${email})`);

      // If doctor, create doctor profile
      if (role === 'DOCTOR') {
        const specialty = specialties[Math.floor(Math.random() * specialties.length)];
        const yearsExperience = Math.floor(Math.random() * 20) + 1;

        await prisma.doctorProfile.create({
          data: {
            userId: user.id,
            specialty,
            yearsExperience,
            clinicName: `Clinic ${i}`,
            bio: `Experienced ${specialty} specialist with ${yearsExperience} years of practice.`,
          },
        });

        console.log(`  → Added doctor profile: ${specialty}, ${yearsExperience} years exp`);
      }

      // If patient, create 1-2 care profiles
      if (role === 'PATIENT' && i % 3 === 0) {
        const numProfiles = Math.random() > 0.5 ? 2 : 1;

        for (let j = 1; j <= numProfiles; j++) {
          const relations = ['Self', 'Parent', 'Child', 'Spouse', 'Sibling'];
          const relation = relations[Math.floor(Math.random() * relations.length)];
          const genders = ['Male', 'Female', 'Other'];
          const gender = genders[Math.floor(Math.random() * genders.length)];

          await prisma.careProfile.create({
            data: {
              ownerId: user.id,
              fullName: `${fullName} - ${relation} ${j}`,
              relation,
              dob: new Date(1950 + Math.floor(Math.random() * 60), Math.floor(Math.random() * 12), 1),
              gender,
              phone: `09${String(i * 10 + j).padStart(8, '0')}`,
              email: `profile${i}_${j}@example.com`,
              nationalId: `${String(i).padStart(12, '0')}`,
              occupation: 'Office Worker',
              province: 'Ho Chi Minh',
              district: `District ${Math.floor(Math.random() * 12) + 1}`,
              address: `${i} Test Street`,
            },
          });

          console.log(`  → Added care profile: ${relation}`);
        }
      }
    } catch (error) {
      if (error.code === 'P2002') {
        console.log(`⚠ User ${i} already exists, skipping...`);
      } else {
        console.error(`✗ Error creating user ${i}:`, error.message);
      }
    }
  }

  console.log('\n✅ Finished creating test users!');
  console.log('\nSummary:');
  const counts = await Promise.all([
    prisma.user.count(),
    prisma.user.count({ where: { role: 'PATIENT' } }),
    prisma.user.count({ where: { role: 'DOCTOR' } }),
    prisma.user.count({ where: { role: 'ADMIN' } }),
    prisma.doctorProfile.count(),
    prisma.careProfile.count(),
  ]);

  console.log(`Total Users: ${counts[0]}`);
  console.log(`- Patients: ${counts[1]}`);
  console.log(`- Doctors: ${counts[2]}`);
  console.log(`- Admins: ${counts[3]}`);
  console.log(`Doctor Profiles: ${counts[4]}`);
  console.log(`Care Profiles: ${counts[5]}`);
}

createTestUsers()
  .catch((e) => {
    console.error('Fatal error:', e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
