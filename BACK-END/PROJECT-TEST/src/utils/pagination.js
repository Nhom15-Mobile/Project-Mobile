// src/utils/pagination.js
function toPagination(query) {
  const page = Math.max(parseInt(query.page || '1', 10), 1);
  const pageSize = Math.min(Math.max(parseInt(query.pageSize || '10', 10), 1), 100);
  const skip = (page - 1) * pageSize;
  return { page, pageSize, skip, take: pageSize };
}

module.exports = { toPagination };