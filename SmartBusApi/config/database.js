const { createPool } = require("mysql");

const pool = createPool({
  host: 'localhost',
  port: 3306,
  user: 'root',
  password: '',
  database: 'smart_bus'
});

module.exports = pool;