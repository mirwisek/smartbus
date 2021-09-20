require("dotenv").config();
const express = require("express");
const app = express();
const userRouter = require("./api/router");
const bodyparser = require("body-parser");
const formidable = require('express-formidable');
// const busyboy = require('busboy-express');

app.use(bodyparser.json());
app.use(bodyparser.urlencoded({ extended: true }));
app.use(express.json());
app.set('view engine', 'ejs');

app.use(formidable());
// app.use(busyboy.default())

app.use("/", userRouter);

const port = process.env.PORT || 3006;

app.listen(port, () => {
  console.log("Server up and running on PORT :", port);

  // Print ip address
  require('dns').lookup(require('os').hostname(), function (err, add, fam) {
    console.log('Send request at http://' + add + ':' + port + '/');
    global.address = add
    global.port = port
  })

});