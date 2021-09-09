const router = require("express").Router();
// const util = require('util')
const { send, toJson } = require('../api/utils');


const {
  createUser,
  getUsers,
  updateB,
  deleteU,
  loginU,
  getBuses,
  postemail,
  postparams,
  getparams
} = require("./controller");

router.get("/getbus", (req, res) => {
  getBus(req.fields).then((r) => {
    // console.log("r: " + r)
    if (r.length > 0) {
      send(res, 200, null, r[0])
    } else {
      send(res, 403, "No User/Location Found...", null)
    }
  }).catch((err) => {
    send(res, 500, err, null)
  })
});

router.post("/login", (req, res) => {
  loginU(req.fields).then((r) => {
    if (r.length > 0) {
      send(res, 200, null, r[0])
    } else {
      send(res, 403, "Login Failed. Email/Password Is Incorrect", null)
    }
  }).catch((err) => {
    send(res, 500, err, null)
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

router.delete("/deleteuser", (req, res) => {
  deleteU(req.body).then((r) => {
    send(r, 200, null, "Deleted Successfully")
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
  // console.log("beforeR: "+ JSON.stringify(req.fields))
  postemail(req.fields).then((r) => {
    // console.log("R: " + JSON.stringify(r.envelope))
    // console.log("Length: "+ JSON.stringify(r.length))
    if (r.envelope != null) {
      send(res, 200, null, "Email Sent")
    } else {
      // console.log("R: " + JSON.stringify(r))
      send(res, 403, "Email Sending Error", null)
    }
  }).catch((err) => {
    send(res, 500, err, null)
  })
});

router.get('/reset-password/:email/:token', (req, res) => {
  // console.log("paramsR: "+JSON.stringify(req.params))
  // data = {
  //   token: req.params,
  //   email
  // }
  getparams(req.params).then((r) => {
    // console.log("resultR: "+JSON.stringify(r))
    // console.log("resultR: "+r[0].email)

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