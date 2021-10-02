const router = require("express").Router();
// const util = require('util')
const { send, toJson } = require('../api/utils');


const {
  createUser,
  updateB,
  verifyUser,
  deleteUser,
  loginU,
  getBuses,
  postemail,
  postparams,
  getparams,
  getAccounts
} = require("./controller");

router.get('/getAccounts', (req, res) => {
  getAccounts().then((r) => {
    send(res, 200, null, r)
  }).catch((err) => {
    send(res, 500, err, null)
  })
});

router.post("/login", (req, res) => {
  loginU(req.fields).then( r => {

    if(r.length > 0 && r[0].is_verified == 0) {
      send(res, 401, 'User account has not been verified by admin', null)
    } else if(r.length > 0 && r[0].is_verified == 1) {
      send(res, 200, null, r[0])
    }else {
      send(res, 403, "Login Failed. Email/Password Is Incorrect", null)
    }
  }).catch((err) => {
    send(res, 400, err, null)
  })
});

router.patch("/verifyUser", (req, res) => {
  verifyU(req.fields).then((result) => {
    if (result.affectedRows > 0) {
      send(res, 200, null, "Verified Successful")
    } else {
      send(res, 403, null, "Verified Failed. No Email Found")
    }
  }).catch((err) => {
    send(res, 400, err, null)
  })
});

router.post("/createuser", (req, res) => {
  createUser(req.fields).then((result) => {
    if (result.affectedRows > 0) {
      send(res, 200, null, result)
    }
  }).catch((err) => {
    if (err.code == 'ER_DUP_ENTRY') {
      send(res, 304, err, null)
    } else
      send(res, 500, err, null)
  })
})

router.patch("/updatebus", (req, res) => {
  updateB(req.fields).then((result) => {
    if (result.affectedRows > 0) {
      send(res, 200, null, "Update Successful")
    } else {
      send(res, 403, null, "Update Failed. No Email Found")
    }
  }).catch((err) => {
    send(res, 500, err, null)
  })
});

router.post("/deleteUser", (req, res) => {
  deleteU(req.fields).then((r) => {
    if(r.affectedRows > 0) {
      send(res, 200, null, "Deleted Successfully")
    } else if(r.affectedRows == -100) {
      send(res, 403, 'No user found with the given email', null)
    } else {
      send(res, 400, 'Could not delete the user', null)
    }
  }).catch((err) => {
    send(res, 500, err, null)
  })
});

router.get('/', (req, res) => {
  res.send('Smart Bus is running!');
});

router.get('/forgot-password', (req, res) => {
  res.render('forgot-password')
});

router.post('/forgot-password', (req, res) => {
  postemail(req.fields).then((r) => {
    if (r.envelope != null) {
      send(res, 200, null, "Email Sent")
    } else {
      // console.log("R: " + JSON.stringify(r))
      send(res, 403, "Email Sending Error", null)
    }
  }).catch((err) => {
    console.log(err)
    send(res, 500, err, null)
  })
});

router.get('/reset-password/:email/:token', (req, res) => {
  getparams(req.params).then((r) => {
    if (r.length > 0) {
      res.render('reset-password', { email: r[0].email })
    } else {
      send(res, 403, "Page/Link Error", null)
    }
  }).catch((err) => {
    send(res, 500, err, null)
  })
});

router.post('/reset-password/:email/:token', (req, res) => {

  postparams(req.fields, req.params).then((r) => {
    if (r.affectedRows > 0) {
      send(res, 200, null, "Update Successfull...")
    } else {
      send(res, 403, "Update Failed...", null)
    }
  }).catch((err) => {
    send(res, 500, err, null)
  })
});

router.get('/getBuses', (req, res) => {
  getBuses().then((r) => {
    send(res, 200, null, r)
  }).catch((err) => {
    send(res, 500, err, null)
  })
});

module.exports = router;