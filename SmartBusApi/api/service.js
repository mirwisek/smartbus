// const { Json } = require("sequelize/types/lib/utils");
const pool = require("../config/database");
const tableRegistration = "registration"
const tableBuses = "buses"

getAllUsers = () => new Promise((resolve, reject) => {
  pool.query(
    `select r.email, r.username, r.usertype, b.busno from ${tableRegistration} AS r LEFT JOIN ${tableBuses} AS b ON r.email = b.email WHERE r.usertype != 'A' ORDER BY r.usertype DESC`,
    (error, results) => {
      if (error) {
        reject(error);
      } else {
        resolve(results);
      }
    }
  );
})

insertInUsers = (conn, data) => {
  return new Promise((resolve, reject) => {
    conn.query(
      `INSERT INTO ${tableRegistration} (email, username, password, usertype) VALUES (?, ?, ?, ?)`,
      [
        data.email,
        data.username,
        data.password,
        data.usertype
      ],
      (error, results) => {
        if (error)
          reject(error);
        resolve(results);
      }
    )
  });
};

insertInBuses = (conn, data) => {
  return new Promise((resolve, reject) => {
    conn.query(
      `INSERT INTO ${tableBuses} (email, currentloc, lastloc, busno) VALUES (?, null, null, ?)`,
      [
        data.email,
        data.busno
      ],

      (error, results) => {
        if (error)
          reject(error);
        resolve(results);
      }
    )
  });
};

function rejectRollback(conn, e, reject) {
  conn.rollback(function () {
    // console.log("Error: ", err)
  });
  conn.release()
  reject(e)
}

create = (data) => new Promise((resolve, reject) => {
  if (data.email == null || data.email == "" || data.password == null || data.password == "") {
    reject({
      error: " Email/Password is required"
    })
  } else {
    pool.getConnection((err, conn) => {
      if (err) reject({
        error: " Connection Error.."
      })
      conn.beginTransaction((err) => {
        if (err) reject(err);

        insertInUsers(conn, data).then((results) => {
          if (results.affectedRows > 0) {
            if (data.busno != null && data.busno != "") {
              // console.log("bus no: " + data.busno)
              insertInBuses(conn, data).then((results2) => {
                if (results2.affectedRows > 0) {
                  conn.commit()
                  let out2 = {
                    "affectedRows": results2.affectedRows
                  }
                  resolve(out2)
                } else {
                  let not2 = {
                    "affectedRows": results2.affectedRows
                  }
                  reject(not2)
                }
              }).catch((e2) => { rejectRollback(conn, e2, reject) })
            } else {
              conn.commit()
              let out1 = {
                "affectedRows": results.affectedRows
              }
              resolve(out1)
            }
          }
        }).catch((e1) => { rejectRollback(conn, e1, reject) })
      })
    })
  }
})

login = (data) => new Promise((resolve, reject) => {
  pool.query(
    `select email, username, usertype from ${tableRegistration} where email = ? AND password = ?`,
    [data.email,
    data.password
    ],
    (error, results) => {
      if (error) {
        reject(error);
      } else {
        resolve(results);
      }
    }
  );
})

updateBus = (data) => new Promise((resolve, reject) => {
  // console.log("email: " +data.email)
  getCurrentLoc(data).then((results) => {
    // console.log("result1: " + results)
    let loc = null
    // There might or might not be location returned by getCurrentLoc
    if(results.length > 0)
      loc = results[0].currentloc
    if(data.isonline || data.currentloc) {
      pool.query(
        `update ${tableBuses} set isonline=?, currentloc=?, lastloc=? where email = ?`,
        [

          data.isonline,
          data.currentloc,
          loc,
          data.email
        ],
        (error, results) => {
          if (error) {
            reject(error);
          } else {
            resolve(results);
          }
        });
    } else {
      reject('At least one field `isonline` or `currentloc` must be provided')
    }
  }).catch((err) => { reject(err) })
})

deleteUser = (data) => new Promise((resolve, reject) => {
  pool.query(
    `delete from ${tableRegistration} where email = ?`,
    [data.email],
    (error, results) => {
      if (error) {
        reject(error);
      } else {
        resolve(results);
      }
    });
})

deleteBus = (data) => new Promise((resolve, reject) => {
  pool.query(
    `delete from ${tableBuses} where email = ?`,
    [data.email],
    (error, results) => {
      if (error) {
        reject(error);
      } else {
        resolve(results);
      }
    });
})

doesUserExist = (data) => new Promise((resolve, reject) => {
  // console.log("data: "+JSON.stringify(data))
  pool.query(
    `select * from ${tableRegistration} where email = ? AND username != null`,
    [data.email],
    (error, results) => {
      if (error) {
        reject(error);
      } else {
        resolve(results);
      }
    }
  );
})

getCurrentLoc = (data) => new Promise((resolve, reject) => {
  if(data.email) {
    sql = `select currentloc from ${tableBuses} where email = ?`
    pool.query(
      sql,
      [data.email],
      (error, results) => {
        if (error) {
          reject(error);
        } else {
          // console.log("getloc: " + results)
          resolve(results);
        }
      });
  } else {
    reject('No email field provided')
  }
})

checkUserExist = (data) => new Promise((resolve, reject) => {
  // console.log("beforeS: "+JSON.stringify(data))

  pool.query(
    `select email, password from ${tableRegistration} where email = ?`,
    [data.email],
    (error, results) => {
      
      if (error) {
        reject(error);
      } else {
        resolve(results);
      }
    }
  );
})

updatepass = (data) => new Promise((resolve, reject) => {
  // console.log("Dataservice: "+JSON.stringify(data))
  pool.query(
    `update ${tableRegistration} set password=? where email = ?`,
    [
      data.password,
      data.email
    ],
    (error, results) => {
      if (error)
        reject(error);
      resolve(results);
    }
  )
})

getBusDetail = () => new Promise((resolve, reject) => {
  sql = `select b.email, b.currentloc, b.lastloc, b.busno, b.isonline, r.username from ${tableBuses} AS b INNER JOIN ${tableRegistration} AS r ON  b.email = r.email`
  pool.query(
    sql,
    [],
    (error, results) => {
      if (error) {
        reject(error);
      } else {
        resolve(results);
      }
    }
  );
})

module.exports = {
  getAllUsers,
  create,
  deleteUser,
  deleteBus,
  updateBus,
  doesUserExist,
  login,
  getCurrentLoc,
  checkUserExist,
  updatepass,
  getBusDetail
};