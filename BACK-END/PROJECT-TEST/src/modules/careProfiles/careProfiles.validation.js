// src/modules/careProfiles/careProfiles.validation.js
const Joi = require('joi');

// Base fields (UI yêu cầu)
const base = {
  fullName: Joi.string().min(2).required(),
  relation: Joi.string().min(1).required(),

  // Các field UI hay có — service hiện tại có thể chưa lưu hết, nhưng ta cho đi qua để sau muốn lưu thì không phải sửa validate
  phone: Joi.string().pattern(/^[0-9+\s\-().]{6,20}$/).required(),
  country: Joi.string().min(2).required(),

  gender: Joi.string().valid('male', 'female', 'other').required(),
  dob: Joi.string().pattern(/^\d{4}-\d{2}-\d{2}$/).required(),

  // optional metadata
  email: Joi.string().email().allow('', null),
  insuranceNo: Joi.string().allow('', null),
  note: Joi.string().allow('', null),
};

// Nhóm địa chỉ theo "tên"
const nameAddressFields = {
  province: Joi.string(),
  district: Joi.string(),
  ward: Joi.string(),
  address: Joi.string(), // số nhà, đường...
};

// Nhóm địa chỉ theo "mã"
const codeAddressFields = {
  provinceCode: Joi.string(),
  districtCode: Joi.string(),
  wardCode: Joi.string(),
  addressDetail: Joi.string(), // số nhà, đường...
};

/**
 * Tạo mới:
 * - Bắt buộc base fields.
 * - Địa chỉ: CHỌN 1 TRONG 2 CÁCH
 *    (A) province + district + ward + address
 *    (B) provinceCode + districtCode + wardCode + addressDetail
 */
const create = Joi.object({
  ...base,
  ...nameAddressFields,
  ...codeAddressFields,
}).custom((value, helpers) => {
  const hasCodes = !!(value.provinceCode || value.districtCode || value.wardCode || value.addressDetail);
  if (hasCodes) {
    if (!value.provinceCode || !value.districtCode || !value.wardCode || !value.addressDetail) {
      return helpers.error('any.custom', {
        message: 'provinceCode, districtCode, wardCode, addressDetail are required when using codes',
      });
    }
  } else {
    if (!value.province || !value.district || !value.ward || !value.address) {
      return helpers.error('any.custom', {
        message: 'province, district, ward, address are required',
      });
    }
  }
  return value;
});

/**
 * Cập nhật:
 * - Cho phép partial update.
 * - Nếu cung cấp 1 phần nhóm địa chỉ thì yêu cầu đủ cả nhóm được chọn (để dữ liệu không nửa tên nửa mã).
 */
const update = Joi.object({
  // tất cả optional cho update
  fullName: Joi.string().min(2),
  relation: Joi.string().min(1),

  phone: Joi.string().pattern(/^[0-9+\s\-().]{6,20}$/),
  country: Joi.string().min(2),

  gender: Joi.string().valid('male', 'female', 'other'),
  dob: Joi.string().pattern(/^\d{4}-\d{2}-\d{2}$/),

  email: Joi.string().email().allow('', null),
  insuranceNo: Joi.string().allow('', null),
  note: Joi.string().allow('', null),

  ...nameAddressFields,
  ...codeAddressFields,
}).custom((value, helpers) => {
  const hasAnyName =
    value.province !== undefined ||
    value.district !== undefined ||
    value.ward !== undefined ||
    value.address !== undefined;

  const hasAnyCode =
    value.provinceCode !== undefined ||
    value.districtCode !== undefined ||
    value.wardCode !== undefined ||
    value.addressDetail !== undefined;

  // Nếu user update theo nhóm "mã"
  if (hasAnyCode) {
    if (!value.provinceCode || !value.districtCode || !value.wardCode || !value.addressDetail) {
      return helpers.error('any.custom', {
        message: 'When updating by codes, provinceCode, districtCode, wardCode, addressDetail must all be provided',
      });
    }
    // Nếu đã chọn nhóm mã, bạn có thể enforce không cho trộn nhóm tên (optional):
    // if (hasAnyName) {
    //   return helpers.error('any.custom', { message: 'Do not mix name-address with code-address in one update' });
    // }
  }

  // Nếu user update theo nhóm "tên"
  if (hasAnyName && !hasAnyCode) {
    if (!value.province || !value.district || !value.ward || !value.address) {
      return helpers.error('any.custom', {
        message: 'When updating by names, province, district, ward, address must all be provided',
      });
    }
  }

  return value;
});

module.exports = { create, update };
